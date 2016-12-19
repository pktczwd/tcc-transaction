package org.pankai.tcctransaction.sample.order.domain.repository;

import org.pankai.tcctransaction.sample.order.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Product findById(long productId) {
        return jdbcTemplate.queryForObject("select product_id,shop_id,product_name,price from ord_product where product_id = ?", new RowMapper<Product>() {
            @Override
            public Product mapRow(ResultSet rs, int i) throws SQLException {
                Product product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setShopId(rs.getLong("shop_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getBigDecimal("price"));
                return product;
            }
        }, productId);
    }
}
