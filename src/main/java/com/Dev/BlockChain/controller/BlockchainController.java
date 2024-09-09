package com.Dev.BlockChain.controller;

import com.Dev.BlockChain.entity.Block;
import com.Dev.BlockChain.entity.Transactions;
import com.Dev.BlockChain.entity.User;
import com.Dev.BlockChain.repository.BlockRepository;
import com.Dev.BlockChain.repository.MinerRepository;
import com.Dev.BlockChain.repository.UserRepository;
import com.Dev.BlockChain.service.BlockchainService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BlockchainController {

    @Autowired
    private MinerRepository minerRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlockchainService blockchainService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Main page controller
    @GetMapping("/")
    public String defaultPage(Model model) {
        return "main";
    }

    // Page for generating hash
    @GetMapping("/generateHashPage")
    public String showHashGeneratorPage() {
        return "generateHash";
    }

    @PostMapping("/generateHash")
    public String generateHashForInput(@RequestParam String input, Model model) {
        String hash = blockchainService.generateHash(input);
        model.addAttribute("input", input);
        model.addAttribute("hash", hash);
        return "generateHash";
    }

    // View all blocks
    @GetMapping("/blocks")
    public String getBlocks(Model model) {
        model.addAttribute("blocks", blockchainService.getAllBlocks());
        return "blocksView";
    }

    // Page to display blockchain transaction form
    @GetMapping("/blockchainTransaction")
    public String blockchainTransactionPage() {
        return "blockchainTransaction";
    }

    @PostMapping("/mineBlock")
    public ResponseEntity<?> mineBlock(@RequestBody Transactions transactions) {
        try {
            Block lastBlock = blockRepository.findTopByOrderByIdDesc();
            Block newBlock = new Block();
            String previousHash = lastBlock != null ? lastBlock.getHash() : "0";
            logger.info("Previous block hash: " + previousHash);

            newBlock.setPreviousHash(previousHash);
            String newHash = blockchainService.generateHash(transactions.toString());
            newBlock.setHash(newHash);
            blockRepository.save(newBlock);
            logger.info("New block hash: " + newHash);

            User sender = userRepository.findById(transactions.getSenderId()).orElse(null);
            User receiver = userRepository.findById(transactions.getReceiverId()).orElse(null);
            if (sender != null && receiver != null) {
                logger.info("Sender balance before: " + sender.getBalance());
                logger.info("Receiver balance before: " + receiver.getBalance());

                sender.setBalance(sender.getBalance() - transactions.getAmount());
                receiver.setBalance(receiver.getBalance() + transactions.getAmount());

                userRepository.save(sender);
                userRepository.save(receiver);

                logger.info("Sender balance after: " + sender.getBalance());
                logger.info("Receiver balance after: " + receiver.getBalance());
            }

            minerRepository.findById(1L).ifPresent(miner -> {
                miner.setReward(miner.getReward() + 50);
                minerRepository.save(miner);
            });

            Map<String, String> response = new HashMap<>();
            response.put("previousHash", previousHash);
            response.put("newHash", newHash);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error occurred during mining: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mining failed");
        }
    }

    @GetMapping("/transactions")
    public String getTransactions(Model model) {
        List<Transactions> transactions = blockchainService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactionsView";
    }

    @GetMapping("/miningStatus")
    public String showMiningStatusPage() {
        return "miningStatus";
    }

    @GetMapping("/transactionStatus/{transactionId}")
    @ResponseBody
    public Map<String, String> getTransactionStatus(@PathVariable Long transactionId) {
        Transactions transaction = blockchainService.getTransactionById(transactionId);
        Map<String, String> response = new HashMap<>();
        response.put("status", transaction.getStatus().name());
        return response;
    }

}
