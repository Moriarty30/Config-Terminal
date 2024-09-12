package com.superpay.config.service;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.TerminalRequest;
import com.superpay.config.entity.*;
import com.superpay.config.mappers.TerminalConfigMapper;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TerminalService {

    @Autowired
    TerminalMapper terminalMapper;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    CommerceRepository commerceRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    TerminalPaymentMethodRepository terminalPaymentMethodRepository;

    @Autowired
    TerminalConfigRepository terminalConfigRepository;

    @Autowired
    TerminalConfigMapper terminalConfigMapper;

    public TerminalDTO createOrUpdateTerminal(TerminalRequest terminalRequest) {
        // Mapear TerminalRequest a TerminalEntity
        TerminalEntity terminalEntityToSave = this.terminalMapper.mapDTOToTerminalEntity(terminalRequest);

        // Buscar el CommerceEntity relacionado
        CommerceEntity commerceEntity = this.commerceRepository.getCommerceByIdOrNit(terminalRequest.getCommerceId());
        if (commerceEntity == null) {
            throw new RuntimeException("Commerce not found with ID: " + terminalRequest.getCommerceId());
        }
        // Asignar el CommerceEntity al TerminalEntity
        terminalEntityToSave.setCommerceEntity(commerceEntity);
        // Guardar el TerminalEntity en la base de datos
        TerminalEntity terminalEntitySaved = this.terminalRepository.saveAndFlush(terminalEntityToSave);
        // Gestionar la asignación de métodos de pago
        List<PaymentMethodEntity> paymentMethodEntities = assignPaymentMethods(terminalEntitySaved, terminalRequest.getPaymentMethods());
        // Asignar los métodos de pago al TerminalEntity guardado
        terminalEntitySaved.setPaymentMethods(new HashSet<>(paymentMethodEntities));

        // Guardar nuevamente el TerminalEntity con los métodos de pago
        this.terminalRepository.saveAndFlush(terminalEntitySaved);

        // Mapear el TerminalEntity guardado a un TerminalDTO y devolverlo
        return this.terminalMapper.mapTerminalEntityToDTO(terminalEntitySaved);
    }

    private List<PaymentMethodEntity> assignPaymentMethods(TerminalEntity terminalEntity, List<String> paymentMethodIds) {
        List<PaymentMethodEntity> paymentMethodEntities = new ArrayList<>();

        for (String paymentMethodId : paymentMethodIds) {
            PaymentMethodEntity paymentMethodEntity = this.paymentMethodRepository.findById(paymentMethodId)
                    .orElseThrow(() -> new RuntimeException("Payment Method not found with ID: " + paymentMethodId));


            boolean alreadyAssigned = this.terminalPaymentMethodRepository.existsByTerminalIdAndPaymentMethodId(terminalEntity.getId(), paymentMethodId);
            if (!alreadyAssigned) {

                TerminalPaymentMethodEntity terminalPaymentMethodEntity = TerminalPaymentMethodEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .terminalId(terminalEntity.getId())
                        .paymentMethodId(paymentMethodId)
                        .build();

                // Guardar la nueva relación
                this.terminalPaymentMethodRepository.saveAndFlush(terminalPaymentMethodEntity);
            }

            // Añadir el método de pago a la lista de métodos
            paymentMethodEntities.add(paymentMethodEntity);
        }

        return paymentMethodEntities;
    }



    public List<TerminalDTO> getTerminalsByIdsOrCodes(ByIds byIds) {
        List<TerminalEntity> terminalEntities = this.terminalRepository.getTerminalsByIdsOrCodes(byIds.getIds());
        List<TerminalDTO> terminalDTOS = new ArrayList<>();
        for (TerminalEntity terminalEntity : terminalEntities) {
            List<TerminalPaymentMethodEntity> terminalAssigns = terminalPaymentMethodRepository.getTerminalPaymentMethodsByTerminalsIds(List.of(terminalEntity.getId()));
            List<String> paymentMethodsIds = terminalAssigns.stream().map(TerminalPaymentMethodEntity::getPaymentMethodId).distinct().toList();
            List<PaymentMethodEntity> paymentMethodEntities = this.paymentMethodRepository.findAllById(paymentMethodsIds);

            List<TerminalPaymentMethodEntity> terminalPaymentMethodEntities = terminalAssigns.stream().filter(terminalPaymentMethodEntity -> terminalPaymentMethodEntity.getTerminalId().equals(terminalEntity.getId())).toList();
            List<PaymentMethodEntity> terminalPaymentMethods = terminalPaymentMethodEntities.stream().map(terminalPaymentMethodEntity -> paymentMethodEntities.stream().filter(paymentMethodEntity -> paymentMethodEntity.getId().equals(terminalPaymentMethodEntity.getPaymentMethodId())).findFirst().orElse(null)).toList();
            terminalEntity.setPaymentMethods(new HashSet<>(terminalPaymentMethods));

            List<TerminalConfigEntity> terminalConfigEntities = this.terminalConfigRepository.getTerminalConfigs(terminalEntity.getId());
            List<ConfigTerminalDTO> terminalConfigs = this.terminalConfigMapper.map(terminalConfigEntities);

            TerminalDTO terminalDTO = this.terminalMapper.mapTerminalEntityToDTO(terminalEntity);
            terminalDTO.setConfigs(terminalConfigs);

            terminalDTOS.add(terminalDTO);
        }
        return terminalDTOS;
    }

}