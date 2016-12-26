package org.pankai.tcctransaction;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.api.TransactionStatus;
import org.pankai.tcctransaction.api.TransactionXid;
import org.pankai.tcctransaction.common.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pankai on 2016/11/13.
 */
public class Transaction implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    private TransactionXid xid;
    private TransactionStatus status;
    private TransactionType transactionType;
    private volatile int retriedCount = 0;
    private Date createTime = new Date();
    private Date lastUpdateTime = new Date();
    private long version = 1;

    private List<Participant> participants = new ArrayList<>();

    private Map<String, Object> attachments = new ConcurrentHashMap<>();

    public Transaction() {
    }

    public Transaction(TransactionContext transactionContext) {
        this.xid = transactionContext.getXid();
        this.status = TransactionStatus.TRYING;
        this.transactionType = TransactionType.BRANCH;
    }

    public Transaction(TransactionType transactionType) {
        this.xid = new TransactionXid();
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public void enlistParticipant(Participant participant) {
        //logging......start
        logger.debug("Enlist participant.");
        Terminator terminator = participant.getTerminator();
        if (terminator != null) {
            logger.debug("Confirm class is:" + terminator.getConfirmInvocationContext().getTargetClass().getName());
            logger.debug("Confirm method is:" + terminator.getConfirmInvocationContext().getMethodName());
            logger.debug("Cancel class is:" + terminator.getCancelInvocationContext().getTargetClass().getName());
            logger.debug("Cancel method is:" + terminator.getCancelInvocationContext().getMethodName());
        }
        //logging......end
        participants.add(participant);
    }

    public Xid getXid() {
        return xid.clone();
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void changeStatus(TransactionStatus status) {
        this.status = status;
    }

    public void commit() {
        for (Participant participant : participants) {
            participant.commit();
        }
    }

    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void addRetriedCount() {
        this.retriedCount++;
    }

    public void resetRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public long getVersion() {
        return version;
    }

    public void updateVersion() {
        this.version++;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void updateTime() {
        this.lastUpdateTime = new Date();
    }

}
