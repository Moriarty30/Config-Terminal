package com.superpay.config.mappers;

import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.CreateTerminalRequest;
import com.superpay.config.entity.TerminalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses =  {TerminalMapper.class})
public interface TerminalMapper {

    @Named("mapCreateTerminalRequestToEnt")
    @Mapping(target = "id", ignore = true)
        //@Mapping(target = "active", source = "request.active")
        //@Mapping(target = "name", source = "request.name")
        // @Mapping(target = "location", source = "request.location")
    TerminalEntity mapCreateTerminalRequestToEnt(CreateTerminalRequest request);

    @Named("mapTerminalEntToDTO")
    TerminalDTO mapTerminalEntToDTO(TerminalEntity entity);
}
