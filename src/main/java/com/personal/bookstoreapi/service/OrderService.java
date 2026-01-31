package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.BookItem;
import com.personal.bookstoreapi.domain.entity.DigitalBookItem;
import com.personal.bookstoreapi.domain.entity.Order;
import com.personal.bookstoreapi.domain.entity.OrderItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.domain.enums.OrderStatus;
import com.personal.bookstoreapi.dto.request.CreateOrderItemDTO;
import com.personal.bookstoreapi.dto.request.CreateOrderRequestDTO;
import com.personal.bookstoreapi.dto.response.OrderDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.OrderItemResponseDTO;
import com.personal.bookstoreapi.dto.response.OrderResponseDTO;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookItemRepository bookItemRepository;
    private final UserService userService;

    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {

        if (request.items() == null || request.items()
                                              .isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        User user = userService.getCurrentUser();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PAID);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemDTO itemDTO : request.items()) {

            BookItem bookItem = bookItemRepository.findById(itemDTO.bookItemId())
                                                  .orElseThrow(() -> new IllegalArgumentException(
                                                          "BookItem not found: " + itemDTO.bookItemId()
                                                  ));

            int quantity = itemDTO.quantity();
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            if (bookItem instanceof PhysicalBookItem physical) {
                if (physical.getStockQuantity() < quantity) {
                    throw new IllegalStateException(
                            "Not enough stock for book item " + bookItem.getId()
                    );
                }
                physical.setStockQuantity(physical.getStockQuantity() - quantity);
            }

            if (bookItem instanceof DigitalBookItem && quantity > 1) {
                throw new IllegalArgumentException(
                        "Digital book can only be purchased once"
                );
            }

            BigDecimal itemTotal =
                    bookItem.getPrice()
                            .multiply(BigDecimal.valueOf(quantity));

            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBookItem(bookItem);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(bookItem.getPrice());

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getStatus()
                          .name(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getMyOrders() {
        User user = userService.getCurrentUser();

        return orderRepository.findAllByUserId(user.getId())
                              .stream()
                              .map(order -> new OrderResponseDTO(
                                      order.getId(),
                                      order.getStatus()
                                           .name(),
                                      order.getTotalAmount(),
                                      order.getCreatedAt()
                              ))
                              .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponseDTO getOrderDetails(Long orderId) {
        User user = userService.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser()
                  .getId()
                  .equals(user.getId())) {
            throw new SecurityException("You are not allowed to view this order");
        }

        List<OrderItemResponseDTO> items = order.getItems()
                                                .stream()
                                                .map(item -> new OrderItemResponseDTO(
                                                        item.getBookItem()
                                                            .getId(),
                                                        item.getBookItem()
                                                            .getBook()
                                                            .getTitle(),
                                                        item.getQuantity(),
                                                        item.getUnitPrice()
                                                ))
                                                .toList();

        return new OrderDetailsResponseDTO(
                order.getId(),
                order.getStatus()
                     .name(),
                order.getTotalAmount(),
                items
        );
    }
}