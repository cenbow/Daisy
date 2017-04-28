package com.yourong.common.eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by py on 2015/7/8.
 */
public class EventBusPostProcessor implements BeanPostProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventBus eventBus;

    @Autowired
    private AsyncEventBus asyncEventBus;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        //是否异步
        boolean isAsync = false;
        if (bean.getClass().isAssignableFrom(AsyncEventListener.class)) {
            isAsync = true;
        }
        if (bean.getClass().isAssignableFrom(EventListener.class)) {
            isAsync = false;
        }
        registerEventBus(bean, beanName, methods, isAsync);
        return bean;
    }

    private void registerEventBus(Object bean, String beanName, Method[] methods, boolean isAsync) {
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Subscribe.class)) {
                    registeBusType(bean, beanName, isAsync, method);
                    return;
                }
            }
        }
    }

    private void registeBusType(Object bean, String beanName, boolean isAsync, Method method) {
        if (isAsync) {
            asyncEventBus.register(bean);
            log.debug("Bean {} containing method {} was subscribed to {}",
                    new Object[]{
                            beanName, method.getName(),
                            AsyncEventBus.class.getCanonicalName()
                    });
        } else {
            eventBus.register(bean);
            log.debug("Bean {} containing method {} was subscribed to {}",
                    new Object[]{
                            beanName, method.getName(),
                            EventBus.class.getCanonicalName()
                    });
        }
    }

}
