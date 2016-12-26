package org.pankai.tcctransaction.sample.redpacket.service;

import org.pankai.tcctransaction.Compensable;
import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.RedPacketTradeOrderDto;
import org.pankai.tcctransaction.sample.redpacket.domain.entity.RedPacketAccount;
import org.pankai.tcctransaction.sample.redpacket.domain.entity.TradeOrder;
import org.pankai.tcctransaction.sample.redpacket.domain.repository.RedPacketAccountRepository;
import org.pankai.tcctransaction.sample.redpacket.domain.repository.TradeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pktczwd on 2016/12/26.
 */
@Service
public class RedPacketTradeOrderService {

    @Autowired
    private RedPacketAccountRepository redPacketAccountRepository;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    //trying阶段,新建draft状态订单,扣除账户金额.
    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    @Transactional
    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto redPacketTradeOrderDto) {
        System.out.println("red packet try record called.");

        RedPacketAccount transferFromAccount = redPacketAccountRepository.findByUserId(redPacketTradeOrderDto.getSelfUserId());
        if (transferFromAccount == null) {
            throw new RuntimeException("指定用户没有红包账户.");
        }

        TradeOrder tradeOrder = new TradeOrder(redPacketTradeOrderDto.getSelfUserId(), redPacketTradeOrderDto.getOppositeUserId(), redPacketTradeOrderDto.getMerchantOrderNo(), redPacketTradeOrderDto.getAmount());

        tradeOrderRepository.insert(tradeOrder);

        transferFromAccount.transferFrom(redPacketTradeOrderDto.getAmount());

        redPacketAccountRepository.save(transferFromAccount);

        return "success";
    }

    @Transactional
    public void confirmRecord(TransactionContext transactionContext, RedPacketTradeOrderDto redPacketTradeOrderDto) {
        System.out.println("red packet confirm record called.");

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(redPacketTradeOrderDto.getMerchantOrderNo());
        if (tradeOrder == null) {
            throw new RuntimeException("指定的订单不存在,订单号:" + redPacketTradeOrderDto.getMerchantOrderNo());
        }

        tradeOrder.confirm();
        tradeOrderRepository.update(tradeOrder);

        //出于测试目的,将红包余额加回去.
        RedPacketAccount transferToAccount = redPacketAccountRepository.findByUserId(redPacketTradeOrderDto.getOppositeUserId());
        transferToAccount.transferTo(redPacketTradeOrderDto.getAmount());

        redPacketAccountRepository.save(transferToAccount);
    }

    @Transactional
    public void cancelRecord(TransactionContext transactionContext, RedPacketTradeOrderDto redPacketTradeOrderDto) {
        System.out.println("red packet cancel record called");

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(redPacketTradeOrderDto.getMerchantOrderNo());

        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);

            RedPacketAccount capitalAccount = redPacketAccountRepository.findByUserId(redPacketTradeOrderDto.getSelfUserId());

            capitalAccount.cancelTransfer(redPacketTradeOrderDto.getAmount());

            redPacketAccountRepository.save(capitalAccount);
        }

    }
}
