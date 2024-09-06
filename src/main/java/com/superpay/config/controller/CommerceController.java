package com.superpay.config.controller;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.service.CommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commerce")
public class CommerceController {

    private final CommerceService commerceService;

    @Autowired
    public CommerceController(CommerceService commerceService) {
        this.commerceService = commerceService;
    }

    @PostMapping
    public ResponseEntity<CommerceDTO> createCommerce(@RequestBody CommerceDTO commerceDTO) {
        CommerceDTO createdCommerce = commerceService.createOrUpdateCommerce(commerceDTO);
        return ResponseEntity.ok(createdCommerce);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<CommerceDTO> getCommerceById(@RequestBody CommerceDTO commerceDTO) {
        CommerceDTO createdCommerce = commerceService.getCommerceById(commerceDTO);
        return ResponseEntity.ok(createdCommerce);
    }
    //Falta más metodods, findbyid, delete, etc
}
