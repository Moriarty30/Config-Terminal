package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.ConfigTerminalRequest;
import com.superpay.config.dtos.requests.TerminalRequest;
import com.superpay.config.entity.*;
import com.superpay.config.mappers.TerminalConfigMapper;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        CommerceEntity commerceEntity = this.commerceRepository.findById(terminalRequest.getCommerceId()).orElse(null);
        terminalEntityToSave.setCommerceEntity(commerceEntity);

        TerminalEntity terminalEntitySaved = this.terminalRepository.saveAndFlush(terminalEntityToSave);

        List<TerminalConfigEntity> terminalConfigEntities = new ArrayList<>();
        for(String configId : terminalRequest.getConfigs()){
            TerminalConfigEntity terminalConfigEntity = this.terminalConfigRepository.findById(configId).orElse(null);
            if(terminalConfigEntity != null){
                this.assignTerminalConfig(terminalEntitySaved.getId(), configId);
                terminalConfigEntities.add(terminalConfigEntity);
            }
        }
        List<PaymentMethodEntity> paymentMethodEntities = new ArrayList<>();
        for (String paymentMethodId : terminalRequest.getPaymentMethods()) {
            PaymentMethodEntity paymentMethodEntity = this.paymentMethodRepository.findById(paymentMethodId).orElse(null);
            if (paymentMethodEntity != null) {
                this.assignPaymentMethod(terminalEntitySaved.getId(), paymentMethodId);
                paymentMethodEntities.add(paymentMethodEntity);
            }
        }
        terminalEntitySaved.setPaymentMethods(paymentMethodEntities);

        terminalEntitySaved.setConfigs(terminalConfigEntities);



        TerminalDTO test= this.terminalMapper.mapTerminalEntityToDTO(terminalEntitySaved);
        return test;
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

    public void assignTerminalConfig(String terminalId, String configId) {

        TerminalEntity terminalEntity = this.terminalRepository.findById(terminalId).orElse(null);
        TerminalConfigEntity terminalConfigEntity = this.terminalConfigRepository.findById(configId).orElse(null);

        if (terminalEntity != null && terminalConfigEntity != null) {
            terminalConfigEntity.setTerminalEntity(terminalEntity);
            this.terminalConfigRepository.saveAndFlush(terminalConfigEntity);
        } else {
            if (terminalEntity == null) {
                throw new RuntimeException("Terminal not found with ID: " + terminalId);
            }
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
            terminalEntity.setPaymentMethods(terminalPaymentMethods);

            List<TerminalConfigEntity> terminalConfigEntities = this.terminalConfigRepository.getTerminalConfigs(terminalEntity.getId());
            List<ConfigTerminalDTO> terminalConfigs = this.terminalConfigMapper.map(terminalConfigEntities);

            TerminalDTO terminalDTO = this.terminalMapper.mapTerminalEntityToDTO(terminalEntity);
            terminalDTO.setConfigs(terminalConfigs);

            terminalDTOS.add(terminalDTO);
        }
        return terminalDTOS;
    }

}