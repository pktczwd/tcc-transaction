package org.pankai.tcctransaction.support;

import org.pankai.tcctransaction.TransactionManager;
import org.pankai.tcctransaction.TransactionRepository;
import org.pankai.tcctransaction.recover.RecoverConfig;

/**
 * Created by pktczwd on 2016/12/7.
 */
public interface TransactionConfigurator {

    public TransactionManager getTransactionManager();

    public TransactionRepository getTransactionRepository();

    public RecoverConfig getRecoverConfig();
}
