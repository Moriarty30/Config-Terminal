package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TerminalService {

    private final TerminalRepository terminalRepository;
    private final TerminalMapper terminalMapper;

    @Autowired
    public TerminalService(TerminalRepository terminalRepository, TerminalMapper terminalMapper) {
        this.terminalRepository = terminalRepository;
        this.terminalMapper = terminalMapper;
    }

    public TerminalDTO createOrUpdateTerminal(TerminalDTO terminalDTO) {
        TerminalEntity terminalEntity = terminalMapper.mapDTOToTerminalEntity(terminalDTO);
        TerminalEntity savedTerminal = terminalRepository.save(terminalEntity);
        return terminalMapper.mapTerminalEntityToDTO(savedTerminal);
    }

    public Set<TerminalDTO> getTerminalsByIds(ByIds byIds) {
        Set<String> ids = new HashSet<>(byIds.getIds());
        return terminalRepository.findByIdIn(ids)
                .stream()
                .map(terminalMapper::mapTerminalEntityToDTO)
                .collect(Collectors.toSet());
    }
}
