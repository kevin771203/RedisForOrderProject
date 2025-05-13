package org.kevinlin.springbootmall.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.kevinlin.springbootmall.dto.OrderQueryParams;
import org.kevinlin.springbootmall.dto.CreateOrderRequest;
import org.kevinlin.springbootmall.model.Order;
import org.kevinlin.springbootmall.service.OrderService;
import org.kevinlin.springbootmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<Order>> getOrders(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer offset) {

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        //取得orderlist
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        //取得orderlist的總數
        Integer count = orderService.countOrders(orderQueryParams);


        //分頁
        Page<Order> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(count);
        page.setResults(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

                                                 @PostMapping("/users/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable Long userId,
                                                 @RequestBody @Valid CreateOrderRequest createOrderRequest) {

        Long orderId = orderService.createOrder(userId, createOrderRequest);

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);


    }
}
