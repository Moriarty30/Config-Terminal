package com.superpay.config.mappers;

import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.entity.PaymentMethodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface PaymentMethodMapper {
    @Mapping(source = "paymentMethodEntity.id", target = "id")
    @Mapping(source = "code", target = "code")
    PaymentMethodDTO mapPaymentMethodEntityToDTO(PaymentMethodEntity paymentMethodEntity);

    PaymentMethodEntity mapDTOToPaymentMethodEntity(PaymentMethodDTO paymentMethodDTO);
}
