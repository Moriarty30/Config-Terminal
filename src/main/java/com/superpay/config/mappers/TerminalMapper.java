package com.superpay.config.mappers;

import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.entity.TerminalEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;



@Mapper(componentModel = "spring")
public interface TerminalMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "merchantId", source = "commerceEntity.id")
    TerminalDTO mapTerminalEntityToDTO(TerminalEntity terminalEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    TerminalEntity mapDTOToTerminalEntity(TerminalDTO terminalDTO);
}
