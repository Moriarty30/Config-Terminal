package com.superpay.config.mappers;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.entity.TerminalConfigEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TerminalConfigMapper {

    @Named("mapTerminalConfigEntToDTO")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "terminalId", source = "entity.terminalEntity.id")
    ConfigTerminalDTO map(TerminalConfigEntity entity);

    @Named("mapTerminalConfigsEntToDTO")
    @IterableMapping(qualifiedByName = "mapTerminalConfigEntToDTO")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<ConfigTerminalDTO> map(List<TerminalConfigEntity> configEntities);

    ConfigTerminalDTO getTerminalConfigByid(String id);
}a