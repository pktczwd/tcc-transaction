package org.pankai.tcctransaction;

import org.pankai.tcctransaction.support.BeanFactoryAdapter;
import org.pankai.tcctransaction.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by pankai on 2016/11/13.
 */
public class Terminator implements Serializable {

    private InvocationContext confirmInvocationContext;
    private InvocationContext cancelInvocationContext;

    public Terminator() {

    }

    public Terminator(InvocationContext confirmInvocationContextm, InvocationContext cancelInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContextm;
        this.cancelInvocationContext = cancelInvocationContext;
    }

    public void commit() {
        invoke(confirmInvocationContext);
    }

    public void rollback() {
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
                return method.invoke(target, invocationContext.getArgs());
            } catch (Exception e) {
                return new SystemException(e);
            }
        }
        return null;
    }

}
