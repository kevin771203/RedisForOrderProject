package org.kevinlin.springbootmall.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceRedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testStockDecrement() {
        String productKey = "stock:product123";

        // 初始化庫存
        redisTemplate.opsForValue().set(productKey, "10");

        // 模擬購買
        Long result = redisTemplate.opsForValue().decrement(productKey);

        // 驗證是否正常減少
        Assertions.assertEquals("9", redisTemplate.opsForValue().get(productKey));
    }

    @Test
    public void testConcurrentDecrement() throws InterruptedException {
        // Given：初始化商品庫存為 5
        String productKey = "stock:product456";
        redisTemplate.opsForValue().set(productKey, "5");

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // When：同時有 10 個執行緒模擬購買行為
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> attemptDecrement(productKey, successCount, latch));
        }

        latch.await(); // 等待所有執行緒執行完畢

        // Then：最多只有 5 筆成功扣庫存
        Assertions.assertEquals(5, successCount.get());
    }

    private void attemptDecrement(String key, AtomicInteger successCount, CountDownLatch latch) {
        try {
            // 嘗試扣庫存
            Long stock = redisTemplate.opsForValue().decrement(key);
            if (stock != null && stock >= 0) {
                successCount.incrementAndGet(); // 成功扣庫存
            } else {
                // 若失敗（扣成負數），則回補
                redisTemplate.opsForValue().increment(key);
            }
        } finally {
            latch.countDown(); // 執行緒完成後計數器減一
        }
    }



}