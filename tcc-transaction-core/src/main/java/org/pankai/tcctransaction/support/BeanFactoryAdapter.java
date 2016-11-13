package org.pankai.tcctransaction.support;

/**
 * Created by pankai on 2016/11/13.
 */
public class BeanFactoryAdapter {

    private static BeanFactory beanFactory;

    public static Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz);
    }

    public static void setBeanFactory(BeanFactory beanFactory) {
        BeanFactoryAdapter.beanFactory = beanFactory;
    }
}
