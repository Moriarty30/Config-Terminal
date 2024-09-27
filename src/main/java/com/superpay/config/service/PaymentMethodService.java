package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.dtos.requests.PaymentMethodRequest;
import com.superpay.config.entity.PaymentMethodEntity;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.entity.TerminalPaymentMethodEntity;
import com.superpay.config.exception.CustomException;
import com.superpay.config.mappers.PaymentMethodMapper;
import com.superpay.config.repository.PaymentMethodRepository;
import com.superpay.config.repository.TerminalPaymentMethodRepository;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    @Autowired
    TerminalPaymentMethodRepository terminalPaymentMethodRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    PaymentMethodMapper paymentMethodMapper;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    public PaymentMethodDTO createOrUpdatePaymentMethod(PaymentMethodRequest paymentMethodRequest) {
        PaymentMethodEntity paymentMethodEntity = paymentMethodMapper.mapDTOToPaymentMethodEntity(paymentMethodRequest);
        if (paymentMethodEntity==null)
            throw new CustomException("Payment method not found with: " + paymentMethodRequest );
        PaymentMethodEntity savedPaymentMethod = paymentMethodRepository.save(paymentMethodEntity);
        return paymentMethodMapper.mapPaymentMethodEntityToDTO(savedPaymentMethod);
    }


    public List<PaymentMethodDTO> getPaymentMethodById(ByIds byIds) {
        List<PaymentMethodEntity> paymentMethodEntity = paymentMethodRepository.getTerminalPaymentMethods(byIds.getIds());
            if (paymentMethodEntity.isEmpty())
                throw new CustomException("Payment methods not found with ID: " + byIds.getIds());

        return paymentMethodMapper.mapPaymentMethodEntityToDTOs(paymentMethodEntity);
    }

    public List<PaymentMethodDTO> getPaymentMethodsByTerminalCode(String code) {
        TerminalEntity terminalEntity = terminalRepository.getTerminalByIdOrCode(code);
            if(terminalEntity == null)
                throw new CustomException("TerminalEntity not found with ID or Code: " + code);
        List<TerminalPaymentMethodEntity> terminalPaymentMethods = terminalPaymentMethodRepository
                .findByTerminalId(terminalEntity.getId());
        List<String> paymentMethodIds = terminalPaymentMethods.stream()
                .map(TerminalPaymentMethodEntity::getPaymentMethodId)
                .collect(Collectors.toList());
            if(paymentMethodIds.isEmpty())
                throw new CustomException("Payment methods not found with ID or Code: " + code);

        List<PaymentMethodEntity> paymentMethodEntities = paymentMethodRepository.findAllById(paymentMethodIds);
        return paymentMethodMapper.mapPaymentMethodEntityToDTOs(paymentMethodEntities);
    }



}
