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

    // Define and initialize the logger
    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public List<Transactions> getAllTransactions() {
        List<Transactions> transactions = transactionRepository.findAll();
        logger.info("Transactions: " + transactions); // Use logger instead of System.out.println
        if (transactions.isEmpty()) {
            logger.info("No transactions found in the database");
        } else {
            logger.info("Found " + transactions.size() + " transactions in the database");
        }
        return transactions;
    }

    public Transactions getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }

    public void mineBlock(Transactions transactions) {
        try {
            User sender = userRepository.findById(transactions.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + transactions.getSenderId()));
            User receiver = userRepository.findById(transactions.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + transactions.getReceiverId()));

            // Check if sender has enough balance
            if (sender.getBalance() < transactions.getAmount()) {
                logger.error("Transaction failed: Sender balance is insufficient.");
                throw new RuntimeException("Insufficient balance for transaction");
            }

            Block lastBlock = blockRepository.findTopByOrderByIdDesc();
            Block newBlock = new Block();
            String previousHash = lastBlock != null ? lastBlock.getHash() : "0";
            logger.info("Previous block hash: " + previousHash);

            newBlock.setPreviousHash(previousHash);
            String newHash = generateHash(transactions.toString());
            newBlock.setHash(newHash);
            blockRepository.save(newBlock);
            logger.info("New block hash: " + newHash);

            // Update balances
            sender.setBalance(sender.getBalance() - transactions.getAmount());
            receiver.setBalance(receiver.getBalance() + transactions.getAmount());

            userRepository.save(sender);
            userRepository.save(receiver);

            minerRepository.findById(1L).ifPresent(miner -> {
                miner.setReward(miner.getReward() + 50);
                minerRepository.save(miner);
            });

        } catch (Exception e) {
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
