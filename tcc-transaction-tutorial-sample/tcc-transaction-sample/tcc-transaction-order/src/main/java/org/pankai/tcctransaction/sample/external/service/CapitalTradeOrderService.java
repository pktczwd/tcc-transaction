package org.pankai.tcctransaction.sample.external.service;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.CapitalTradeOrderDto;
import org.springframework.stereotype.Component;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Component
public class CapitalTradeOrderService {

    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        return null;
    }

}
