package org.pankai.tcctransaction.common;

/**
 * Created by pankai on 2016/11/13.
 */
public enum TransactionType {

    ROOT(1),
    BRANCH(2);

    int id;

    TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionType valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }
}
