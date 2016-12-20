package org.pankai.tcctransaction.sample.order.web;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.pankai.tcctransaction.sample.order.service.PlaceOrderService;
import org.pankai.tcctransaction.sample.order.web.request.PlaceOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.security.InvalidParameterException;

/**
 * Created by pktczwd on 2016/12/16.
 */
@Controller
public class OrderController {

    @Autowired
    private PlaceOrderService placeOrderService;

    @RequestMapping(value = "/placeorder", method = RequestMethod.POST)
    @ResponseBody
    public String placeOrder(@RequestParam String redPacketPayAmount,
                             @RequestParam long shopId,
                             @RequestParam long payerUserId,
                             @RequestParam long productId) {

        PlaceOrderRequest request = buildRequest(redPacketPayAmount, shopId, payerUserId, productId);
        return placeOrderService.placeOrder(request.getPayerUserId(), request.getShopId(), request.getProductQuantities(), request.getRedPacketPayAmount());
    }

    private PlaceOrderRequest buildRequest(String redPacketPayAmount, long shopId, long payerUserId, long productId) {
        BigDecimal redPacketPayAmountInBigDecimal = new BigDecimal(redPacketPayAmount);
        if (redPacketPayAmountInBigDecimal.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidParameterException("invalid red packet amount :" + redPacketPayAmount);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setPayerUserId(payerUserId);
        request.setShopId(shopId);
        request.setRedPacketPayAmount(new BigDecimal(redPacketPayAmount));
        request.getProductQuantities().add(new ImmutablePair<Long, Integer>(productId, 1));
        return request;
    }
}
