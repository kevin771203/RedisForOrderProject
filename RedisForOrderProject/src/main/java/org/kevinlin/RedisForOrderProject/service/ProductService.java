package org.kevinlin.springbootmall.service;

import jakarta.validation.Valid;
import org.kevinlin.springbootmall.dto.ProductQueryParams;
import org.kevinlin.springbootmall.dto.ProductRequest;
import org.kevinlin.springbootmall.model.Product;

import java.util.List;

public interface ProductService {

    Integer countProducts(ProductQueryParams productQueryParams);

    List<Product> getProducts(ProductQueryParams productQueryParams);

    Product getProductById(Long productId);

    Long createProduct(@Valid ProductRequest productRequest);

    void updateProduct(Long productId, @Valid ProductRequest productRequest);

    void deleteProductById(Long productId);
}
