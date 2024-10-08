package com.superpay.config.service;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.ConfigTerminalRequest;
import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.entity.TerminalEntity;
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


    //Metodo para crear una configuración en caso de que no exista, o actualizarla en caso de que ya exista
    public ConfigTerminalDTO createOrUpdateTerminalConfig(ConfigTerminalRequest request) {

        TerminalEntity terminalEntity = terminalRepository.findById(request.getTerminalId()).orElse(null);
        TerminalConfigEntity existingConfig = terminalConfigRepository.findByTerminalIdAndCode(request.getTerminalId(), request.getCode());

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
        TerminalConfigEntity savedEntity = terminalConfigRepository.saveAndFlush(configEntityToSave);

        return terminalConfigMapper.mapToDTO(savedEntity);
    }


    public ConfigTerminalDTO getConfigTerminal(String terminalId, String code) {
        TerminalConfigEntity entity = terminalConfigRepository.findByTerminalIdAndCode(terminalId, code);
        if (entity == null) {
            throw new RuntimeException("No terminal config found with terminalId: " + terminalId + " and code: " + code);
        }
        return terminalConfigMapper.mapToDTO(entity);
    }

    public List<TerminalDTO> getTerminalsByConfigId(String configId) {
        TerminalConfigEntity configEntity = terminalConfigRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found with ID: " + configId));
        //String terminalId = configEntity.getTerminalEntity().getId();
        //System.out.println("TERMINAL ID"+ terminalId);
        List<TerminalEntity> terminalEntities = terminalRepository.findByTerminalConfigs_Id(configEntity.getTerminalEntity().getId());
        return terminalEntities.stream()
                .map(terminalMapper::mapTerminalEntityToDTO)
                .collect(Collectors.toList());
    }

}