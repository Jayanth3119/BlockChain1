package com.Dev.BlockChain.entity;

import com.Dev.BlockChain.model.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private Long senderId;
    private Long receiverId;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
