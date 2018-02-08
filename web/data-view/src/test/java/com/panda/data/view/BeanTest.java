package com.panda.data.view;

import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jiang.zheng on 2018/2/8.
 */
public class BeanTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:/META-INF/spring/applicationContext.xml");

        SimpleCacheManager cacheManager = context.getBean(SimpleCacheManager.class);
        cacheManager.toString();
    }
}
