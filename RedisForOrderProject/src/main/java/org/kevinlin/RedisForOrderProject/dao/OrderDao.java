package org.kevinlin.springbootmall.dao;

import org.kevinlin.springbootmall.dto.OrderQueryParams;
import org.kevinlin.springbootmall.model.Order;
import org.kevinlin.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {
    Long createOrder(Order order);

    void createOrderItems(Long orderId, List<OrderItem> orderItemList);

    Order getOrderById(Long orderId);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Integer countOrders(OrderQueryParams orderQueryParams);
}
