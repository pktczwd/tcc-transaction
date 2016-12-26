package org.pankai.tcctransaction.sample.redpacket.domain.repository;

import org.pankai.tcctransaction.sample.redpacket.domain.entity.TradeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.List;

/**
 * Created by pktczwd on 2016/12/20.
 */
@Repository
public class TradeOrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(TradeOrder tradeOrder) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement("insert into red_trade_order(self_user_id,opposite_user_id,merchant_order_no,amount,status) values(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setObject(1, tradeOrder.getSelfUserId());
                stmt.setObject(2, tradeOrder.getOppositeUserId());
                stmt.setObject(3, tradeOrder.getMerchantOrderNo());
                stmt.setObject(4, tradeOrder.getAmount());
                stmt.setObject(5, tradeOrder.getStatus());
                return stmt;
            }
        }, keyHolder);
        tradeOrder.setId(keyHolder.getKey().longValue());
    }

    public void update(TradeOrder tradeOrder) {
        jdbcTemplate.update("update red_trade_order set status = ? where id = ?", tradeOrder.getStatus(), tradeOrder.getId());
    }

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo) {
        List<TradeOrder> list = jdbcTemplate.query("select id,self_user_id,opposite_user_id,merchant_order_no,amount,status from red_trade_order where merchant_order_no = ?", new RowMapper<TradeOrder>() {
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
        }, merchantOrderNo);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

}
