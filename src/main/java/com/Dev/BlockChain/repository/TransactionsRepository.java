package com.Dev.BlockChain.repository;

import com.Dev.BlockChain.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Optional<Transactions> findById(Long id);
    List<Transactions> findAll();
    Transactions save(Transactions transaction);
    List<Transactions> findBySenderId(Long senderId);
    List<Transactions> findByReceiverId(Long receiverId);

}
