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
    private TerminalRepository terminalRepository; // Repositorio para obtener TerminalEntity

    // Crear o actualizar la configuración de un terminal
    public ConfigTerminalDTO createOrUpdateTerminalConfig(ConfigTerminalRequest request) {
        // Verificar si existe un terminal con el ID proporcionado
        TerminalEntity terminalEntity = terminalRepository.findById(request.getTerminalId())
                .orElseThrow(() -> new RuntimeException("Terminal not found with ID: " + request.getTerminalId()));

        // Verificar si ya existe una configuración para este terminal y este código
        TerminalConfigEntity existingConfig = terminalConfigRepository.findByTerminalIdAndCode(request.getTerminalId(), request.getCode());

        TerminalConfigEntity configEntityToSave;

        if (existingConfig != null) {
            // Actualizar la configuración existente
            existingConfig.setType(request.getType());
            existingConfig.setValue(request.getValue());
            existingConfig.setCreatedAt(LocalDateTime.now()); // Actualizamos la fecha de creación por si cambió
            existingConfig.setCreatedAtTz(LocalDateTime.now());

            configEntityToSave = existingConfig;
        } else {
            // Crear una nueva configuración si no existe
            configEntityToSave = TerminalConfigEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .terminalEntity(terminalEntity)  // Asignar la entidad TerminalEntity
                    .code(request.getCode())
                    .type(request.getType())
                    .value(request.getValue())
                    .createdAt(LocalDateTime.now())
                    .createdAtTz(LocalDateTime.now())
                    .build();
        }

        // Guardar o actualizar la configuración de terminal
        TerminalConfigEntity savedEntity = terminalConfigRepository.saveAndFlush(configEntityToSave);

        // Retornar el DTO del terminal guardado
        return terminalConfigMapper.mapToDTO(savedEntity);
    }

    // Obtener una configuración de terminal por terminalId y código
    public ConfigTerminalDTO getConfigTerminal(String terminalId, String code) {
        TerminalConfigEntity entity = terminalConfigRepository.findByTerminalIdAndCode(terminalId, code);
        if (entity == null) {
            throw new RuntimeException("No terminal config found with terminalId: " + terminalId + " and code: " + code);
        }
        return terminalConfigMapper.mapToDTO(entity);
    }

    // Obtener todas las configuraciones de un terminal por terminalId
    public List<ConfigTerminalDTO> getConfigsByTerminalId(String terminalId) {
        List<TerminalConfigEntity> entities = terminalConfigRepository.findByTerminalEntity_Id(terminalId);
        return terminalConfigMapper.mapToDTO(entities);
    }
}
