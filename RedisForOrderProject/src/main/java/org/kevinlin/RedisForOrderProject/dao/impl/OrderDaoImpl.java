package org.kevinlin.springbootmall.dao.impl;

import org.kevinlin.springbootmall.dao.OrderDao;
import org.kevinlin.springbootmall.dto.OrderQueryParams;
import org.kevinlin.springbootmall.model.Order;
import org.kevinlin.springbootmall.model.OrderItem;
import org.kevinlin.springbootmall.rowmapper.OrderItemRowMapper;
import org.kevinlin.springbootmall.rowmapper.OrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE 1 = 1";

        Map<String, Object> map = new HashMap<>();

        //查詢條件
        sql = addFilteringSql(orderQueryParams, sql, map);

        //排序條件
        sql += " ORDER BY created_date DESC";

        //分頁條件
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;

    }

    @Override
    public Integer countOrders(OrderQueryParams orderQueryParams) {
        String sql = "SELECT COUNT(*) FROM `order` WHERE 1 = 1";

        Map<String, Object> map = new HashMap<>();

        //查詢條件
        sql = addFilteringSql(orderQueryParams, sql, map);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;

    }

    @Override
    public Order getOrderById(Long orderId) {
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        if (orderList.size() > 0) {
            return orderList.get(0);
        }else {
            return null;
        }

    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.amount ,p.product_name, p.image_url " +
                "FROM order_item as oi " +
                "LEFT JOIN product as p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        return orderItemList;
    }

    @Override
    public Long createOrder(Order order) {
        String sql = "INSERT INTO `order` (order_id, user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:orderId, :userId, :totalAmount, NOW(), NOW())";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", order.getOrderId());
        map.put("userId", order.getUserId());
        map.put("totalAmount", order.getTotalAmount());

        Date date = new Date();
        map.put("createdDate", date);
        map.put("lastModifiedDate", date);

        namedParameterJdbcTemplate.update(sql, map);

        return order.getOrderId();
    }

    @Override
    public void createOrderItems(Long orderId, List<OrderItem> orderItemList) {
        String sql = "INSERT INTO order_item (order_item_id,order_id, product_id, quantity, amount) " +
                "VALUES (:orderItemId, :orderId, :productId, :quantity, :amount)";

        MapSqlParameterSource[] parameterSource = new MapSqlParameterSource[orderItemList.size()];

        for(int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);

            parameterSource[i] = new MapSqlParameterSource();
            parameterSource[i].addValue("orderItemId", orderItem.getOrderItemId());
            parameterSource[i].addValue("orderId", orderId);
            parameterSource[i].addValue("productId", orderItem.getProductId());
            parameterSource[i].addValue("quantity", orderItem.getQuantity());
            parameterSource[i].addValue("amount", orderItem.getAmount());

        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSource);
    }

    private String addFilteringSql(OrderQueryParams orderQueryParams, String sql, Map<String, Object> map) {

        if (orderQueryParams.getUserId() != null) {
            sql += " AND user_id = :userId";
            map.put("userId", orderQueryParams.getUserId());
        }

        return sql;


    }
}
