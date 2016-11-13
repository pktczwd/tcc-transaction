package org.pankai.tcctransaction.repository;

import org.pankai.tcctransaction.Transaction;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;

/**
 * Created by pankai on 2016/11/13.
 * this repository is suitable for single node, not for cluster nodes.
 */
public class FileSystemTransactionRepository extends CachableTransactionRepository {

    private String rootPath = "/tcc";

    private volatile boolean initialized;



    @Override

    protected int doCreate(Transaction transaction) {
        return 0;
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        return 0;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        return 0;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        return null;
    }

    @Override
    protected List<Transaction> doFindAllUnmodifiedSince(Date date) {
        return null;
    }
}
