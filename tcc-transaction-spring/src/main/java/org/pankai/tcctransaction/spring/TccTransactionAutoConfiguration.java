package org.pankai.tcctransaction.spring;

import org.pankai.tcctransaction.interceptor.CompensableTransactionInterceptor;
import org.pankai.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import org.pankai.tcctransaction.recover.TransactionRecovery;
import org.pankai.tcctransaction.spring.aspect.TccCompensableAspect;
import org.pankai.tcctransaction.spring.aspect.TccTransactionContextAspect;
import org.pankai.tcctransaction.spring.support.TccTransactionConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by pktczwd on 2016/12/20.
 */
@Configuration
@ComponentScan
@EnableScheduling
public class TccTransactionAutoConfiguration {

    @Bean
    public TccTransactionConfigurator tccTransactionConfigurator() {
        return new TccTransactionConfigurator();
    }

    @Bean
    public TransactionRecovery transactionRecovery() {
        TransactionRecovery transactionRecovery = new TransactionRecovery();
        transactionRecovery.setTransactionConfigurator(tccTransactionConfigurator());
        return transactionRecovery;
    }

    @Bean
    public CompensableTransactionInterceptor compensableTransactionInterceptor() {
        CompensableTransactionInterceptor compensableTransactionInterceptor = new CompensableTransactionInterceptor();
        compensableTransactionInterceptor.setTransactionConfigurator(tccTransactionConfigurator());
        return compensableTransactionInterceptor;
    }

    @Bean
    public ResourceCoordinatorInterceptor resourceCoordinatorInterceptor() {
        ResourceCoordinatorInterceptor resourceCoordinatorInterceptor = new ResourceCoordinatorInterceptor();
        resourceCoordinatorInterceptor.setTransactionConfigurator(tccTransactionConfigurator());
        return resourceCoordinatorInterceptor;
    }

    @Bean
    public TccCompensableAspect tccCompensableAspect() {
        TccCompensableAspect tccCompensableAspect = new TccCompensableAspect();
        tccCompensableAspect.setCompensableTransactionInterceptor(compensableTransactionInterceptor());
        return tccCompensableAspect;
    }

    @Bean
    public TccTransactionContextAspect tccTransactionContextAspect() {
        TccTransactionContextAspect tccTransactionContextAspect = new TccTransactionContextAspect();
        tccTransactionContextAspect.setResourceCoordinatorInterceptor(resourceCoordinatorInterceptor());
        return tccTransactionContextAspect;
    }


}
