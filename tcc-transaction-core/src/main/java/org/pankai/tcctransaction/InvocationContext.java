package org.pankai.tcctransaction;

import java.io.Serializable;

/**
 * Created by pankai on 2016/11/13.
 */
public class InvocationContext implements Serializable {

    private Class targetClass;

    private String methodName;

    private Class[] parameterTypes;

    private Object[] args;

    public InvocationContext() {
    }

    public InvocationContext(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }
}
