package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.entity.PaymentMethodEntity;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.entity.TerminalPaymentMethodEntity;
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

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    @Autowired
    private TerminalRepository terminalRepository;

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


    public List<PaymentMethodDTO> getPaymentMethodById(ByIds byIds) {
        List<PaymentMethodEntity> paymentMethodEntity = paymentMethodRepository.getTerminalPaymentMethods(byIds.getIds());

        return paymentMethodMapper.mapPaymentMethodEntityToDTOs(paymentMethodEntity);
    }

    public List<PaymentMethodDTO> getPaymentMethodsByTerminalCode(String code) {
        TerminalEntity terminalEntity = terminalRepository.getTerminalByIdOrCode(code);

        List<TerminalPaymentMethodEntity> terminalPaymentMethods = terminalPaymentMethodRepository
                .findByTerminalId(terminalEntity.getId());

        List<String> paymentMethodIds = terminalPaymentMethods.stream()
                .map(TerminalPaymentMethodEntity::getPaymentMethodId)
                .collect(Collectors.toList());

        List<PaymentMethodEntity> paymentMethodEntities = paymentMethodRepository.findAllById(paymentMethodIds);
        return paymentMethodMapper.mapPaymentMethodEntityToDTOs(paymentMethodEntities);
    }



}
