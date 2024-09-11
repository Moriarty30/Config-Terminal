package com.superpay.config.service;

import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.entity.ConfigEntity;
import com.superpay.config.entity.PaymentMethodEntity;
import com.superpay.config.mappers.PaymentMethodMapper;
import com.superpay.config.repository.ConfigRepository;
import com.superpay.config.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    PaymentMethodMapper paymentMethodMapper;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    public ConfigEntity getConfig() {
        return this.configRepository.getConfig();
    }



    public ConfigEntity incrementCollaboratorPortalExternalIdSeq() {
        ConfigEntity configEntity = this.configRepository.getConfig();
        configEntity.setCollaboratorPortalExternalIdSeq(configEntity.getCollaboratorPortalExternalIdSeq() + 1);
        return this.configRepository.saveAndFlush(configEntity);
    }

    public List<PaymentMethodDTO> getTerminalPaymentMethods(String terminalId) {
        List<PaymentMethodEntity> paymentMethodEntityList = this.paymentMethodRepository.getTerminalPaymentMethods(terminalId);
        return this.paymentMethodMapper.mapPaymentMethods(paymentMethodEntityList);
    }
}
