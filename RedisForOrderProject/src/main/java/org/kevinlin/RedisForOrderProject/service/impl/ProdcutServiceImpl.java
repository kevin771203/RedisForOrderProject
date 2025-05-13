package org.kevinlin.springbootmall.service.impl;

import org.kevinlin.springbootmall.dao.ProductDao;
import org.kevinlin.springbootmall.dto.ProductQueryParams;
import org.kevinlin.springbootmall.dto.ProductRequest;
import org.kevinlin.springbootmall.model.Product;
import org.kevinlin.springbootmall.service.ProductService;
import org.kevinlin.springbootmall.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProdcutServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public Integer countProducts(ProductQueryParams productQueryParams) {
        return productDao.countProducts(productQueryParams);
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        return productDao.getProducts(productQueryParams);
    }

    @Override
    public Product getProductById(Long productId) {
        return productDao.getProductById(productId);
    }

    @Override
    public Long createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setProductId(idGenerator.generateId()); // 雪花 ID
        product.setProductName(productRequest.getProductName());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setDescription(productRequest.getDescription());

        return productDao.createProduct(product); //丟完整的 Product 給 DAO
    }


    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {
        productDao.updateProduct(productId, productRequest);
    }

    @Override
    public void deleteProductById(Long productId) {
        productDao.deleteProductById(productId);
    }

}
