package org.kevinlin.springbootmall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import org.junit.jupiter.api.Test;
import org.kevinlin.springbootmall.dao.UserDao;
import org.kevinlin.springbootmall.dao.impl.UserDaoImpl;
import org.kevinlin.springbootmall.dto.BuyItem;
import org.kevinlin.springbootmall.dto.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserDaoImpl userDaoImpl;
    @Autowired
    private UserDao userDao;

    // 創建訂單
    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Transactional
    @Test
    void createOrder_success() throws Exception {

        // Given
        CreateOrder order = getOrder(1L, createBuyItems(
            of(1L, 5),
            of(2L, 2)
        ));

        // When
        RequestBuilder requestBuilder = when_execute(order);

        // Then
        then_should_be(requestBuilder,
                201,
                1,
                750,
                2
        );
    }

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Transactional
    @Test
    void createOrder_illegalArgument_emptyBuyItemList() throws Exception {

        //given
        CreateOrder order = getOrder(1L, createBuyItems());

        //when

        RequestBuilder requestBuilder = when_execute(order);

        //then
        then_should_be(requestBuilder);
    }

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Transactional
    @Test
    void createOrder_userNotExist() throws Exception {

        // Given
        CreateOrder order = getOrder(100L, createBuyItems(
            of(1L, 5)));

        // When
        RequestBuilder requestBuilder = when_execute(order);

        //then
        then_should_be(requestBuilder);
    }

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Transactional
    @Test
    void createOrder_productNotExist() throws Exception {

        // Given
        CreateOrder order = getOrder(1L, createBuyItems(
            of(100L, 5)));

        // When
        RequestBuilder requestBuilder = when_execute(order);

        //then
        then_should_be(requestBuilder);
    }

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Transactional
    @Test
    void createOrder_stockNotEnough() throws Exception {

        // Given
        CreateOrder order = getOrder(1L, createBuyItems(
            of(1L, 50000)));

        // When
        RequestBuilder requestBuilder = when_execute(order);

        //then
        then_should_be(requestBuilder);
    }

    // 查詢訂單列表
    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Test
    void getOrders() throws Exception {

        //given
        Long userId = 1L;

        //when
        RequestBuilder requestBuilder = when_execute(userId);

        //then
        then_should_be(
                requestBuilder,
                status().isOk(),
                2,
                List.of(
                    new OrderExpectation(1, 100000, 1),
                    new OrderExpectation(1, 500690, 3)
                )
        );
    }

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Test
    void getOrders_pagination() throws Exception {

        //given
        Long userId = 1L;
        String limit = "2";
        String offset = "2";

        //when
        RequestBuilder requestBuilder = when_execute(userId, limit, offset);

        //then
        then_should_be(
                requestBuilder,
                status().isOk(),
                0,
                List.of()
        );
    }

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Test
    void getOrders_userHasNoOrder() throws Exception {

        //given
        Long userId = 2L;

        //when
        RequestBuilder requestBuilder = when_execute(userId);

        //then
        then_should_be(
                requestBuilder,
                status().isOk(),
                0,
                List.of()
        );
    }


    private record OrderExpectation(int userId, int totalAmount, int itemCount) {}

    @WithMockUser(username = "testuser", roles = {"NORMAL_MEMBER"})
    @Test
    void getOrders_userNotExist() throws Exception {

        //given
        Long userId = 100L;

        //when
        RequestBuilder requestBuilder = when_execute(userId);

        //then
        then_should_be(
                requestBuilder,
                status().isOk(),
                0,
                List.of()
        );
    }

    private CreateOrder getOrder(Long userId, List<BuyItem> buyItemList) throws JsonProcessingException {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setBuyItemsList(buyItemList);
        String json = objectMapper.writeValueAsString(createOrderRequest);
        return new CreateOrder(userId, json);
    }

    private static List<BuyItem> createBuyItems(@Nullable Object[]... items) {
        List<BuyItem> buyItemList = new ArrayList<>();
        for (Object[] item : items) {
            Long productId = (Long) item[0];
            Integer quantity = (Integer) item[1];
            buyItemList.add(createBuyItem(productId, quantity));
        }
        return buyItemList;
    }

    private static BuyItem createBuyItem(Long productId, int quantity) {
        BuyItem item = new BuyItem();
        item.setProductId(productId);
        item.setQuantity(quantity);
        return item;
    }

    private static Object[] of(Long productId, Integer quantity) {
        return new Object[]{productId, quantity};
    }

    private record CreateOrder(Long userId, String json) {}

    private static RequestBuilder when_execute(CreateOrder order) {
        return MockMvcRequestBuilders
                .post("/users/{userId}/orders", order.userId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(order.json())
                .with(csrf());
    }

    private static RequestBuilder when_execute(Long userId) {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", userId);
        return requestBuilder;
    }

    private static RequestBuilder when_execute(Long userId, String limit, String offset) {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", userId)
                .param("limit", limit)
                .param("offset", offset);
        return requestBuilder;
    }

    private void then_should_be(RequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    private void then_should_be(RequestBuilder requestBuilder,
                                int status,
                                int userId,
                                int totalAmount,
                                int orderItemList) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(status))
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.userId", equalTo(userId)))
                .andExpect(jsonPath("$.totalAmount", equalTo(totalAmount)))
                .andExpect(jsonPath("$.orderItemList", hasSize(orderItemList)))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    private void then_should_be(RequestBuilder requestBuilder, ResultMatcher status, int result, List<OrderExpectation> expectations) throws Exception {

        mockMvc.perform(requestBuilder)
            .andExpect(status)
            .andExpect(jsonPath("$.limit", notNullValue()))
            .andExpect(jsonPath("$.offset", notNullValue()))
            .andExpect(jsonPath("$.total", notNullValue()))
            .andExpect(jsonPath("$.results", hasSize(result)));

        for (int i = 0; i < expectations.size(); i++) {
            OrderExpectation exp = expectations.get(i);
            mockMvc.perform(requestBuilder)
                    .andExpect(jsonPath("$.results[" + i + "].orderId", notNullValue()))
                    .andExpect(jsonPath("$.results[" + i + "].userId", equalTo(exp.userId())))
                    .andExpect(jsonPath("$.results[" + i + "].totalAmount", equalTo(exp.totalAmount())))
                    .andExpect(jsonPath("$.results[" + i + "].orderItemList", hasSize(exp.itemCount())))
                    .andExpect(jsonPath("$.results[" + i + "].createdDate", notNullValue()))
                    .andExpect(jsonPath("$.results[" + i + "].lastModifiedDate", notNullValue()));
        }
    }
}