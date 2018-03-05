package com.panda.mongodb.support;

import com.panda.mongodb.support.dao.BaseMongoDaoImpl;
import org.bson.Document;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class BaseMongoDaoTest {

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/*.xml");
    BaseMongoDaoImpl mongoDao = context.getBean(BaseMongoDaoImpl.class);
    private String collection = "demo";

    @Test
    public void insertOne() {
        Document doc = new Document();
        doc.put("name", "Andrew");
        doc.put("age", 18);
        boolean flag = mongoDao.insertOne(collection, doc);
        System.out.println(flag);
    }

    @Test
    public void findOneByCondition() {
        Document doc = new Document();
        doc.put("name", "Andrew");
        doc.put("age", 18);
        Document document = mongoDao.findOneByCondition(collection, doc);
        System.out.println(document);
    }
}