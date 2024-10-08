package com.superpay.config.mappers;

import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.dtos.requests.PaymentMethodRequest;
import com.superpay.config.entity.PaymentMethodEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")

public interface PaymentMethodMapper {
    @Mapping(source = "paymentMethodEntity.id", target = "id")
    @Mapping(source = "code", target = "code")
    PaymentMethodDTO mapPaymentMethodEntityToDTO(PaymentMethodEntity paymentMethodEntity);

    @Mapping(source = "paymentMethodEntity.id", target = "id")
    @Mapping(source = "code", target = "code")
    List<PaymentMethodDTO> mapPaymentMethodEntityToDTOs(List <PaymentMethodEntity> paymentMethodEntity);

    PaymentMethodEntity mapDTOToPaymentMethodEntity(PaymentMethodRequest patmentMethodRequest);

   // List<PaymentMethodDTO> toDtoList(List<PaymentMethodEntity> paymentMethodEntities);
}
