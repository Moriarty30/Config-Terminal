package com.superpay.config.mappers;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.entity.CommerceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommerceMapper {

    // Mapea de CommerceEntity a CommerceDTO
    CommerceDTO mapCommerceEntityToDTO(CommerceEntity commerceEntity);

    // Mapea de CommerceDTO a CommerceEntity
    CommerceEntity mapDTOToCommerceEntity(CommerceDTO commerceDTO);
}
