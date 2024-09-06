package com.Dev.BlockChain.repository;

import com.Dev.BlockChain.entity.Miner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinerRepository extends JpaRepository<Miner, Long> {
}