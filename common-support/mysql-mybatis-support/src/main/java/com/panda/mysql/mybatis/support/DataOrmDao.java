package com.panda.mysql.mybatis.support;

import java.util.List;
import java.util.Map;

public interface DataOrmDao {

    /**
     * 根据sql查询
     * @param nameSql
     * @param params
     * @param <T>
     * @return
     */
    <T> List<T> findBySQL(String nameSql, Map<String, Object> params);

    /**
     * 根据sql查询
     * @param nameSql
     * @param params
     * @param <T>
     * @return
     */
    <T> T findOneBySQL(String nameSql, Map<String, Object> params);

    /**
     * 根据sql删除数据
     * @param nameSql
     * @param params
     * @return
     */
    int deleteBySQL(String nameSql, Map<String, Object> params);

    /**
     * 根据sql更新数据
     * @param nameSql
     * @param params
     * @return
     */
    int updateBySQL(String nameSql, Map<String, Object> params);

    /**
     * 根据sql更新数据
     * @param nameSql
     * @param object
     * @return
     */
    <T> int updateBySQL(String nameSql, T object);

    /**
     * 根据sql更新数据
     * @param nameSql
     * @param object
     * @return
     */
    <T> boolean insertBySQL(String nameSql, T object);

    /**
     * 根据sql更新数据
     * @param nameSql
     * @param params
     * @return
     */
    int insertBySQL(String nameSql, Map<String, Object> params);

    /**
     * 批量插入数据
     * @param nameSql
     * @param objects
     * @param <T>
     * @return
     */
    <T> int batchInsertBySQL(String nameSql, List<T> objects);

    /**
     * 异步批量插入
     * @param nameSql
     * @param queue
     * @param <T>
     * @return
     */
    <T> boolean asyncInsertBySQL(String nameSql, T object);
}
