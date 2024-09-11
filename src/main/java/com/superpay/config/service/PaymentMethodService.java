package com.superpay.config.service;

import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.entity.PaymentMethodEntity;
import com.superpay.config.mappers.PaymentMethodMapper;
import com.superpay.config.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    public PaymentMethodDTO createOrUpdatePaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodEntity paymentMethodEntity = paymentMethodMapper.mapDTOToPaymentMethodEntity(paymentMethodDTO);
        PaymentMethodEntity savedPaymentMethod = paymentMethodRepository.save(paymentMethodEntity);
        return paymentMethodMapper.mapPaymentMethodEntityToDTO(savedPaymentMethod);
    }


    public PaymentMethodDTO getPaymentMethodById(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(paymentMethodDTO.getId()).orElse(null);
        return paymentMethodMapper.mapPaymentMethodEntityToDTO(paymentMethodEntity);
    }

}
