package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${server.port}")
    private int serverPort;

    @PostMapping
    public ResponseEntity<?> payment(
            @RequestBody PaymentRequest request
    ) {

        boolean failed =
                ThreadLocalRandom.current().nextBoolean();

        System.out.println(
                "Payment request received by instance: " + serverPort
        );

        if (failed) {
            System.out.println(
                    "Payment FAILED on instnace: "+ serverPort
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error",
                            "Simulated payment failure"
                    ));
        }

        System.out.println(
                "Payment APPROVED on instance: " + serverPort
        );

        return ResponseEntity.ok(
                new PaymentResponse(
                        "APPROVED",
                        serverPort
                )
        );
    }
}

