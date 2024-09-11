package com.superpay.config.service;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.dtos.requests.CommerceRequest;
import com.superpay.config.entity.CommerceEntity;
import com.superpay.config.entity.PaymentMethodEntity;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.entity.TerminalPaymentMethodEntity;
import com.superpay.config.mappers.CommerceMapper;
import com.superpay.config.repository.CommerceRepository;
import com.superpay.config.repository.PaymentMethodRepository;
import com.superpay.config.repository.TerminalPaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.HashSet;

import java.util.stream.Collectors;

@Service
public class CommerceService {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    private final CommerceRepository commerceRepository;
    private final CommerceMapper commerceMapper;
    @Autowired
    private TerminalPaymentMethodRepository terminalPaymentMethodRepository;

    @Autowired
    public CommerceService(CommerceRepository commerceRepository, CommerceMapper commerceMapper) {
        this.commerceRepository = commerceRepository;
        this.commerceMapper = commerceMapper;
    }

    public CommerceDTO createOrUpdateCommerce(CommerceRequest CommerceRequest) {
        CommerceEntity commerceEntityToSave = this.commerceMapper.mapCommerceEntityToDTO(CommerceRequest);
        CommerceEntity commerceEntitySaved = this.commerceRepository.saveAndFlush(commerceEntityToSave);
        return this.commerceMapper.mapDTOToCommerceEntity(commerceEntitySaved);
    }

    /*
    public CommerceDTO getCommerceByIdOrNit(String commerceIdOrNit) {
        CommerceEntity commerceEntity = this.commerceRepository.getCommerceByIdOrNit(commerceIdOrNit);
        if (commerceEntity != null) {
            List<String> terminalsIds = commerceEntity.getTerminals().stream().map(TerminalEntity::getId).toList();
            TerminalPaymentMethodEntity terminalAssigns = terminalPaymentMethodRepository.getTerminalPaymentMethodsByTerminalsIds(terminalsIds.toString());
            List<String> paymentMethodsIds = terminalAssigns.stream().map(TerminalPaymentMethodEntity::getPaymentMethodId).distinct().toList();
            List<PaymentMethodEntity> paymentMethodEntities = this.paymentMethodRepository.findAllById(paymentMethodsIds);

            for (TerminalEntity terminal : commerceEntity.getTerminals()) {
                List<TerminalPaymentMethodEntity> terminalPaymentMethodEntities = terminalAssigns.stream().filter(terminalPaymentMethodEntity -> terminalPaymentMethodEntity.getTerminalId().equals(terminal.getId())).toList();
                List<PaymentMethodEntity> terminalPaymentMethods = terminalPaymentMethodEntities.stream().map(terminalPaymentMethodEntity -> paymentMethodEntities.stream().filter(paymentMethodEntity -> paymentMethodEntity.getId().equals(terminalPaymentMethodEntity.getPaymentMethodId())).findFirst().orElse(null)).collect(Collectors.toList());
                terminal.setPaymentMethods(new HashSet<>(terminalPaymentMethods));
            }

            return this.commerceMapper.mapDTOToCommerceEntity(commerceEntity);
        }
        return null;
    }

     */

}
