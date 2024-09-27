package com.superpay.config.controller;

import com.superpay.config.dtos.CommerceDTO;
import com.superpay.config.dtos.requests.CommerceRequest;
import com.superpay.config.entity.CommerceEntity;
import com.superpay.config.service.CommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commerce")
public class CommerceController {

    @Autowired
    CommerceService commerceService;

    @PostMapping("/NewCommerce")
    @ResponseStatus(HttpStatus.CREATED)
    public CommerceDTO saveCommerce(@RequestBody CommerceRequest commerceRequest) {
        return this.commerceService.createOrUpdateCommerce(commerceRequest);
    }


    @GetMapping("/{commerceIdOrNit}")
    @ResponseStatus(HttpStatus.OK)
    public CommerceDTO getCommerceByIdOrNit(@PathVariable String commerceIdOrNit) {
        return this.commerceService.getTerminalByCommerce(commerceIdOrNit);
    }

    @GetMapping("/ListCommerce/{commerceIdOrNit}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommerceDTO> getCommerceByNitorId(@PathVariable String commerceIdOrNit) {
        return this.commerceService.getCommerceByNitOrId(commerceIdOrNit);
    }


}
