package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.TerminalRequest;
import com.superpay.config.entity.*;
import com.superpay.config.exception.CustomException;
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

        CommerceEntity commerceEntity = this.commerceRepository.findById(terminalRequest.getCommerceId())
                .orElseThrow(() -> new CustomException("Commerce not found with ID: " + terminalRequest.getCommerceId()));
        terminalEntityToSave.setCommerceEntity(commerceEntity);

        TerminalEntity terminalEntitySaved = this.terminalRepository.saveAndFlush(terminalEntityToSave);

        List<TerminalConfigEntity> terminalConfigEntities = new ArrayList<>();
        for (String configId : terminalRequest.getConfigs()) {
            TerminalConfigEntity terminalConfigEntity = this.terminalConfigRepository.findById(configId)
                    .orElseThrow(() -> new CustomException("Config not found with ID: " + configId));
            this.assignTerminalConfig(terminalEntitySaved.getId(), configId);
            terminalConfigEntities.add(terminalConfigEntity);
        }

        List<PaymentMethodEntity> paymentMethodEntities = new ArrayList<>();
        for (String paymentMethodId : terminalRequest.getPaymentMethods()) {
            PaymentMethodEntity paymentMethodEntity = this.paymentMethodRepository.findById(paymentMethodId)
                    .orElseThrow(() -> new CustomException("Payment method not found with ID: " + paymentMethodId));
            this.assignPaymentMethod(terminalEntitySaved.getId(), paymentMethodId);
            paymentMethodEntities.add(paymentMethodEntity);
        }
        terminalEntitySaved.setPaymentMethods(paymentMethodEntities);
        terminalEntitySaved.setConfigs(terminalConfigEntities);

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
        List<TerminalEntity> terminalEntities;
        try {
            terminalEntities = this.terminalRepository.getTerminalsByIdsOrCodes(byIds.getIds());
        } catch (Exception e) {
            throw new CustomException("Error retrieving terminals by IDs or codes: " + e.getMessage());
        }

        if (terminalEntities.isEmpty()) {
            throw new CustomException("No terminals found with the provided IDs or codes.");
        }

        List<TerminalDTO> terminalDTOS = new ArrayList<>();
        for (TerminalEntity terminalEntity : terminalEntities) {
            List<TerminalPaymentMethodEntity> terminalAssigns;
            try {
                terminalAssigns = terminalPaymentMethodRepository.getTerminalPaymentMethodsByTerminalsIds(List.of(terminalEntity.getId()));
            } catch (Exception e) {
                throw new CustomException("Error retrieving payment methods for terminal ID: " + terminalEntity.getId() + ". " + e.getMessage());
            }

            List<String> paymentMethodsIds = terminalAssigns.stream().map(TerminalPaymentMethodEntity::getPaymentMethodId).distinct().toList();
            List<PaymentMethodEntity> paymentMethodEntities;
            try {
                paymentMethodEntities = this.paymentMethodRepository.findAllById(paymentMethodsIds);
            } catch (Exception e) {
                throw new CustomException("Error retrieving payment method entities: " + e.getMessage());
            }

            List<TerminalPaymentMethodEntity> terminalPaymentMethodEntities = terminalAssigns.stream().filter(terminalPaymentMethodEntity -> terminalPaymentMethodEntity.getTerminalId().equals(terminalEntity.getId())).toList();
            List<PaymentMethodEntity> terminalPaymentMethods = terminalPaymentMethodEntities.stream().map(terminalPaymentMethodEntity -> paymentMethodEntities.stream().filter(paymentMethodEntity -> paymentMethodEntity.getId().equals(terminalPaymentMethodEntity.getPaymentMethodId())).findFirst().orElse(null)).toList();
            terminalEntity.setPaymentMethods(terminalPaymentMethods);

            List<TerminalConfigEntity> terminalConfigEntities;
            try {
                terminalConfigEntities = this.terminalConfigRepository.getTerminalConfigs(terminalEntity.getId());
            } catch (Exception e) {
                throw new CustomException("Error retrieving terminal configs for terminal ID: " + terminalEntity.getId() + ". " + e.getMessage());
            }

            List<ConfigTerminalDTO> terminalConfigs = this.terminalConfigMapper.map(terminalConfigEntities);

            TerminalDTO terminalDTO = this.terminalMapper.mapTerminalEntityToDTO(terminalEntity);
            terminalDTO.setConfigs(terminalConfigs);

            terminalDTOS.add(terminalDTO);
        }
        return terminalDTOS;
    }
}