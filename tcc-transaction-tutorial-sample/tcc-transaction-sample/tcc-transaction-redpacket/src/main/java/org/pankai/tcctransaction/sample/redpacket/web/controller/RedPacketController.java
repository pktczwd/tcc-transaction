package org.pankai.tcctransaction.sample.redpacket.web.controller;

import org.pankai.tcctransaction.sample.redpacket.service.RedPacketTradeOrderService;
import org.pankai.tcctransaction.sample.redpacket.web.controller.request.RedPacketTradeOrderRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by pktczwd on 2016/12/26.
 */
@Controller
public class RedPacketController {

    @Autowired
    private RedPacketTradeOrderService redPacketTradeOrderService;

    @RequestMapping(value = "/record", method = RequestMethod.POST)
    @ResponseBody
    public String RedPacketTradeOrderRecordRequest(@RequestBody RedPacketTradeOrderRecordRequest redPacketTradeOrderRecordRequest) {
        return redPacketTradeOrderService.record(redPacketTradeOrderRecordRequest.getTransactionContext(), redPacketTradeOrderRecordRequest.getRedPacketTradeOrderDto());
    }
}
