package org.pankai.tcctransaction.sample.capital.domain.repository;

import org.pankai.tcctransaction.sample.capital.domain.entity.TradeOrder;
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
public class TradeOrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(TradeOrder tradeOrder) {
        jdbcTemplate.update("insert into cap_trade_order(self_user_id,opposite_user_id,merchant_order_no,amount,status) values(?,?,?,?,?)", tradeOrder.getSelfUserId(), tradeOrder.getOppositeUserId(), tradeOrder.getMerchantOrderNo(), tradeOrder.getAmount(), tradeOrder.getStatus());
    }

    public void update(TradeOrder tradeOrder) {
        jdbcTemplate.update("update cap_trade_order set status = ? where id = ?", tradeOrder.getStatus(), tradeOrder.getId());
    }

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo) {
        return jdbcTemplate.queryForObject("select * from cap_trade_order where merchant_order_no = ?", new RowMapper<TradeOrder>() {
            @Override
            public TradeOrder mapRow(ResultSet rs, int i) throws SQLException {
                TradeOrder tradeOrder = new TradeOrder();
                tradeOrder.setId(rs.getLong("id"));
                tradeOrder.setSelfUserId(rs.getLong("self_user_id"));
                tradeOrder.setOppositeUserId(rs.getLong("opposite_user_id"));
                tradeOrder.setMerchantOrderNo(rs.getString("merchant_order_no"));
                tradeOrder.setAmount(rs.getBigDecimal("amount"));
                tradeOrder.setStatus(rs.getString("status"));
                return tradeOrder;
            }
        },merchantOrderNo);
    }
}
