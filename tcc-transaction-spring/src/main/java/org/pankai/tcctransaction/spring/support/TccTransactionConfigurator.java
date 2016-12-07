package org.pankai.tcctransaction.spring.support;

import org.pankai.tcctransaction.TransactionManager;
import org.pankai.tcctransaction.TransactionRepository;
import org.pankai.tcctransaction.recover.RecoverConfig;
import org.pankai.tcctransaction.spring.recover.DefaultRecoverConfig;
import org.pankai.tcctransaction.support.TransactionConfigurator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by pktczwd on 2016/12/7.
 */
public class TccTransactionConfigurator implements TransactionConfigurator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;

    private TransactionManager transactionManager = new TransactionManager(this);


    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
