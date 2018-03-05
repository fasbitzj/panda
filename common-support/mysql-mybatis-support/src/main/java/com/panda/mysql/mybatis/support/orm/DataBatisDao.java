package com.panda.mysql.mybatis.support.orm;

import com.panda.mysql.mybatis.support.DataOrmDao;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBatisDao implements DataOrmDao {

    private SqlSessionTemplate sqlSession;

    private DataExecutor dataExecutor;

    @Override
    public <T> List<T> findBySQL(String nameSql, Map<String, Object> params) {
        try {
            return sqlSession.selectList(nameSql, params);
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    @Override
    public <T> T findOneBySQL(String nameSql, Map<String, Object> params) {
        try {
            return sqlSession.selectOne(nameSql, params);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public int deleteBySQL(String nameSql, Map<String, Object> params) {
        try {
            return sqlSession.delete(nameSql, params);
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public int updateBySQL(String nameSql, Map<String, Object> params) {
        try {
            return sqlSession.update(nameSql, params);
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public <T> int updateBySQL(String nameSql, T object) {
        try {
            return sqlSession.update(nameSql, object);
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public <T> boolean insertBySQL(String nameSql, T object) {
        return false;
    }

    @Override
    public int insertBySQL(String nameSql, Map<String, Object> params) {
        try {
            return sqlSession.insert(nameSql, params);
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public <T> int batchInsertBySQL(String nameSql, List<T> objects) {
        try {
            return sqlSession.insert(nameSql, objects);
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public <T> boolean asyncInsertBySQL(String nameSql, T object) {
        dataExecutor.execute(() -> {
            try {
                insertBySQL(nameSql, object);
            } catch (Exception e) {

            }
        });
        return true;
    }

    public SqlSessionTemplate getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public DataExecutor getDataExecutor() {
        return dataExecutor;
    }

    public void setDataExecutor(DataExecutor dataExecutor) {
        this.dataExecutor = dataExecutor;
    }
}
