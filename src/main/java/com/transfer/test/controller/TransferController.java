package com.transfer.test.controller;

import com.transfer.test.dto.TransferRequestDTO;
import com.transfer.test.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransactionService transactionService;

    public TransferController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequestDTO request) {
        transactionService.transfer(request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok("Transfer successful");
    }
}