package org.pankai.tcctransaction.sample.external.service;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.RedPacketTradeOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Component
public class RedPacketTradeOrderService {

    private static final Logger logger = LoggerFactory.getLogger(RedPacketTradeOrderService.class);

    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        logger.info("RedPacketTradeOrderService record method called.");
        logger.info("Transaction ID:" + transactionContext.getXid().toString());
        logger.info("Transaction status:" + transactionContext.getStatus());
        return null;
    }
}
