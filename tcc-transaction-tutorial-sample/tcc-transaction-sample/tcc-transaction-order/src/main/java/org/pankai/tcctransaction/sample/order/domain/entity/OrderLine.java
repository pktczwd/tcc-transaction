package org.pankai.tcctransaction.sample.order.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by pktczwd on 2016/12/16.
 */
public class OrderLine implements Serializable {

    private long id;

    private long productId;

    private int quantity;

    private BigDecimal unitPrice;

    public OrderLine() {

    }

    public OrderLine(Long productId, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }


}
