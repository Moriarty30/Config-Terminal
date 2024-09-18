package com.superpay.config.mappers;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.entity.TerminalConfigEntity;
import org.mapstruct.*;


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


    @Mapping(source = "terminalEntity.id", target = "terminalId")
    ConfigTerminalDTO mapToDTO(TerminalConfigEntity entity);


    @Mapping(source = "terminalEntity.id", target = "terminalId")
    List<ConfigTerminalDTO> mapToDTO(List<TerminalConfigEntity> entities);

}