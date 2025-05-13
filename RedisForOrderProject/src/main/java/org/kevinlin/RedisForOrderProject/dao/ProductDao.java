package org.kevinlin.springbootmall.dao;

import org.kevinlin.springbootmall.dto.ProductQueryParams;
import org.kevinlin.springbootmall.dto.ProductRequest;
import org.kevinlin.springbootmall.model.Product;

import java.util.List;

public interface ProductDao {

    Integer countProducts(ProductQueryParams productQueryParams);

    List<Product> getProducts(ProductQueryParams productQueryParams);

    Product getProductById(Long productId);

    Long createProduct(Product product);

    void updateProduct(Long productId, ProductRequest productRequest);

    void deleteProductById(Long productId);

    void updateStock(Long productId, Integer stock);

}
