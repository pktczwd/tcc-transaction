package org.pankai.tcctransaction;

import org.pankai.tcctransaction.support.BeanFactoryAdapter;
import org.pankai.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by pankai on 2016/11/13.
 */
public class Terminator implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Terminator.class);

    private InvocationContext confirmInvocationContext;
    private InvocationContext cancelInvocationContext;

    public Terminator() {

    }

    public Terminator(InvocationContext confirmInvocationContextm, InvocationContext cancelInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContextm;
        this.cancelInvocationContext = cancelInvocationContext;
    }

    public void commit() {
        logger.info("Transaction participant commit operation.");
        invoke(confirmInvocationContext);
    }

    public void rollback() {
        logger.info("Transaction participant rollback operation.");
        invoke(cancelInvocationContext);
    }

    private Object invoke(InvocationContext invocationContext) {
        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {
            try {
                Object target = BeanFactoryAdapter.getBean(invocationContext.getTargetClass());
                if (target == null && !invocationContext.getTargetClass().isInterface()) {
                    target = invocationContext.getTargetClass().newInstance();
                }
                Method method = null;
                method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());
                logger.info("Target class is:" + target.getClass().getName());
                logger.info("Target method is:" + method.getName());
                return method.invoke(target, invocationContext.getArgs());
            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
        return null;
    }

    public InvocationContext getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public void setConfirmInvocationContext(InvocationContext confirmInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContext;
    }

    public InvocationContext getCancelInvocationContext() {
        return cancelInvocationContext;
    }

    public void setCancelInvocationContext(InvocationContext cancelInvocationContext) {
        this.cancelInvocationContext = cancelInvocationContext;
    }
}
