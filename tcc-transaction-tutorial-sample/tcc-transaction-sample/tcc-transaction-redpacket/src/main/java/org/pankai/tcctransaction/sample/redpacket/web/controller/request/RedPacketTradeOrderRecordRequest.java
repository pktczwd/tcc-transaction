package org.pankai.tcctransaction.sample.redpacket.web.controller.request;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.RedPacketTradeOrderDto;

/**
 * Created by pktczwd on 2016/12/26.
 */
public class RedPacketTradeOrderRecordRequest {

    private TransactionContext transactionContext;
    private RedPacketTradeOrderDto redPacketTradeOrderDto;

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public void setTransactionContext(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    public RedPacketTradeOrderDto getRedPacketTradeOrderDto() {
        return redPacketTradeOrderDto;
    }

    public void setRedPacketTradeOrderDto(RedPacketTradeOrderDto redPacketTradeOrderDto) {
        this.redPacketTradeOrderDto = redPacketTradeOrderDto;
    }
}
