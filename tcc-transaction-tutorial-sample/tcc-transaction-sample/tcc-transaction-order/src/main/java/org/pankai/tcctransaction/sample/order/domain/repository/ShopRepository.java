package org.pankai.tcctransaction.sample.order.domain.repository;

import org.pankai.tcctransaction.sample.order.domain.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pktczwd on 2016/12/16.
 */
@Repository
public class ShopRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Shop findById(long id) {
        return jdbcTemplate.queryForObject("select * from ord_shop where shop_id = ?", new RowMapper<Shop>() {
            @Override
            public Shop mapRow(ResultSet rs, int rowNum) throws SQLException {
                Shop shop = new Shop();
                shop.setId(rs.getLong("shop_id"));
                shop.setOwnerUserId(rs.getLong("owner_user_id"));
                return shop;
            }
        }, id);
    }
}
