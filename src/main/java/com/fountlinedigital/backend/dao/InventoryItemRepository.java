package com.fountlinedigital.backend.dao;

import com.fountlinedigital.backend.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findBySku(String sku);

    List<InventoryItem> findByNameContainingIgnoreCase(String name);

    List<InventoryItem> findBySkuContainingIgnoreCase(String sku);
}