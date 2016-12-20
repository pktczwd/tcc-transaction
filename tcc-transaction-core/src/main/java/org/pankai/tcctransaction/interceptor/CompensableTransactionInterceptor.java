package org.pankai.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.pankai.tcctransaction.NoExistedTransactionException;
import org.pankai.tcctransaction.OptimistLockException;
import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.api.TransactionStatus;
import org.pankai.tcctransaction.common.MethodType;
import org.pankai.tcctransaction.support.TransactionConfigurator;
import org.pankai.tcctransaction.utils.CompensableMethodUtils;
import org.pankai.tcctransaction.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by pktczwd on 2016/12/14.
 */
public class CompensableTransactionInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(CompensableTransactionInterceptor.class);

    private TransactionConfigurator transactionConfigurator;

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    /**
     * 根据方法中的TransactionContext来确定需要执行的方法.
     */
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {
        TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());
        logger.info("CompensableTransactionInterceptor interceptCompensableMethod method called.");
        if (transactionContext != null) {
            logger.info("Transaction ID:" + transactionContext.getXid().toString());
            logger.info("Transaction status:" + transactionContext.getStatus());
        } else {
            logger.info("Transaction Context is null.");
        }
        MethodType methodType = CompensableMethodUtils.calculateMethodType(transactionContext, true);
        logger.info("Method Type:" + methodType
        );
        switch (methodType) {
            case ROOT:
                return rootMethodProceed(pjp);
            case PROVIDER:
                return providerMethodProceed(pjp, transactionContext);
            default:
                return pjp.proceed();
        }
    }

    /**
     * 调用方自动TCC型事务.
     * 调用方的代码是直接调用.
     */
    private Object rootMethodProceed(ProceedingJoinPoint pjp) throws Throwable {
        transactionConfigurator.getTransactionManager().begin();
        Object returnValue;
        try {
            returnValue = pjp.proceed();
        } catch (OptimistLockException e) {
            throw e;//do not rollback, waiting for recovery job
        } catch (Throwable tryingException) {
            logger.warn("Compensable transaction trying failed.", tryingException);
            transactionConfigurator.getTransactionManager().rollback();
            throw tryingException;
        }

        transactionConfigurator.getTransactionManager().commit();
        return returnValue;
    }

    /**
     * 服务提供方自动TCC型事务.
     * 服务方的代码TRYING阶段为直接调用,CONFIRMING和CANCELLING阶段为反射调用.
     */
    private Object providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {
        switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
            case TRYING:
                transactionConfigurator.getTransactionManager().propagationNewBegin(transactionContext);
                return pjp.proceed();
            case CONFIRMING:
                try {
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().commit();
                } catch (NoExistedTransactionException exception) {
                    //the transaction has been commit, ignore it.
                }
                break;
            case CANCELLING:
                try {
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().rollback();
                } catch (NoExistedTransactionException exception) {
                    //the transaction has been rollback, ignore it.
                }
        }

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        return ReflectionUtils.getNullValue(method.getReturnType());
    }
}
