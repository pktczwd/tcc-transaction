package org.pankai.tcctransaction.sample.order.domain.service;

import org.pankai.tcctransaction.Compensable;
import org.pankai.tcctransaction.sample.external.dto.CapitalTradeOrderDto;
import org.pankai.tcctransaction.sample.external.dto.RedPacketTradeOrderDto;
import org.pankai.tcctransaction.sample.external.service.CapitalTradeOrderService;
import org.pankai.tcctransaction.sample.external.service.RedPacketTradeOrderService;
import org.pankai.tcctransaction.sample.order.domain.entity.Order;
import org.pankai.tcctransaction.sample.order.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Service
public class PaymentService {

    @Autowired
    private CapitalTradeOrderService capitalTradeOrderService;

    @Autowired
    private RedPacketTradeOrderService redPacketTradeOrderService;

    @Autowired
    private OrderRepository orderRepository;

    @Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment")
    @Transactional
    public void makePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
        System.out.println("order try make payment called");

        order.pay(redPacketPayAmount, capitalPayAmount);
        orderRepository.updateOrder(order);

        capitalTradeOrderService.record(null, buildCapitalTradeOrderDto(order));
        redPacketTradeOrderService.record(null, buildRedPacketTradeOrderDto(order));
    }

    public void confirmMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {

        System.out.println("order confirm make payment called");
        order.confirm();

        orderRepository.updateOrder(order);
    }

    public void cancelMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {

        System.out.println("order cancel make payment called");

        order.cancelPayment();

        orderRepository.updateOrder(order);
    }

    private CapitalTradeOrderDto buildCapitalTradeOrderDto(Order order) {

        CapitalTradeOrderDto tradeOrderDto = new CapitalTradeOrderDto();
        tradeOrderDto.setAmount(order.getCapitalPayAmount());
        tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
        tradeOrderDto.setSelfUserId(order.getPayerUserId());
        tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
        tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));

        return tradeOrderDto;
    }

    private RedPacketTradeOrderDto buildRedPacketTradeOrderDto(Order order) {
        RedPacketTradeOrderDto tradeOrderDto = new RedPacketTradeOrderDto();
        tradeOrderDto.setAmount(order.getRedPacketPayAmount());
        tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
        tradeOrderDto.setSelfUserId(order.getPayerUserId());
        tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
        tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));

        return tradeOrderDto;
    }

}
