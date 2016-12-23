package org.pankai.tcctransaction.sample.capital.service;

import org.pankai.tcctransaction.Compensable;
import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.capital.domain.entity.CapitalAccount;
import org.pankai.tcctransaction.sample.capital.domain.entity.TradeOrder;
import org.pankai.tcctransaction.sample.capital.domain.repository.CapitalAccountRepository;
import org.pankai.tcctransaction.sample.capital.domain.repository.TradeOrderRepository;
import org.pankai.tcctransaction.sample.external.dto.CapitalTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Service
public class CapitalTradeOrderService {

    @Autowired
    private CapitalAccountRepository capitalAccountRepository;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    @Transactional
    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        System.out.println("capital try record called");

        //创建订单之前就检查用户账户是否存在.用户账户存在才允许创建订单.
        CapitalAccount transferFromAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
        if (transferFromAccount == null) {
            throw new RuntimeException("指定用户的账户信息不存在.");
        }

        TradeOrder tradeOrder = new TradeOrder(
                tradeOrderDto.getSelfUserId(),
                tradeOrderDto.getOppositeUserId(),
                tradeOrderDto.getMerchantOrderNo(),
                tradeOrderDto.getAmount()
        );

        tradeOrderRepository.insert(tradeOrder);

        //在trying阶段,就进行业务前置条件检查,然后直接扣减金额.
        transferFromAccount.transferFrom(tradeOrderDto.getAmount());

        capitalAccountRepository.save(transferFromAccount);
        return "success";
    }

    @Transactional
    public void confirmRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        System.out.println("capital confirm record called");

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        tradeOrder.confirm();
        tradeOrderRepository.update(tradeOrder);

        //在confirming阶段,应该仅仅改变订单状态就ok了,不必再操作账户金额.这里进行加回是为了方便测试.
        CapitalAccount transferToAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

        transferToAccount.transferTo(tradeOrderDto.getAmount());

        capitalAccountRepository.save(transferToAccount);
    }

    @Transactional
    public void cancelRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
        System.out.println("capital cancel record called");

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount capitalAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
            //这里从代码层面可能存在空指针异常,但是从业务层面不应该出现.因为在trying阶段已经限制了账户存在才能创建订单.
            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());
            capitalAccountRepository.save(capitalAccount);


        }
    }

}
