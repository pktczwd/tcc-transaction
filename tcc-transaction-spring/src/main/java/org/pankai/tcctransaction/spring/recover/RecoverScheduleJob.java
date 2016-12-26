package org.pankai.tcctransaction.spring.recover;

import org.pankai.tcctransaction.recover.TransactionRecovery;
import org.pankai.tcctransaction.spring.support.TccTransactionConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 原文使用quartz驱动.这里重构为使用spring schedule.
 * Created by pktczwd on 2016/12/16.
 */
@Component
public class RecoverScheduleJob {

    @Autowired
    private TransactionRecovery transactionRecovery;
    @Autowired
    private TccTransactionConfigurator tccTransactionConfigurator;
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @PostConstruct
    public void init() {
        threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                transactionRecovery.startRecover();
            }
        }, new CronTrigger(tccTransactionConfigurator.getRecoverConfig().getCronExpression()));
    }
}
