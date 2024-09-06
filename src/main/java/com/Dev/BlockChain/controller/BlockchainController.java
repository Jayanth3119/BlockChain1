package com.Dev.BlockChain.controller;

import org.springframework.ui.Model;
import com.Dev.BlockChain.entity.Transaction;
import com.Dev.BlockChain.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class BlockchainController {

    @Autowired
    private BlockchainService blockchainService;

    @GetMapping("/blocks")
    public String getBlocks(Model model) {
        model.addAttribute("blocks", blockchainService.getAllBlocks());
        return "blocks";
    }

    @GetMapping("/mine")
    public String mineBlock(@RequestParam Long transactionId, Model model) {
        Transaction transaction = blockchainService.getTransactionById(transactionId);
        blockchainService.mineBlock(transaction);
        model.addAttribute("message", "Block mined successfully!");
        return "mine";
    }

    @GetMapping("/transactions")
    public String getTransactions(Model model) {
        model.addAttribute("transactions", blockchainService.getAllTransactions());
        return "transactions";
    }

    @GetMapping("/transactionStatus")
    public String getTransactionStatus(@RequestParam Long transactionId, Model model) {
        Transaction transaction = blockchainService.getTransactionById(transactionId);
        model.addAttribute("transaction", transaction);
        return "transactionStatus";
    }
}
