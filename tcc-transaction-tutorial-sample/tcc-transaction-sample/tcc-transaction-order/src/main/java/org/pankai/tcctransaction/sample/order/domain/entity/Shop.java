package org.pankai.tcctransaction.sample.order.domain.entity;

/**
 * Created by pktczwd on 2016/12/16.
 */
public class Shop {

    private long id;

    private long ownerUserId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
