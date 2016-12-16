package org.pankai.tcctransaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.pankai.tcctransaction.interceptor.CompensableTransactionInterceptor;
import org.springframework.core.Ordered;

/**
 * Created by pktczwd on 2016/12/16.
 */
@Aspect
public class TccCompensableAspect implements Ordered {

    //根据Ordered的文档显示,值越大而优先级越低,所以此类应该在TccTransactionContextAspect之前
    private int order = Ordered.HIGHEST_PRECEDENCE;

    private CompensableTransactionInterceptor compensableTransactionInterceptor;

    @Pointcut("@annotation(org.pankai.tcctransaction.Compensable)")
    public void compensableService() {

    }

    @Around("compensableService()")
    public Object InterceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {
        return compensableTransactionInterceptor.interceptCompensableMethod(pjp);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setCompensableTransactionInterceptor(CompensableTransactionInterceptor compensableTransactionInterceptor) {
        this.compensableTransactionInterceptor = compensableTransactionInterceptor;
    }
}
