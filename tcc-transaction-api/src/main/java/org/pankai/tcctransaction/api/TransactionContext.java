package org.pankai.tcctransaction.api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pankai on 2016/11/13.
 */
public class TransactionContext implements Serializable{

    private TransactionXid xid;
    private int status;

    private Map<String,String> attachments = new ConcurrentHashMap<>();

    public TransactionContext(){

    }

    public TransactionContext(TransactionXid xid,int status){
        this.xid = xid;
        this.status = status;
    }

    public TransactionXid getXid() {
        return xid;
    }

    public void setXid(TransactionXid xid) {
        this.xid = xid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }
}
