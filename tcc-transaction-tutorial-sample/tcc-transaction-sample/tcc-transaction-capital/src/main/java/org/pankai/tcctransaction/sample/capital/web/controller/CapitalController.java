package org.pankai.tcctransaction.sample.capital.web.controller;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.capital.service.CapitalTradeOrderService;
import org.pankai.tcctransaction.sample.external.dto.CapitalTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Controller
public class CapitalController {

    @Autowired
    private CapitalTradeOrderService capitalTradeOrderService;

    @RequestMapping(value = "/record", method = RequestMethod.POST)
    @ResponseBody
    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        return capitalTradeOrderService.record(transactionContext, tradeOrderDto);
    }
}
