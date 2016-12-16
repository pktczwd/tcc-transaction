package org.pankai.tcctransaction.spring.recover;

import org.pankai.tcctransaction.recover.TransactionRecovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 原文使用quartz驱动.这里重构为使用spring schedule.
 * Created by pktczwd on 2016/12/16.
 */
@Component
public class RecoverScheduleJob {

    @Autowired
    private TransactionRecovery transactionRecovery;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000, initialDelay = 1000)
    public void init() {
        transactionRecovery.startRecover();
    }


}
