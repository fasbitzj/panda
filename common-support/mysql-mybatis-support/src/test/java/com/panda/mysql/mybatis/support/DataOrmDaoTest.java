package com.panda.mysql.mybatis.support;


import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataOrmDaoTest {

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/*.xml");
    DataOrmDao dataOrmDao = context.getBean(DataOrmDao.class);
    private static final String NAME_SPACE = "DemoMapper.";

    @Test
    public void findBySQL() {
        Map<String, Object> param = new HashMap<>();
        List<Demo> list = dataOrmDao.findBySQL(NAME_SPACE + "findAll", param);
        if (null != list) {
            System.out.println(list.size());
            System.out.println(list);
        }
    }
}