package com.superpay.config.controller;
import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.dtos.requests.PaymentMethodRequest;
import com.superpay.config.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/config/terminal-payment-methods")
public class PaymentMethodController {

        @Autowired
        PaymentMethodService paymentMethodService;


        @Autowired
        public PaymentMethodController(PaymentMethodService paymentMethodServise) {
            this.paymentMethodService = paymentMethodServise;
        }

        @PostMapping("/create-or-update")
        public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@RequestBody PaymentMethodRequest paymentMethodRequest) {
            PaymentMethodDTO createdPaymentMethod = paymentMethodService.createOrUpdatePaymentMethod(paymentMethodRequest);
            return ResponseEntity.ok(createdPaymentMethod);
        }

        @PostMapping("/by-ids")
        public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodById(@RequestBody ByIds byIds) {
            List<PaymentMethodDTO> createdPaymentMethod = paymentMethodService.getPaymentMethodById(byIds);
            return ResponseEntity.ok(createdPaymentMethod);
        }

    @GetMapping("/{code}")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByTerminalCode(@PathVariable String code) {
        List<PaymentMethodDTO> paymentMethods = paymentMethodService.getPaymentMethodsByTerminalCode(code);

        if (paymentMethods != null && !paymentMethods.isEmpty()) {
            return ResponseEntity.ok(paymentMethods);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
