package com.superpay.config.service;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.ConfigTerminalRequest;
import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.exception.CustomException;
import com.superpay.config.mappers.TerminalConfigMapper;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.TerminalConfigRepository;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TerminalConfigService {

    @Autowired
    private TerminalConfigRepository terminalConfigRepository;

    @Autowired
    private TerminalConfigMapper terminalConfigMapper;

    @Autowired
    private TerminalRepository terminalRepository;

    @Autowired
    private TerminalMapper terminalMapper;


public ConfigTerminalDTO createOrUpdateTerminalConfig(ConfigTerminalRequest request) {
    TerminalEntity terminalEntity;
    try {
        terminalEntity = terminalRepository.findById(request.getTerminalId())
                .orElseThrow(() -> new CustomException("Terminal not found with ID: " + request.getTerminalId()));
    } catch (Exception e) {
        throw new CustomException("Error retrieving terminal: " + e.getMessage());
    }

    TerminalConfigEntity existingConfig;
    try {
        existingConfig = terminalConfigRepository.findByTerminalIdAndCode(request.getTerminalId(), request.getCode());
    } catch (Exception e) {
        throw new CustomException("Error retrieving existing config: " + e.getMessage());
    }

    TerminalConfigEntity configEntityToSave;
    if (existingConfig != null) {
        existingConfig.setType(request.getType());
        existingConfig.setValue(request.getValue());
        existingConfig.setCreatedAt(LocalDateTime.now());
        existingConfig.setCreatedAtTz(LocalDateTime.now());
        configEntityToSave = existingConfig;
    } else {
        configEntityToSave = TerminalConfigEntity.builder()
                .id(UUID.randomUUID().toString())
                .terminalEntity(terminalEntity)
                .code(request.getCode())
                .type(request.getType())
                .value(request.getValue())
                .createdAt(LocalDateTime.now())
                .createdAtTz(LocalDateTime.now())
                .build();
    }

    TerminalConfigEntity savedEntity;
    try {
        savedEntity = terminalConfigRepository.saveAndFlush(configEntityToSave);
    } catch (Exception e) {
        throw new CustomException("Error saving terminal config: " + e.getMessage());
    }

    return terminalConfigMapper.mapToDTO(savedEntity);
}


    public List<ConfigTerminalDTO> getConfigTerminal(String IdorCode) {
        List<TerminalConfigEntity> entity = terminalConfigRepository.findByTerminalIdOrCode(IdorCode);
        if (entity.isEmpty()) {
            throw new RuntimeException("No terminal config found with " + IdorCode);
        }
        return terminalConfigMapper.mapToDTO(entity);
    }

    /*
    public List<TerminalDTO> getTerminalsByConfigIdorCode(String configIdorcode) {
        List<TerminalConfigEntity> configEntities = terminalConfigRepository.findByTerminalIdOrCode(configIdorcode);
        if (configEntities.isEmpty()) {
            throw new RuntimeException("No terminal config found with " + configIdorcode);
        }
        List<TerminalEntity> terminalEntities = configEntities.stream()
                .flatMap(configEntity -> terminalRepository.findByTerminalConfigs_Id(configEntity.getTerminalEntity().getId()).stream())
                .toList();
        return terminalEntities.stream()
                .map(terminalMapper::mapTerminalEntityToDTO)
                .collect(Collectors.toList());
    }
    */
}