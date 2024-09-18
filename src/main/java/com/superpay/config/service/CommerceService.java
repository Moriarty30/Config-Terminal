package com.superpay.config.service;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.CommerceRequest;
import com.superpay.config.entity.CommerceEntity;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.mappers.CommerceMapper;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.CommerceRepository;
import com.superpay.config.repository.PaymentMethodRepository;
import com.superpay.config.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CommerceService {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    CommerceRepository commerceRepository;

    @Autowired
    CommerceMapper commerceMapper;

    @Autowired
    TerminalMapper terminalMapper;

    @Autowired
    public CommerceService(CommerceRepository commerceRepository, CommerceMapper commerceMapper) {
        this.commerceRepository = commerceRepository;
        this.commerceMapper = commerceMapper;
    }

    public CommerceDTO createOrUpdateCommerce(CommerceRequest CommerceRequest) {
        CommerceEntity commerceEntityToSave = this.commerceMapper.mapCommerceEntityToDTO(CommerceRequest);
        CommerceEntity commerceEntitySaved = this.commerceRepository.saveAndFlush(commerceEntityToSave);
        return this.commerceMapper.mapDTOToCommerceEntity(commerceEntitySaved);
    }

    public CommerceDTO getCommerceByIdOrNit(String commerceIdOrNit) {
        CommerceEntity commerceEntities = this.commerceRepository.getCommerceByIdOrNit(commerceIdOrNit);
        if(commerceEntities == null) {
            throw new RuntimeException("Commerce not found with ID or NIT: " + commerceIdOrNit);
        }
        List<TerminalEntity> terminalEntity = this.terminalRepository.findByComerceId(commerceEntities.getId());
        List<TerminalDTO> terminalDTO = this.terminalMapper.mapsTerminalEntityToDTO(terminalEntity);
        CommerceDTO commerceDTO = this.commerceMapper.mapDTOToCommerceEntity(commerceEntities);
        commerceDTO.setTerminals(terminalDTO);
        return commerceDTO;
    }




}
