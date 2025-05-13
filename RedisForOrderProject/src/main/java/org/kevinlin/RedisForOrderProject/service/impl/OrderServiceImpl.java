package org.kevinlin.springbootmall.service.impl;

import org.kevinlin.springbootmall.dao.OrderDao;
import org.kevinlin.springbootmall.dao.ProductDao;
import org.kevinlin.springbootmall.dao.UserDao;
import org.kevinlin.springbootmall.dto.BuyItem;
import org.kevinlin.springbootmall.dto.OrderQueryParams;
import org.kevinlin.springbootmall.dto.CreateOrderRequest;
import org.kevinlin.springbootmall.model.Order;
import org.kevinlin.springbootmall.model.OrderItem;
import org.kevinlin.springbootmall.model.Product;
import org.kevinlin.springbootmall.model.Users;
import org.kevinlin.springbootmall.service.OrderService;
import org.kevinlin.springbootmall.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;



import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;


    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        for(Order order : orderList) {
            List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(order.getOrderId());
            order.setOrderItemList(orderItemList);
        }

        return orderList;
    }

    @Override
    public Integer countOrders(OrderQueryParams orderQueryParams) {
        return orderDao.countOrders(orderQueryParams);
    }

    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderDao.getOrderById(orderId);

        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

        order.setOrderItemList(orderItemList);

        return order;
    }

    @Transactional
    @Override
    public Long createOrder(Long userId, CreateOrderRequest createOrderRequest) {

        //檢查 userId 是否存在
        Users user = userDao.getUserById(userId);

        if (user == null) {
            log.warn("User with id {} not found", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // 加 Redis Lock
        String lockKey = "order:create:" + userId;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "lock", java.time.Duration.ofSeconds(5));

        if (Boolean.FALSE.equals(success)) {
            log.warn("User {} is already creating an order, please try again later", userId);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "正在處理訂單，請稍後再試");
        }

        try {
            int totalAmount = 0;
            List<OrderItem> orderItemList = new ArrayList<>();

            for (BuyItem buyItem : createOrderRequest.getBuyItemsList()) {
                Product product = productDao.getProductById(buyItem.getProductId());

                //檢查 product 是否存在
                if (product == null) {
                    log.warn("Product with id {} not found", buyItem.getProductId());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

                } else if (product.getStock() < buyItem.getQuantity()) {
                    log.warn("Product with id {} stock is not enough", buyItem.getProductId());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }

                productDao.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity());

                //計算訂單總花費
                int amount = product.getPrice() * buyItem.getQuantity();
                totalAmount += amount;

                //轉換BuyItem為OrderItem
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderItemId(idGenerator.generateId());
                orderItem.setProductId(buyItem.getProductId());
                orderItem.setQuantity(buyItem.getQuantity());
                orderItem.setAmount(amount);

                orderItemList.add(orderItem);
            }


            Order order = new Order();
            order.setOrderId(idGenerator.generateId());
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);

            orderDao.createOrderItems(order.getOrderId(),orderItemList);


            return orderDao.createOrder(order);
        } finally {
            // 最後釋放鎖
            redisTemplate.delete(lockKey);
        }
    }
}
