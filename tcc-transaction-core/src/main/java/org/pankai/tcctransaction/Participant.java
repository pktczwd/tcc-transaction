package org.pankai.tcctransaction;

import org.pankai.tcctransaction.api.TransactionXid;

import java.io.Serializable;

/**
 * Created by pankai on 2016/11/13.
 */
public class Participant implements Serializable {

    private TransactionXid xid;
    private Terminator terminator;

    public Participant() {

    }

    public Participant(TransactionXid xid, Terminator terminator) {
        this.xid = xid;
        this.terminator = terminator;
    }

    public void rollback() {
        terminator.rollback();
    }

    public void commit() {
        terminator.commit();
    }


}
