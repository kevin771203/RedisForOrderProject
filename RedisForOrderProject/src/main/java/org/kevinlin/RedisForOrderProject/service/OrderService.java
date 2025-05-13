package org.kevinlin.springbootmall.service;

import org.kevinlin.springbootmall.dto.OrderQueryParams;
import org.kevinlin.springbootmall.dto.CreateOrderRequest;
import org.kevinlin.springbootmall.model.Order;

import java.util.List;

public interface OrderService {

    Long createOrder(Long userId, CreateOrderRequest createOrderRequest);

    Order getOrderById(Long orderId);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Integer countOrders(OrderQueryParams orderQueryParams);
}
