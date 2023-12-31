package com.programmers.gccoffee.repository;

import com.programmers.gccoffee.model.Category;
import com.programmers.gccoffee.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.programmers.gccoffee.repository.JdbcUtils.toLocalDateTime;
import static com.programmers.gccoffee.repository.JdbcUtils.toUUID;

@Repository
public class ProductJdbcRepository implements ProductRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ProductJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM products", productMapper);
    }

    @Override
    public Product insert(Product product) {
        var update = jdbcTemplate.update(
                "INSERT INTO products(productId, productName, category, price, description, createdAt, updatedAt) " +
                        "VALUES (UUID_TO_BIN(:productId), :productName, :category, :price, :description, :createdAt, :updatedAt)",
                toParamMap(product));

        if (update != 1) {
            throw new RuntimeException("Noting was inserted");
        }

        return product;
    }

    @Override
    public Product update(Product product) {
        return jdbcTemplate.queryForObject(
                "UPDATE products SET productName = :productName, category = :category, price = :price, description = :description, updatedAt = :updatedAt " +
                        "WHERE productId = UUID_TO_BIN(:productId)",
                toParamMap(product),
                productMapper
        );
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM products WHERE product_id = UUID_TO_BIN(:productId)",
                    Collections.singletonMap("productId", productId.toString().getBytes()),
                    productMapper
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Product> findByName(String productName) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM products WHERE product_name = :productName",
                    Collections.singletonMap("productName", productName),
                    productMapper
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return jdbcTemplate.query(
                "SELECT * FROM products WHERE category = :category",
                Collections.singletonMap("category", category.toString()),
                productMapper
        );
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM products", Collections.emptyMap());
    }

    private static final RowMapper<Product> productMapper = (resultSet, i) -> {
        var productId = toUUID(resultSet.getBytes("productId"));
        var productName = resultSet.getString("productName");
        var category = Category.valueOf(resultSet.getString("category"));
        var price = resultSet.getLong("price");
        var description = resultSet.getString("description");
        var createdAt = toLocalDateTime(resultSet.getTimestamp("createdAt"));
        var updatedAt = toLocalDateTime(resultSet.getTimestamp("updatedAt"));
        return new Product(productId, productName, category, price, description, createdAt, updatedAt);
    };

    private Map<String, Object> toParamMap(Product product) {
        var paramMap = new HashMap<String, Object>();
        paramMap.put("productId", product.getProductId().toString().getBytes());
        paramMap.put("productName", product.getProductName());
        paramMap.put("category", product.getCategory().toString());
        paramMap.put("price", product.getPrice());
        paramMap.put("description", product.getDescription());
        paramMap.put("createdAt", product.getCreatedAt());
        paramMap.put("updatedAt", product.getUpdatedAt());
        return paramMap;
    }
}
