package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.TerminalRequest;
import com.superpay.config.entity.*;
import com.superpay.config.mappers.TerminalConfigMapper;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TerminalService {

    @Autowired
    TerminalMapper terminalMapper;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    CommerceRepository commerceRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    TerminalPaymentMethodRepository terminalPaymentMethodRepository;

    @Autowired
    TerminalConfigRepository terminalConfigRepository;

    @Autowired
    TerminalConfigMapper terminalConfigMapper;

    public TerminalDTO createOrUpdateTerminal(TerminalRequest terminalRequest) {
        TerminalEntity terminalEntityToSave = this.terminalMapper.mapDTOToTerminalEntity(terminalRequest);
        CommerceEntity commerceEntity = this.commerceRepository.getCommerceByIdOrNit(terminalRequest.getCommerceId());
        TerminalPaymentMethodEntity terminalPaymentMethodEntity = this.terminalPaymentMethodRepository.getTerminalPaymentMethodEntitiesById(terminalEntityToSave.getId());
        System.out.println("terminalPaymentMethodRepository: " + terminalPaymentMethodRepository);
        terminalEntityToSave.setCommerceEntity(commerceEntity);

        TerminalEntity terminalEntitySaved = this.terminalRepository.saveAndFlush(terminalEntityToSave);

        List<PaymentMethodEntity> paymentMethodEntities = new ArrayList<>();

        for (String paymentMethodId : terminalRequest.getPaymentMethods()) {
            PaymentMethodEntity paymentMethodEntity = this.paymentMethodRepository.getPaymentMethodEntitiesByid(terminalRequest.getPaymentMethods());

            if (paymentMethodEntity != null) {
                this.assignPaymentMethod(terminalEntitySaved.getId(), paymentMethodId);
                paymentMethodEntities.add(paymentMethodEntity);
            }
        }
        terminalEntitySaved.setPaymentMethods(new HashSet<>(paymentMethodEntities));
        //terminalEntitySaved.setConfigs();
        System.out.println("terminalEntitySaved: " + terminalEntitySaved);
        return this.terminalMapper.mapTerminalEntityToDTO(terminalEntitySaved);
    }

    public void assignPaymentMethod(String terminalId, String paymentMethodId) {
        TerminalEntity terminalEntity = this.terminalRepository.findById(terminalId).orElse(null);
        PaymentMethodEntity paymentMethodEntity = this.paymentMethodRepository.findById(paymentMethodId).orElse(null);

        if (terminalEntity != null && paymentMethodEntity != null) {
            TerminalPaymentMethodEntity terminalPaymentMethodEntity = TerminalPaymentMethodEntity
                    .builder()
                    .paymentMethodId(paymentMethodId)
                    .terminalId(terminalId)
                    .build();
            this.terminalPaymentMethodRepository.saveAndFlush(terminalPaymentMethodEntity);
        }
    }


    public List<TerminalDTO> getTerminalsByIdsOrCodes(ByIds byIds) {
        List<TerminalEntity> terminalEntities = this.terminalRepository.getTerminalsByIdsOrCodes(byIds.getIds());
        List<TerminalDTO> terminalDTOS = new ArrayList<>();
        for (TerminalEntity terminalEntity : terminalEntities) {
            List<TerminalPaymentMethodEntity> terminalAssigns = terminalPaymentMethodRepository.getTerminalPaymentMethodsByTerminalsIds(List.of(terminalEntity.getId()));
            List<String> paymentMethodsIds = terminalAssigns.stream().map(TerminalPaymentMethodEntity::getPaymentMethodId).distinct().toList();
            List<PaymentMethodEntity> paymentMethodEntities = this.paymentMethodRepository.findAllById(paymentMethodsIds);

            List<TerminalPaymentMethodEntity> terminalPaymentMethodEntities = terminalAssigns.stream().filter(terminalPaymentMethodEntity -> terminalPaymentMethodEntity.getTerminalId().equals(terminalEntity.getId())).toList();
            List<PaymentMethodEntity> terminalPaymentMethods = terminalPaymentMethodEntities.stream().map(terminalPaymentMethodEntity -> paymentMethodEntities.stream().filter(paymentMethodEntity -> paymentMethodEntity.getId().equals(terminalPaymentMethodEntity.getPaymentMethodId())).findFirst().orElse(null)).toList();
            terminalEntity.setPaymentMethods(new HashSet<>(terminalPaymentMethods));

            List<TerminalConfigEntity> terminalConfigEntities = this.terminalConfigRepository.getTerminalConfigs(terminalEntity.getId());
            List<ConfigTerminalDTO> terminalConfigs = this.terminalConfigMapper.map(terminalConfigEntities);

            TerminalDTO terminalDTO = this.terminalMapper.mapTerminalEntityToDTO(terminalEntity);
            terminalDTO.setConfigs(terminalConfigs);

            terminalDTOS.add(terminalDTO);
        }
        return terminalDTOS;
    }



}