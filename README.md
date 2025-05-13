# 🚀 RedisForOrderProject

透過 Redis 快取技術，實作高併發環境下的「訂單下訂與庫存控制」機制，防止超賣與效能瓶頸。

---

## 📌 專案簡介

本專案以 Spring Boot 結合 Redis，模擬秒殺搶購情境。  
在大量用戶同時下單時，系統能：

- 快速處理請求
- 立即回應是否成功下單
- 避免超賣、降低資料庫壓力

---

## 🧰 技術棧

- **Java 17**
- **Spring Boot 3.x**
- **Redis**（使用字串序列化處理 key/value）
- **Spring Data Redis**
- **Maven 构建**

---

## 🛠️ Redis 配置

使用 Spring Boot 註解方式建立 RedisTemplate，並將 Key / Value 序列化為 String 以利操作與除錯。

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key / Value 皆為字串序列化，方便在 redis-cli 中查詢
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}
````

這樣設定後，你可以在 Redis 中用以下指令查看庫存：

```bash
GET product_stock_1001
```

---

## 🔄 下單邏輯核心流程

```java
// 1. 檢查庫存是否足夠（直接從 Redis 快取）
Long stock = redisTemplate.opsForValue().decrement("product_stock_1001");

if (stock != null && stock >= 0) {
    // 2. 若庫存足夠，處理訂單建立
    orderRepository.save(...);
} else {
    // 3. 若庫存不足，可選擇補回 1 或直接回應失敗
    redisTemplate.opsForValue().increment("product_stock_1001");
    throw new RuntimeException("庫存不足");
}
```

---

## 📦 Redis 優勢說明

| 問題     | 傳統做法       | Redis 解法           |
| ------ | ---------- | ------------------ |
| 高併發搶單  | 資料庫搶鎖/死鎖   | Redis 快取扣減庫存，非阻塞操作 |
| 超賣     | 多筆交易同時通過判斷 | `decr()` 保證庫存不為負數  |
| 延遲/反應慢 | 資料庫查詢耗時    | Redis 快速讀寫，幾乎毫秒等級  |

---

## 🚀 快速啟動

### 使用 Docker 啟動 Redis

```bash
docker run -d -p 6379:6379 redis
```

### 啟動 Spring Boot 專案

```bash
./mvnw spring-boot:run
```

---

## 🧪 測試方式

你可以使用 Postman、curl 或寫腳本模擬高併發下單：

```bash
curl -X POST http://localhost:8080/orders/1001
```

搭配 Redis CLI 即時查看庫存：

```bash
GET product_stock_1001
```

---

