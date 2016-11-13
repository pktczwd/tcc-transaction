package org.pankai.tcctransaction;

import org.pankai.tcctransaction.api.TransactionXid;

import java.util.Date;
import java.util.List;

/**
 * Created by pankai on 2016/11/13.
 */
public interface TransactionRepository {

    int create(Transaction transaction);

    int update(Transaction transaction);

    int delete(Transaction transaction);

    Transaction findByXid(TransactionXid xid);

    List<Transaction> findAllUnmodifiedSince(Date date);
}
