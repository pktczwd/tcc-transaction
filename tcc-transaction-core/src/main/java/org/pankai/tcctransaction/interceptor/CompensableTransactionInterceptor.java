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
        logger.info("Method Type:" + methodType);
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
            //在Trying阶段发生了异常,要求rollback事务.
            transactionConfigurator.getTransactionManager().rollback();
            throw tryingException;
        }
        //在Trying阶段全部正常执行了,则执行commit操作.
        transactionConfigurator.getTransactionManager().commit();
        return returnValue;
    }

    /**
     * 服务提供方自动TCC型事务.
     */
    private Object providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {
        switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
            //服务提供方收到trying请求时,开启事务,并且执行trying逻辑.
            case TRYING:
                transactionConfigurator.getTransactionManager().propagationNewBegin(transactionContext);
                return pjp.proceed();
            //服务提供方收到confirming请求时,从本地事务库中读取事务,由事务中保存的信息反射调用confirming逻辑.
            case CONFIRMING:
                try {
                    //根据调用方传递来的transactionContext找出本地的分支事务.
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().commit();
                } catch (NoExistedTransactionException exception) {
                    //the transaction has been commit, ignore it.
                }
                break;
            //服务提供方收到cancelling请求时,从本地事务库中读取事务,由事务中保存的信息反射调用cancelling逻辑.
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
