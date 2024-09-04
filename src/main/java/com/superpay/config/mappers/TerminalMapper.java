package com.superpay.config.mappers;

import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.entity.TerminalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TerminalMapper {

    @Mapping(source = "commerceEntity.id", target = "merchantId")
    @Mapping(source = "code", target = "code")
    TerminalDTO mapTerminalEntityToDTO(TerminalEntity terminalEntity);

    @Mapping(source = "merchantId", target = "commerceEntity.id")
    TerminalEntity mapDTOToTerminalEntity(TerminalDTO terminalDTO);
}
