package com.Dev.BlockChain.repository;

import com.Dev.BlockChain.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Block findTopByOrderByIdDesc();
    List<Block> findAll();
    Block save(Block block);
    Block findByHash(String hash);

}
