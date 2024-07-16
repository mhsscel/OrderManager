package com.genesis.studio.sibs.ordermanager.service;

import com.genesis.studio.sibs.ordermanager.dto.StockMovementDTO;
import com.genesis.studio.sibs.ordermanager.model.Item;
import com.genesis.studio.sibs.ordermanager.model.Order;
import com.genesis.studio.sibs.ordermanager.model.StockMovement;
import com.genesis.studio.sibs.ordermanager.repository.ItemRepository;
import com.genesis.studio.sibs.ordermanager.repository.OrderRepository;
import com.genesis.studio.sibs.ordermanager.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final EmailService emailService;

    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository, OrderRepository orderRepository, ItemRepository itemRepository, EmailService emailService) {
        this.stockMovementRepository = stockMovementRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.emailService = emailService;
    }

    public StockMovementDTO createStockMovement(StockMovement stockMovement) {
        Item item = itemRepository.findById(stockMovement.getItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        stockMovement.setItem(item);
        stockMovement.setCreationDate(new Date());

        StockMovement savedStockMovement = stockMovementRepository.save(stockMovement);

        item.setQuantity(item.getQuantity() + stockMovement.getQuantity());
        itemRepository.save(item);

        return convertToDTO(savedStockMovement);
    }

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public StockMovement getStockMovementById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockMovement not found with id: " + id));
    }

    public StockMovementDTO updateStockMovement(Long id, StockMovement stockMovement) {
        StockMovement existingStockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StockMovement not found"));

        Item item = itemRepository.findById(stockMovement.getItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        existingStockMovement.setItem(item);
        existingStockMovement.setQuantity(stockMovement.getQuantity());
        existingStockMovement.setCreationDate(existingStockMovement.getCreationDate());

        StockMovement updatedStockMovement = stockMovementRepository.save(existingStockMovement);
        return convertToDTO(updatedStockMovement);
    }

    public void deleteStockMovement(Long id) {
        stockMovementRepository.deleteById(id);
    }

    private StockMovementDTO convertToDTO(StockMovement stockMovement) {
        return new StockMovementDTO(
                stockMovement.getId(),
                stockMovement.getCreationDate(),
                stockMovement.getItem().getId(),
                stockMovement.getItem().getName(),
                stockMovement.getQuantity()
        );
    }

    private void attributeStockMovementToOrders(StockMovement stockMovement) {
        List<Order> orders = orderRepository.findByItemIdAndCompletedFalse(stockMovement.getItem().getId());
        int remainingQuantity = stockMovement.getQuantity();
        for (Order order : orders) {
            if (remainingQuantity <= 0) break;
            int neededQuantity = order.getQuantity() - order.getFulfilledQuantity();
            if (neededQuantity > 0) {
                int usedQuantity = Math.min(remainingQuantity, neededQuantity);
                order.setFulfilledQuantity(order.getFulfilledQuantity() + usedQuantity);
                remainingQuantity -= usedQuantity;
                if (order.getQuantity() == order.getFulfilledQuantity()) {
                    order.setCompleted(true);
                    orderRepository.save(order);
                    emailService.sendOrderCompletedEmail(order);
                }
            }
        }
        stockMovement.setQuantity(stockMovement.getQuantity() - remainingQuantity);
        stockMovementRepository.save(stockMovement);
    }
}