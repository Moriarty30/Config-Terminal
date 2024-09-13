package com.superpay.config.service;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.requests.ConfigTerminalRequest;
import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.mappers.TerminalConfigMapper;
import com.superpay.config.repository.TerminalConfigRepository;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TerminalConfigService {

    @Autowired
    private TerminalConfigRepository terminalConfigRepository;

    @Autowired
    private TerminalConfigMapper terminalConfigMapper;

    @Autowired
    private TerminalRepository terminalRepository;


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

    public List<ConfigTerminalDTO> getConfigsByTerminalId(String terminalId) {
        List<TerminalConfigEntity> entities = terminalConfigRepository.findByTerminalEntity_Id(terminalId);
        return terminalConfigMapper.mapToDTO(entities);
    }
}
