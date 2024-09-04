package com.superpay.config.service;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.entity.CommerceEntity;
import com.superpay.config.mappers.CommerceMapper;
import com.superpay.config.repository.CommerceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommerceService {

    private final CommerceRepository commerceRepository;
    private final CommerceMapper commerceMapper;

    @Autowired
    public CommerceService(CommerceRepository commerceRepository, CommerceMapper commerceMapper) {
        this.commerceRepository = commerceRepository;
        this.commerceMapper = commerceMapper;
    }

    public CommerceDTO createOrUpdateCommerce(CommerceDTO commerceDTO) {
        CommerceEntity commerceEntity = commerceMapper.mapDTOToCommerceEntity(commerceDTO);
        CommerceEntity savedCommerce = commerceRepository.save(commerceEntity);
        return commerceMapper.mapCommerceEntityToDTO(savedCommerce);
    }

    //agregar delete
}
