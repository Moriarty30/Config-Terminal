package com.superpay.config.service;

import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<TerminalDTO> getTerminalsByIds(Set<String> ids) {
        List<TerminalEntity> terminals = terminalRepository.findAllById(ids);
        return terminals.stream()
                .map(terminalMapper::mapTerminalEntityToDTO)
                .collect(Collectors.toList());
    }
}
