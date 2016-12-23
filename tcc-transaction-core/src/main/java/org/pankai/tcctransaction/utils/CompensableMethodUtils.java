package org.pankai.tcctransaction.utils;

import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.common.MethodType;

/**
 * Created by pktczwd on 2016/12/14.
 */
public class CompensableMethodUtils {

    public static MethodType calculateMethodType(TransactionContext transactionContext, boolean isCompensable) {
        if (transactionContext == null && isCompensable) {
            //isRootTransactionMethod,理解为TCC服务的发起方.在本例中为orderService.
            return MethodType.ROOT;
        } else if (transactionContext == null && !isCompensable) {
            //isSoaConsumer,理解为TCC服务的发起方对提供方的调用.在本例中为orderService调用capitalService和redpacketService两处.
            return MethodType.CONSUMER;
        } else if (transactionContext != null && isCompensable) {
            //isSoaProvider,理解为TCC服务的提供方.
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    public static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {
        int i = -1;
        for (i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(org.pankai.tcctransaction.api.TransactionContext.class)) {
                break;
            }
        }
        return i;
    }

    public static TransactionContext getTransactionContextFromArgs(Object[] args) {
        TransactionContext transactionContext = null;
        for (Object arg : args) {
            if (arg != null && org.pankai.tcctransaction.api.TransactionContext.class.isAssignableFrom(arg.getClass())) {
                transactionContext = (org.pankai.tcctransaction.api.TransactionContext) arg;
            }
        }
        return transactionContext;
    }


}
