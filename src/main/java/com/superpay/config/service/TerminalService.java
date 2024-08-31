package com.superpay.config.service;

import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.CreateTerminalRequest;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.exception.CustomException;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TerminalService {

    @Autowired
    private TerminalRepository terminalRepository;

    @Autowired
    private TerminalMapper terminalMapper;

    public List<TerminalEntity> getAllTerminals() {
        return terminalRepository.findAll();
    }

    public List<TerminalEntity> getTerminalsByIds(List<String> ids) {
        return terminalRepository.findAllById(ids);
    }

    public TerminalEntity getTerminalById(String idOrName) throws CustomException {
        TerminalEntity terminalEntity = terminalRepository.findByIdOrName(idOrName);
        if (terminalEntity != null) {
            return terminalEntity;
        } else {
            throw CustomException.builder()
                    .code("CONF_0001")
                    .message("Terminal not found")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    public TerminalDTO createOrUpdateTerminal(CreateTerminalRequest createTerminalRequest) {
        TerminalEntity terminalEntityToSave = this.terminalMapper.mapCreateTerminalRequestToEnt(createTerminalRequest);
        TerminalEntity terminalEntitySaved = terminalRepository.saveAndFlush(terminalEntityToSave);
        return this.terminalMapper.mapTerminalEntToDTO(terminalEntitySaved);
    }

    public void deleteTerminal(String id) {
        terminalRepository.deleteById(id);
    }
}
