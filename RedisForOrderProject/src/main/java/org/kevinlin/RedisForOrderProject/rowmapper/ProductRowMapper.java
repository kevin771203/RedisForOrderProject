package org.kevinlin.springbootmall.rowmapper;

import org.kevinlin.springbootmall.constant.ProductCategory;
import org.kevinlin.springbootmall.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {


    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {

        Product product = new Product();
        product.setProductId(resultSet.getLong("product_id"));
        product.setProductName(resultSet.getString("product_name"));
        product.setCategory(getStringProductCategory(resultSet, "category"));
        product.setImageUrl(resultSet.getString("image_url"));
        product.setPrice(resultSet.getInt("price"));
        product.setStock(resultSet.getInt("stock"));
        product.setDescription(resultSet.getString("description"));
        product.setCreatedDate(resultSet.getTimestamp("created_date"));
        product.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));

        return product;
    }

    private static ProductCategory getStringProductCategory(ResultSet resultSet, String category) throws SQLException {
        return ProductCategory.valueOf(resultSet.getString(category));
    }
}
