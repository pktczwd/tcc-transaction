package org.pankai.tcctransaction.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.pankai.tcctransaction.OptimistLockException;
import org.pankai.tcctransaction.Transaction;
import org.pankai.tcctransaction.TransactionRepository;
import org.pankai.tcctransaction.api.TransactionXid;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pankai on 2016/11/13.
 */
public abstract class CachableTransactionRepository implements TransactionRepository {

    private int expireDuration = 300;

    private Cache<Xid, Transaction> transactionXidCompensableTransactionCache;

    @Override
    public int create(Transaction transaction) {
        int result = doCreate(transaction);
        if (result > 0) {
            putToCache(transaction);
        }
        return result;
    }

    @Override
    public int update(Transaction transaction) {
        int result = doUpdate(transaction);
        if (result > 0) {
            putToCache(transaction);
        } else {
            throw new OptimistLockException();
        }
        return result;
    }

    @Override
    public int delete(Transaction transaction) {
        int result = doDelete(transaction);
        if (result > 0) {
            removeFromCache(transaction);
        }
        return result;
    }

    @Override
    public Transaction findByXid(TransactionXid xid) {
        Transaction transaction = findFromCache(xid);
        if (transaction == null) {
            transaction = doFindOne(xid);
            if (transaction != null) {
                putToCache(transaction);
            }
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllUnmodifiedSince(Date date) {
        List<Transaction> transactions = doFindAllUnmodifiedSince(date);
        for (Transaction transaction : transactions) {
            putToCache(transaction);
        }
        return transactions;
    }

    public CachableTransactionRepository() {
        transactionXidCompensableTransactionCache = CacheBuilder.newBuilder().expireAfterAccess(expireDuration, TimeUnit.SECONDS).maximumSize(1000).build();
    }

    protected void putToCache(Transaction transaction) {
        transactionXidCompensableTransactionCache.put(transaction.getXid(), transaction);
    }

    protected void removeFromCache(Transaction transaction) {
        transactionXidCompensableTransactionCache.invalidate(transaction.getXid());
    }

    protected Transaction findFromCache(TransactionXid xid) {
        return transactionXidCompensableTransactionCache.getIfPresent(xid);
    }

    public final void setExpireDuration(int durationInSeconds) {
        this.expireDuration = durationInSeconds;
    }

    /**
     * 其实现要求将事务内容序列化进数据库.
     */
    protected abstract int doCreate(Transaction transaction);

    protected abstract int doUpdate(Transaction transaction);

    protected abstract int doDelete(Transaction transaction);

    protected abstract Transaction doFindOne(Xid xid);

    protected abstract List<Transaction> doFindAllUnmodifiedSince(Date date);

}
