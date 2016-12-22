package org.pankai.tcctransaction.sample.order.domain.repository;

import org.pankai.tcctransaction.sample.order.domain.entity.Order;
import org.pankai.tcctransaction.sample.order.domain.entity.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createOrder(Order order) {
        KeyHolder orderKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement("insert into ord_order(payer_user_id,payee_user_id,red_packet_pay_amount,capital_pay_amount,status,merchant_order_no) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setObject(1, order.getPayerUserId());
                stmt.setObject(2, order.getPayeeUserId());
                stmt.setObject(3, order.getRedPacketPayAmount());
                stmt.setObject(4, order.getCapitalPayAmount());
                stmt.setObject(5, order.getStatus());
                stmt.setObject(6, order.getMerchantOrderNo());
                return stmt;
            }
        }, orderKeyHolder);
        order.setId(orderKeyHolder.getKey().longValue());
        for (OrderLine orderLine : order.getOrderLines()) {
            KeyHolder orderLineKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement stmt = connection.prepareStatement("insert into ord_order_line(product_id,quantity,unit_price) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    stmt.setObject(1, orderLine.getProductId());
                    stmt.setObject(2, orderLine.getQuantity());
                    stmt.setObject(3, orderLine.getUnitPrice());
                    return stmt;
                }
            }, orderLineKeyHolder);
            orderLine.setId(orderKeyHolder.getKey().longValue());
        }
    }

    public void updateOrder(Order order) {
        jdbcTemplate.update("update ord_order set status = ?,red_packet_pay_amount = ?,capital_pay_amount = ? where order_id = ?", order.getStatus(), order.getRedPacketPayAmount(), order.getCapitalPayAmount(), order.getId());
    }

}
