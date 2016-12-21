package org.pankai.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.pankai.tcctransaction.*;
import org.pankai.tcctransaction.Terminator;
import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.api.TransactionStatus;
import org.pankai.tcctransaction.api.TransactionXid;
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
public class ResourceCoordinatorInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ResourceCoordinatorInterceptor.class);

    private TransactionConfigurator transactionConfigurator;

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    /**
     * 确认事务的参与者
     */
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        logger.info("ResourceCoordinatorInterceptor interceptTransactionContextMethod method called.");
        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();
        if (transaction != null) {
            logger.info("Transaction ID:" + transaction.getXid().toString());
            logger.info("Transaction status:" + transaction.getStatus().toString());
        } else {
            logger.info("Transaction is null.");
        }
        if (transaction != null && transaction.getStatus().equals(TransactionStatus.TRYING)) {
            TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());
            Compensable compensable = getCompensable(pjp);
            MethodType methodType = CompensableMethodUtils.calculateMethodType(transactionContext, compensable != null ? true : false);
            logger.info("Method Type:" + methodType);
            switch (methodType) {
                //==============调用方orderService===============
                //事务已经由CompensableTransactionInterceptor打开.
                //状态为TRYING,6b0d6f1a-6d3a-39fa-aea3-ccbefced6079|d0abaf58-b691-3fdc-bd3c-ad74ef9691d1
                //且TransactionContext为空,有@Compensable,确认为ROOT.
                //==============调用方orderService===============
                case ROOT:
                    generateAndEnlistRootParticipant(pjp);
                    break;
                //==============orderService调用capitalTradeOrderService===============
                //调用时,TransactionContext为null,方法上没有@Compensable,确认为CONSUMER.
                //==============orderService调用capitalTradeOrderService===============
                case CONSUMER:
                    generateAndEnlistConsumerParticipant(pjp);
                    break;
                case PROVIDER:
                    generateAndEnlistProviderParticipant(pjp);
                    break;
            }
        }
        return pjp.proceed(pjp.getArgs());
    }

    /**
     * 看起来像基于注解生成事务的参与者.
     */
    //==============调用方orderService===============
    //orderService的confirm操作和cancel操作很好理解.
    //直接由注解上的方法注明.这里将他们写进当前事务.
    //==============调用方orderService===============
    private Participant generateAndEnlistRootParticipant(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Compensable compensable = getCompensable(pjp);
        String confirmMethodName = compensable.confirmMethod();
        String cancelMethodName = compensable.cancelMethod();

        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();

        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());

        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());

        InvocationContext confirmInvocation = new InvocationContext(targetClass, confirmMethodName, method.getParameterTypes(), pjp.getArgs());
        InvocationContext cancelInvocation = new InvocationContext(targetClass, cancelMethodName, method.getParameterTypes(), pjp.getArgs());

        Participant participant = new Participant(xid, new Terminator(confirmInvocation, cancelInvocation));

        transaction.enlistParticipant(participant);

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.update(transaction);

        return participant;
    }

    /**
     * 看起来像基于参数列表生成事务的参与者.
     */
    private Participant generateAndEnlistConsumerParticipant(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();
        //用当前事务的全局事务ID生成新的事务上下文.
        //新的事务上下文全局事务ID与原事务保持一致.
        //事务分支标识符新生成.
        //事务状态与原事务保持一致,仍然是TRYING.
        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());

        int position = CompensableMethodUtils.getTransactionContextParamPosition(signature.getParameterTypes());

        //所以此处,预留给TransactionContext的位置起了作用.
        //原本在调用远程方法的时候,此处传入的是null,在这里由tcc-transaction填入了值.
        pjp.getArgs()[position] = new TransactionContext(xid, transaction.getStatus().getId());

        //基于约定,confirm方法与cancel方法的参数及其类型,顺序,应该一样.
        Object[] tryArgs = pjp.getArgs();
        Object[] confirmArgs = new Object[tryArgs.length];
        Object[] cancelArgs = new Object[tryArgs.length];

        System.arraycopy(tryArgs, 0, confirmArgs, 0, tryArgs.length);
        //确认操作的事务上下文中,事务的状态为CONFIRMING.
        confirmArgs[position] = new TransactionContext(xid, TransactionStatus.CONFIRMING.getId());

        System.arraycopy(tryArgs, 0, cancelArgs, 0, tryArgs.length);
        //取消操作的事务上下文中,事务的状态为CANCELLING.
        cancelArgs[position] = new TransactionContext(xid, TransactionStatus.CANCELLING.getId());

        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());

        //这里并不能理解,为何方法名都是一样的?因为confirm method和cancel method应该是分开的.在这里并没有体现.
        //因为参数中的事务上下文信息包含了事务的状态信息.
        //即确认事务的时候,事务上下文的状态是CONFIRMING.
        //而取消事务的时候,事务上下文的状态是CANCELLING.
        InvocationContext confirmInvocation = new InvocationContext(targetClass, method.getName(), method.getParameterTypes(), confirmArgs);
        InvocationContext cancelInvocation = new InvocationContext(targetClass, method.getName(), method.getParameterTypes(), cancelArgs);

        Participant participant = new Participant(xid, new Terminator(confirmInvocation, cancelInvocation));

        transaction.enlistParticipant(participant);

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();

        transactionRepository.update(transaction);

        return participant;
    }

    /**
     * 服务提供方的事务参数者.
     */
    private Participant generateAndEnlistProviderParticipant(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Compensable compensable = getCompensable(pjp);

        String confirmMethodName = compensable.confirmMethod();
        String cancelMethodName = compensable.cancelMethod();

        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();

        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());

        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());

        InvocationContext confirmInvocation = new InvocationContext(targetClass, confirmMethodName, method.getParameterTypes(), pjp.getArgs());
        InvocationContext cancelInvocation = new InvocationContext(targetClass, cancelMethodName, method.getParameterTypes(), pjp.getArgs());

        Participant participant = new Participant(xid, new Terminator(confirmInvocation, cancelInvocation));

        transaction.enlistParticipant(participant);

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.update(transaction);

        return participant;
    }


    private Compensable getCompensable(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Compensable compensable = method.getAnnotation(Compensable.class);

        if (compensable == null) {
            Method targetMethod = null;
            try {
                targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
                if (targetMethod != null) {
                    compensable = targetMethod.getAnnotation(Compensable.class);
                }
            } catch (NoSuchMethodException e) {
                compensable = null;
            }
        }

        return compensable;
    }
}
