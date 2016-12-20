package org.pankai.tcctransaction.spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.pankai.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import org.springframework.core.Ordered;

/**
 * Created by pktczwd on 2016/12/15.
 */
@Aspect
public class TccTransactionContextAspect implements Ordered {

    //根据Ordered的文档显示,值越大而优先级越低,所以此类应该在TccCompensableAspect之后.
    private int order = Ordered.HIGHEST_PRECEDENCE + 1;

    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor;

    //在执行的地方进行拦截
    //所有public的,且至少有一个参数,第一个参数是TransactionContext的方法
    //或者
    //有@Compensable注解的方法
    @Pointcut("execution(public * *(org.pankai.tcctransaction.api.TransactionContext,..))||@annotation(org.pankai.tcctransaction.Compensable)")
    public void transactionContextCall() {

    }

    //所以在方法前后都会执行拦截器中的方法?有待debug时验证.
    @Around("transactionContextCall()")
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        return resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    @Override
    public int getOrder() {
        return order;
    }
}
