package com.panda.mongodb.support;

import com.panda.mongodb.support.dao.MongoCondition;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public interface BaseMongoDao {
    /**
     * insertOne
     * @param collection
     * @param document
     * @return
     */
    boolean insertOne(String collection, Document document);

    /**
     *
     * @param collection
     * @param documentList
     */
    void insertMany(String collection, List<Document> documentList);

    /**
     *
     * @param collection
     * @param filter
     */
    void delete(String collection, Document filter);

    /**
     *
     * @param collection
     */
    void deleteAll(String collection);

    /**
     *
     * @param collection
     * @param condition
     * @return
     */
    List<Document> findByCondition(String collection, MongoCondition condition);

    /**
     *
     * @param collection
     * @param filter
     * @return
     */
    Document findOneByCondition(String collection, MongoCondition condition);

    /**
     *
     * @param collection
     * @param bson
     * @return
     */
    Document findOneByCondition(String collection, Bson bson);

    /**
     *
     * @param collection
     * @param condition
     * @param skip
     * @return
     */
    Document findOneByConditionSkip(String collection, MongoCondition condition, int skip);

    /**
     *
     * @param collection
     * @param bson
     * @param skip
     * @return
     */
    Document findByConditionSkip(String collection, Bson bson, int skip);

    /**
     *
     * @param collection
     * @param condition
     * @return
     */
    long count(String collection, MongoCondition condition);

    /**
     *
     * @param collection
     * @param filter
     * @param update
     * @return
     */
    long update(String collection, Document filter, Document update);

    /**
     *
     * @param collection
     * @param filter
     * @param update
     * @return
     */
    Document findAndUpdate(String collection, Document filter, Document update);
}
