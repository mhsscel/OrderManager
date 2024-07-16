package com.genesis.studio.sibs.ordermanager.service;

import com.genesis.studio.sibs.ordermanager.dto.OrderRequest;
import com.genesis.studio.sibs.ordermanager.model.AppUser;
import com.genesis.studio.sibs.ordermanager.model.Item;
import com.genesis.studio.sibs.ordermanager.model.Order;
import com.genesis.studio.sibs.ordermanager.model.StockMovement;
import com.genesis.studio.sibs.ordermanager.repository.ItemRepository;
import com.genesis.studio.sibs.ordermanager.repository.OrderRepository;
import com.genesis.studio.sibs.ordermanager.repository.StockMovementRepository;
import com.genesis.studio.sibs.ordermanager.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final StockMovementRepository stockMovementRepository;

    @Autowired
    private final StockMovementService stockMovementService;

    @Autowired
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, UserRepository userRepository, StockMovementRepository stockMovementRepository, StockMovementService stockMovementService, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementService = stockMovementService;
        this.emailService = emailService;
    }

    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        logger.info("Fetching order with id: {}", id);
        Optional<Order> order = orderRepository.findById(id);
        return order.orElse(null);
    }

    public Order createOrder(Long itemId, int quantity, Long userId) {
        logger.info("Creating order for item: {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (item == null) {
            logger.info("Item cannot be null.");
            throw new IllegalArgumentException("Item cannot be null.");
        }

        if (item.getQuantity() < quantity) {
            logger.info("Insufficient stock for item: " + item.getName());
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getName());
        }

        item.setQuantity(item.getQuantity() - quantity);
        item = itemRepository.save(item);

        Order order = new Order();
        order.setItem(item);
        order.setQuantity(quantity);
        order.setUser(user);
        order.setCreationDate(new Date());
        order.setFulfilledQuantity(0);
        order.setCompleted(false);

        logger.info("Order successfully created: " + order.getId());
        order = orderRepository.save(order);
        fulfillOrder(order);

        /*StockMovement stockMovement = new StockMovement();
        stockMovement.setItem(item);
        stockMovement.setQuantity(-order.getQuantity());
        stockMovementService.createStockMovement(stockMovement);*/

        return order;
    }

    public Order updateOrder(Long id, OrderRequest orderRequest) {
        logger.info("Updating order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        Item item = itemRepository.findById(orderRequest.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + orderRequest.getItemId()));
        AppUser user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + orderRequest.getUserId()));

        order.setItem(item);
        order.setUser(user);
        order.setQuantity(orderRequest.getQuantity());
        order.setCreationDate(new Date());

        orderRepository.save(order);
        fulfillOrder(order);
        return order;
    }

    public void deleteOrder(Long id) {
        logger.info("Deleting order with id: {}", id);
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            logger.error("Order with id {} does not exist", id);
        }
    }
    private void fulfillOrder(Order order) {
        logger.info("Fulfilling order with ID: {}", order.getId());
        List<StockMovement> stockMovements = stockMovementRepository.findByItemId(order.getItem().getId());
        int remainingQuantity = order.getQuantity();
        for (StockMovement stockMovement : stockMovements) {
            if (remainingQuantity <= 0) break;
            if (stockMovement.getQuantity() > 0) {
                int usedQuantity = Math.min(remainingQuantity, stockMovement.getQuantity());
                stockMovement.setQuantity(stockMovement.getQuantity() - usedQuantity);
                stockMovementRepository.save(stockMovement);
                remainingQuantity -= usedQuantity;
                order.setFulfilledQuantity(order.getFulfilledQuantity() + usedQuantity);
            }
        }
        if (remainingQuantity <= 0) {
            logger.info("Order with ID: {} is completed", order.getId());
            order.setCompleted(true);
            orderRepository.save(order);
            emailService.sendOrderCompletedEmail(order);
        } else {
            orderRepository.save(order);
        }
    }
}