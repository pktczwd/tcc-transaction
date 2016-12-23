package org.pankai.tcctransaction.common;

/**
 * Created by pankai on 2016/11/13.
 * 事务类型
 * 事务类型分为主事务和分支事务.
 * 主事务会自动恢复.
 * 分支事务由主事务触发恢复.
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
