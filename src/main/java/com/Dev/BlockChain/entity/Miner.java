package com.Dev.BlockChain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Miner {
    @Id
    private Long id;
    private String name;
    private Double reward;
}
