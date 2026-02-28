package com.fountlinedigital.backend.service;

import com.fountlinedigital.backend.dao.InventoryItemRepository;
import com.fountlinedigital.backend.dto.InventoryCreateRequest;
import com.fountlinedigital.backend.dto.InventoryResponse;
import com.fountlinedigital.backend.dto.InventoryUpdateRequest;
import com.fountlinedigital.backend.entity.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository repo;

    public List<InventoryResponse> search(String q) {
        List<InventoryItem> items;
        if (q == null || q.isBlank()) {
            items = repo.findAll();
        } else {
            var byName = repo.findByNameContainingIgnoreCase(q);
            var bySku = repo.findBySkuContainingIgnoreCase(q);

            for (var item : bySku) {
                boolean exists = byName.stream().anyMatch(n -> n.getId().equals(item.getId()));
                if (!exists) byName.add(item);
            }
            items = byName;
        }
        return items.stream().map(this::toResponse).toList();
    }

    public InventoryResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public InventoryResponse create(InventoryCreateRequest req) {
        repo.findBySku(req.sku()).ifPresent(existing -> {
            throw new IllegalArgumentException("SKU already exists");
        });

        InventoryItem item = new InventoryItem();
        item.setName(req.name());
        item.setSku(req.sku());
        item.setQuantity(req.quantity());
        item.setReorderLevel(req.reorderLevel());

        return toResponse(repo.save(item));
    }

    @Transactional
    public InventoryResponse update(Long id, InventoryUpdateRequest req) {
        InventoryItem existing = find(id);

        repo.findBySku(req.sku()).ifPresent(other -> {
            if (!other.getId().equals(id)) throw new IllegalArgumentException("SKU already exists");
        });

        existing.setName(req.name());
        existing.setSku(req.sku());
        existing.setQuantity(req.quantity());
        existing.setReorderLevel(req.reorderLevel());

        return toResponse(repo.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private InventoryItem find(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
    }

    private InventoryResponse toResponse(InventoryItem i) {
        return new InventoryResponse(
                i.getId(),
                i.getName(),
                i.getSku(),
                i.getQuantity(),
                i.getReorderLevel(),
                i.getUpdatedAt()
        );
    }
}