package org.pankai.tcctransaction.sample.external.service;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.RedPacketTradeOrderDto;
import org.springframework.stereotype.Component;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Component
public class RedPacketTradeOrderService {

    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        return null;
    }
}
