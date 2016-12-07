package org.pankai.tcctransaction;

import org.apache.log4j.Logger;
import org.pankai.tcctransaction.support.TransactionConfigurator;

/**
 * Created by pktczwd on 2016/12/7.
 */
public class TransactionManager {

    private final Logger logger = Logger.getLogger(TransactionManager.class);

    private TransactionConfigurator transactionConfigurator;

    public TransactionManager(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
