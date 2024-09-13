package com.superpay.config.mappers;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.dtos.requests.CommerceRequest;
import com.superpay.config.entity.CommerceEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommerceMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "terminals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CommerceEntity mapCommerceEntityToDTO(CommerceRequest request);

    @Named("mapCommerceEntToDTO")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommerceDTO mapDTOToCommerceEntity(CommerceEntity entity);

    /*
    @Named("mapCommerceEntListToDTOList")
    @IterableMapping(qualifiedByName = "mapCommerceEntToDTO")
    List<CommerceDTO> mapCommerces(List<CommerceEntity> commerceEntityList);
    */
}
