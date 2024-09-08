package com.Dev.BlockChain.controller;

import com.Dev.BlockChain.entity.Transactions;
import com.Dev.BlockChain.service.BlockchainService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BlockchainController {

    @Autowired
    private BlockchainService blockchainService;

    @GetMapping("/")
    public String defaultPage(Model model) {
        return "main";
    }

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

    @GetMapping("/blocks")
    public String getBlocks(Model model) {
        model.addAttribute("blocks", blockchainService.getAllBlocks());
        return "blocksView";
    }

    @GetMapping("/mine")
    public String mineBlock(@RequestParam Long transactionId, Model model) {
        Transactions transaction = blockchainService.getTransactionById(transactionId);
        blockchainService.mineBlock(transaction);
        model.addAttribute("message", "Block mined successfully!");
        return "mineStatus";
    }

    @GetMapping("/transactions")
    public String getTransactions(Model model) {
      List<Transactions> transactions = blockchainService.getAllTransactions();
      System.out.println("Transactions: " + transactions);
      model.addAttribute("transactions", transactions);
      return "transactionsView";
    }

    @GetMapping("/transactionStatus/{transactionId}")
    public String getTransactionStatus(@PathVariable Long transactionId, Model model) {
        Optional<Transactions> transaction = Optional.ofNullable(blockchainService.getTransactionById(transactionId));
        if (transaction.isPresent()) {
            model.addAttribute("status", transaction.get().getStatus());
        } else {
            model.addAttribute("status", "Transaction not found with ID: " + transactionId);
        }
        return "transactionStatusView";
    }


}
