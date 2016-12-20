package org.pankai.tcctransaction.sample.external.service;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.CapitalTradeOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Component
public class CapitalTradeOrderService {

    private static Logger logger = LoggerFactory.getLogger(CapitalTradeOrderService.class);

    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        logger.info("CapitalTradeOrderService record method called.");
        logger.info("Transaction ID:" + transactionContext.getXid().toString());
        logger.info("Transaction status:" + transactionContext.getStatus());
        return null;
    }

}
