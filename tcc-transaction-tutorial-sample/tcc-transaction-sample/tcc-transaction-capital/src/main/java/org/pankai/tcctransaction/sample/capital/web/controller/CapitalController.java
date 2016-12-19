package org.pankai.tcctransaction.sample.capital.web.controller;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.capital.service.CapitalTradeOrderService;
import org.pankai.tcctransaction.sample.external.dto.CapitalTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Controller
public class CapitalController {

    @Autowired
    private CapitalTradeOrderService capitalTradeOrderService;

    @RequestMapping("/record")
    @ResponseBody
    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        return capitalTradeOrderService.record(transactionContext, tradeOrderDto);
    }

    @RequestMapping("/confirmRecord")
    @ResponseBody
    public void confirmRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        capitalTradeOrderService.confirmRecord(transactionContext, tradeOrderDto);
    }

    @RequestMapping("/record")
    @ResponseBody
    public void cancelRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        capitalTradeOrderService.cancelRecord(transactionContext, tradeOrderDto);
    }

}
