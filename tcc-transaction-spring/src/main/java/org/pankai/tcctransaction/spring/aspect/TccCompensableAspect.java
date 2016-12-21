package org.pankai.tcctransaction.spring.aspect;

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

    //仅拦截有@Compensable注解的方法
    //以调用方orderService举例.
    //orderService在支付的时候分别从capitalService(用户主账户)和redpacketService(用户红包)进行扣款.
    //==============调用方orderService===============
    //首先拦截orderService的方法.
    //确认其事务为调用方(方法参数列表中没有TransactionContext,但是方法上含有@Compensable)
    //开启事务
    //==============调用方orderService===============
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
