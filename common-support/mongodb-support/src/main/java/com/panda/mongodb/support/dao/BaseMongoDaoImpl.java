package com.panda.mongodb.support.dao;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import com.panda.mongodb.support.BaseMongoDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class BaseMongoDaoImpl implements InitializingBean, BaseMongoDao {
    private static final Logger logger = LogManager.getLogger(BaseMongoDaoImpl.class);

    private String database;
    private String connect;
    private int poolSize;
    private String user;
    private String password;

    private MongoClient mongoClient;
    private MongoDatabase mongoDB;

    @Override
    public boolean insertOne(String collection, Document document) {
        try {
            mongoDB.getCollection(collection).insertOne(document);
            return true;
        } catch (Exception e) {
            logger.error("insertOne error. collection:{} , document:{}, error:{}, errorInfo:{}" , collection, document.toJson(), e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void insertMany(String collection, List<Document> documentList) {
        try {
            mongoDB.getCollection(collection).insertMany(documentList);
        } catch (Exception e) {
            logger.error("insertMany error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
    }

    @Override
    public void delete(String collection, Document filter) {
        try {
            mongoDB.getCollection(collection).deleteMany(filter);
        } catch (Exception e) {
            logger.error("delete error. collection:{}, filter:{}, error:{}, errorInfo:{}", collection, filter, e.getMessage(), e);
        }
    }

    @Override
    public void deleteAll(String collection) {
        try {
            mongoDB.getCollection(collection).deleteMany(new Document());
        } catch (Exception e) {
            logger.error("deleteAll error, collection: {},error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
    }

    @Override
    public List<Document> findByCondition(String collection, MongoCondition condition) {
        try {
            if (null == condition) condition = new MongoCondition();
            Collection<QueryItem> items = condition.getQueryItems();
            // 生成查询条件
            Bson bson = createBsonByQueryItemList(items);
            // 按指定字段排序
            BasicDBObject sort = new BasicDBObject();
            if (!StringUtils.isEmpty(condition.getOrderBy())) {
                if (condition.isDesc()) {
                    sort.put(condition.getOrderBy(), -1);
                } else {
                    sort.put(condition.getOrderBy(), 1);
                }
            } else {
                sort.put("_id", 1);
            }
            int currentPage = condition.getCurrentPage();
            int pageSize = condition.getPageSize();
            if (pageSize < 0) return null;
            FindIterable<Document> iterable = null;
            if (bson == null) {
                iterable = mongoDB.getCollection(collection).find()
                        .sort(sort).skip((currentPage - 1) * pageSize).limit(pageSize);
            } else {
                iterable = mongoDB.getCollection(collection).find(bson)
                        .sort(sort).skip((currentPage - 1) * pageSize).limit(pageSize);
            }
            MongoCursor<Document> cursor = iterable.iterator();
            List<Document> objects = new ArrayList<>();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                objects.add(document);
            }
            return objects;
        } catch (Exception e) {
            logger.error("findByCondition error. collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Document findOneByCondition(String collection, MongoCondition condition) {
        try {
            if (condition == null) condition = new MongoCondition();
            Collection<QueryItem> items = condition.getQueryItems();
            // 生成查询条件
            Bson bson = createBsonByQueryItemList(items);
            if (null == bson) {
                return mongoDB.getCollection(collection).find().first();
            }
            return mongoDB.getCollection(collection).find(bson).first();
        } catch (Exception e) {
            logger.error("findByCondition error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Document findOneByCondition(String collection, Bson bson) {
        try {
            if (null == bson) return mongoDB.getCollection(collection).find().first();
            return mongoDB.getCollection(collection).find(bson).first();
        } catch (Exception e) {
            logger.error("findByCondition error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Document findOneByConditionSkip(String collection, MongoCondition condition, int skip) {
        try {
            if (null == condition) condition = new MongoCondition();
            Collection<QueryItem> items = condition.getQueryItems();
            Bson bson = createBsonByQueryItemList(items);
            if (null == bson) return mongoDB.getCollection(collection).find().skip(skip).first();
            return mongoDB.getCollection(collection).find(bson).skip(skip).first();
        } catch (Exception e) {
            logger.error("findOneByConditionSkip error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Document findByConditionSkip(String collection, Bson bson, int skip) {
        try {
            if (null == bson) return mongoDB.getCollection(collection).find().skip(skip).first();
            return mongoDB.getCollection(collection).find(bson).skip(skip).first();
        } catch (Exception e) {
            logger.error("findByConditionSkip error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public long count(String collection, MongoCondition condition) {
        try {
            if (null == condition) condition = new MongoCondition();
            Collection<QueryItem> items = condition.getQueryItems();
            Bson bson = createBsonByQueryItemList(items);
            if (null == bson) return mongoDB.getCollection(collection).count();
            return mongoDB.getCollection(collection).count(bson);
        } catch (Exception e) {
            logger.error("count error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return 0L;
    }

    @Override
    public long update(String collection, Document filter, Document update) {
        try {
            UpdateResult result = mongoDB.getCollection(collection).updateMany(filter, new Document("$set", update));
            return result.getModifiedCount();
        } catch (Exception e) {
            logger.error("update error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return 0L;
    }

    @Override
    public Document findAndUpdate(String collection, Document filter, Document update) {
        try {
            return mongoDB.getCollection(collection).findOneAndUpdate(filter, update);
        } catch (Exception e) {
            logger.error("update error, collection:{}, error:{}, errorInfo:{}", collection, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (null == mongoClient) {
                mongoClient = getMongoClient();
                mongoDB = mongoClient.getDatabase(database);
                logger.info("mongodb connect to : {} : {}", connect, mongoDB.getName());
            }
        } catch (Exception e) {
            logger.error("get database error, database: {}, error:{}", database, e.getMessage());
        }
    }

    /**
     * 格式化查询条件
     * @param items
     * @return
     */
    private Bson createBsonByQueryItemList(Collection<QueryItem> items) {
        try {
            if (null == items) return null;
            if (items.isEmpty()) return null;
            List<Bson> list = new ArrayList<>();
            for (QueryItem item: items) {
                if (null == item) continue;
                switch (item.getFormula()) {
                    case EQ:
                        list.add(Filters.eq(item.getParam(), item.getValue()));
                        break;
                    case NE:
                        list.add(Filters.ne(item.getParam(), item.getValue()));
                        break;
                    case LT:
                        list.add(Filters.lt(item.getParam(), item.getValue()));
                        break;
                    case GT:
                        list.add(Filters.gt(item.getParam(), item.getValue()));
                        break;
                    case LE:
                        list.add(Filters.lte(item.getParam(), item.getValue()));
                        break;
                    case GE:
                        list.add(Filters.gte(item.getParam(), item.getValue()));
                        break;
                    case LIKE:
                        list.add(Filters.regex(item.getParam(), Pattern.compile(
                                "^.*" + item.getValue() + ".*$",Pattern.CASE_INSENSITIVE)));
                        break;
                    case IN:
                        if (ObjectUtils.isArray(item.getValue())) {
                            Object[] values = (Object[]) item.getValue();
                            list.add(Filters.in(item.getParam(), values));
                        } else if (item.getValue() instanceof List) {
                            list.add(Filters.in(item.getParam(), ((List) item.getValue()).toArray()));
                        } else {
                            list.add(Filters.in(item.getParam(), item.getValue()));
                        }
                        break;
                    case BETWEEN:
                        Object params = item.getValue();
                        if (params != null && ObjectUtils.isArray(params)) {
                            Object[] array = (Object[]) params;
                            list.add(Filters.gte(item.getParam(), array[0]));
                            list.add(Filters.lte(item.getParam(), array[1]));
                        }
                        break;
                    case IS:
                        list.add(Filters.eq(item.getParam(), item.getValue()));
                        break;
                    case OR:
                        break;
                }

                if (list.size() > 0) {
                    return Filters.and(list);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public MongoClient getMongoClient() {
        poolSize = (poolSize <= 0) ? 10 : poolSize;
        try {
            if (StringUtils.isEmpty(connect)) return mongoClient;
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            builder.connectionsPerHost(poolSize);
            String[] hosts = connect.split(",");
            List<ServerAddress> addressList = new ArrayList<>();
            for (String host: hosts) {
                String[] path = host.split(":");
                if (path.length > 1) {
                    addressList.add(new ServerAddress(path[0], Integer.parseInt(path[1])));
                } else {
                    logger.error("invalidate config for mongodb: " + host);
                    return null;
                }
            }

            if (addressList.size() == 0) {
                logger.error("invalidate config for mongodb, nothing to do:" + connect);
                return null;
            }
            // 有用户名密码配置
            if (!StringUtils.isEmpty(user)) {
                List<MongoCredential> mongoCredentialList = new ArrayList<>();
                mongoCredentialList.add(MongoCredential.createCredential(user, database, password.toCharArray()));
                mongoClient = new MongoClient(addressList, mongoCredentialList);
            } else {
                mongoClient = new MongoClient(addressList);
            }
            mongoClient.addOption(Bytes.QUERYOPTION_SLAVEOK);  // 支持从库读数据
        } catch (Exception e) {
            logger.error("getMongoClient error:{}, errorInfo: {}", e.getMessage(), e);
        }
        return mongoClient;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getMongoDB() {
        return mongoDB;
    }

    public void setMongoDB(MongoDatabase mongoDB) {
        this.mongoDB = mongoDB;
    }
}
