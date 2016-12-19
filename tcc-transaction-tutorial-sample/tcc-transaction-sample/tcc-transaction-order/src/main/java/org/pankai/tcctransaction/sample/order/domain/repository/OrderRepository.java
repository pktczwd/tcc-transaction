package org.pankai.tcctransaction.sample.order.domain.repository;

import org.pankai.tcctransaction.sample.order.domain.entity.Order;
import org.pankai.tcctransaction.sample.order.domain.entity.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createOrder(Order order) {
        jdbcTemplate.update("insert into ord_order(payer_user_id,payee_user_id,red_packet_pay_amount,capital_pay_amount,status,merchant_order_no) values(?,?,?,?,?,?)", order.getPayerUserId(), order.getPayeeUserId(), order.getRedPacketPayAmount(), order.getCapitalPayAmount(), order.getMerchantOrderNo());
        for (OrderLine orderLine : order.getOrderLines()) {
            jdbcTemplate.update("insert into ord_order_line(product_id,quantity,unit_price) values(?,?,?)", orderLine.getProductId(), orderLine.getQuantity(), orderLine.getUnitPrice());
        }
    }

    public void updateOrder(Order order) {
        jdbcTemplate.update("update ord_order set status = ?,red_packet_pay_amount = ?,capital_pay_amount = ? where order_id = ?", order.getStatus(), order.getRedPacketPayAmount(), order.getCapitalPayAmount(), order.getId());
    }

}
