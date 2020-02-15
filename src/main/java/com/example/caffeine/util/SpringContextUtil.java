package com.example.caffeine.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * spring context上下文工具类
 *
 * @author Alex
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT;

    public static ApplicationContext getApplicationContext() {
        return SpringContextUtil.APPLICATION_CONTEXT;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == SpringContextUtil.APPLICATION_CONTEXT) {
            SpringContextUtil.APPLICATION_CONTEXT = applicationContext;
        }
    }

    public static <T> T getBean(Class<T> tClass) {
        return getApplicationContext().getBean(tClass);
    }

    public static <T> Map<String, T> getBeanByType(Class<T> tClass) {
        return getApplicationContext().getBeansOfType(tClass);
    }
}
