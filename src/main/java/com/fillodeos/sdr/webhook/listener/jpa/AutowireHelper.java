package com.fillodeos.sdr.webhook.listener.jpa;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *  Modified from http://guylabs.ch/2014/02/22/autowiring-pring-beans-in-hibernate-jpa-entity-listeners/
 */
@Component
public class AutowireHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private AutowireHelper() {
    }

    /**
     * Tries to autowire the specified instance of the class.     *
     * @param classToAutowire the instance of the class which holds @Autowire annotations
     */
    public static void autowire(Object classToAutowire) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        AutowireHelper.applicationContext = applicationContext;
    }
}
