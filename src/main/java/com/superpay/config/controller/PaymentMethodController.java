package com.superpay.config.controller;
import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-method")
public class PaymentMethodController {

        private final PaymentMethodService paymentMethodServise;

        @Autowired
        public PaymentMethodController(PaymentMethodService paymentMethodServise) {
            this.paymentMethodServise = paymentMethodServise;
        }

        @PostMapping
        public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
            PaymentMethodDTO createdPaymentMethod = paymentMethodServise.createOrUpdatePaymentMethod(paymentMethodDTO);
            return ResponseEntity.ok(createdPaymentMethod);
        }

        //Falta más metodods, findbyid, delete, etc
}
