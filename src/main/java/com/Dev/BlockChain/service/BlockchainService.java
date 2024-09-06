package com.Dev.BlockChain.service;

import com.Dev.BlockChain.entity.Block;
import com.Dev.BlockChain.entity.Transaction;
import com.Dev.BlockChain.entity.User;
import com.Dev.BlockChain.repository.BlockRepository;
import com.Dev.BlockChain.repository.TransactionRepository;
import com.Dev.BlockChain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlockchainService {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Business logic for mining and updating ledger, etc.
    public void mineBlock(Transaction transaction) {
        // Mine logic to create a new block based on the transaction
        Block newBlock = new Block();
        newBlock.setPreviousHash(getLastBlock().getHash());
        newBlock.setHash(calculateHash());
        blockRepository.save(newBlock);

        // Update ledger (sender and receiver balances)
        User sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        User receiver = userRepository.findById(transaction.getReceiverId()).orElse(null);

        if (sender != null && receiver != null) {
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            receiver.setBalance(receiver.getBalance() + transaction.getAmount());

            userRepository.save(sender);
            userRepository.save(receiver);
        }
    }

    private Block getLastBlock() {
        return blockRepository.findAll().get(blockRepository.findAll().size() - 1);
    }

    private String calculateHash() {
        // Logic to calculate block hash (use cryptographic methods like SHA-256)
        return "someHash";
    }

    public Transaction getTransactionById(Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return transaction.orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

}
