package com.superpay.config.mappers;

import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.entity.PaymentMethodEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")

public interface PaymentMethodMapper {
    @Mapping(source = "paymentMethodEntity.id", target = "id")
    @Mapping(source = "code", target = "code")
    PaymentMethodDTO mapPaymentMethodEntityToDTO(PaymentMethodEntity paymentMethodEntity);

    PaymentMethodEntity mapDTOToPaymentMethodEntity(PaymentMethodDTO paymentMethodDTO);

    /*@Named("mapPaymentMethodsEntToDTO")
    @IterableMapping(qualifiedByName = "mapPaymentMethodEntToDTO")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    */
    List<PaymentMethodDTO> mapPaymentMethods(List<PaymentMethodEntity> entities);

}
