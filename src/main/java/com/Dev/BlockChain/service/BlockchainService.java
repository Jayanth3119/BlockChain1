package com.Dev.BlockChain.service;

import com.Dev.BlockChain.entity.Block;
import com.Dev.BlockChain.entity.Transactions;
import com.Dev.BlockChain.entity.User;
import com.Dev.BlockChain.repository.BlockRepository;
import com.Dev.BlockChain.repository.TransactionsRepository;
import com.Dev.BlockChain.repository.UserRepository;
import com.Dev.BlockChain.repository.MinerRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

@Service
public class BlockchainService {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private TransactionsRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MinerRepository minerRepository;

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public List<Transactions> getAllTransactions() {
        List<Transactions> transactions = transactionRepository.findAll();
        System.out.println("Transactions: " + transactions); // Add this line to verify the list
        if (transactions.isEmpty()) {
            System.out.println("No transactions found in the database");
        } else {
            System.out.println("Found " + transactions.size() + " transactions in the database");
        }
        return transactions;
    }

    public Transactions getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }

    public void mineBlock(Transactions transactions) {
        try {
            Block lastBlock = blockRepository.findTopByOrderByIdDesc();
            Block newBlock = new Block();
            newBlock.setPreviousHash(lastBlock != null ? lastBlock.getHash() : "0");
            newBlock.setHash(generateHash(transactions.toString()));
            blockRepository.save(newBlock);
            User sender = userRepository.findById(transactions.getSenderId()).orElse(null);
            User receiver = userRepository.findById(transactions.getReceiverId()).orElse(null);
            if (sender != null && receiver != null) {
                sender.setBalance(sender.getBalance() - transactions.getAmount());
                receiver.setBalance(receiver.getBalance() + transactions.getAmount());
                userRepository.save(sender);
                userRepository.save(receiver);
            }
            minerRepository.findById(1L).ifPresent(miner -> {
                miner.setReward(miner.getReward() + 50);
                minerRepository.save(miner);
            });

        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred during mining: {}", e.getMessage(), e);
            throw e;
        }
    }


    public String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
