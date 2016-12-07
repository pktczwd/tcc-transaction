package org.pankai.tcctransaction.recover;

import org.apache.log4j.Logger;
import org.pankai.tcctransaction.Transaction;
import org.pankai.tcctransaction.TransactionRepository;
import org.pankai.tcctransaction.api.TransactionStatus;
import org.pankai.tcctransaction.support.TransactionConfigurator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pktczwd on 2016/12/7.
 */
public class TransactionRecovery {

    private final Logger logger = Logger.getLogger(Transaction.class.getSimpleName());

    private TransactionConfigurator transactionConfigurator;

    private void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public void startRecover() {
        List<Transaction> transactions = loadErrorTransactions();
        recoverTransactions(transactions);
    }

    private List<Transaction> loadErrorTransactions() {
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        List<Transaction> transactions = transactionRepository.findAllUnmodifiedSince(new Date(currentTimeInMillis - transactionConfigurator.getRecoverConfig().getRecoverDuration() * 1000));
        List<Transaction> recoverTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            int result = transactionRepository.update(transaction);
            if (result > 0) {
                recoverTransactions.add(transaction);
            }
        }
        return recoverTransactions;
    }

    private void recoverTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (transaction.getRetriedCount() > transactionConfigurator.getRecoverConfig().getMaxRetryCount()) {
                logger.error(String.format("recover failed with max retry count, will not try again. txid:%s, status:%s, retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
                continue;
            }

            try {
                transaction.addRetriedCount();

                if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
                    transaction.changeStatus(TransactionStatus.CONFIRMING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.commit();
                } else {
                    transaction.changeStatus(TransactionStatus.CANCELLING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.rollback();
                }
                transactionConfigurator.getTransactionRepository().delete(transaction);
            } catch (Throwable e) {
                logger.warn(String.format("recover failed, txid:%s, status:%s, retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()), e);
            }
        }


    }
}
