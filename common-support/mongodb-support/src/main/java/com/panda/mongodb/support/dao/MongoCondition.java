package com.panda.mongodb.support.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class MongoCondition implements Serializable {
    private static final long serialVersionUID = -1L;

    private Collection<QueryItem> queryItems = new LinkedHashSet<>();
    private String orderBy;
    private boolean desc = true;  // 默认降序
    private int currentPage = 1;
    private int pageSize = 100;
    private String hint;

    /**
     * 查询结果需要排序的字段，excludeFields和includeFields互斥，二选一
     */
    private Collection<String> excludeFields = new LinkedHashSet<>();
    private Collection<String> includeFields = new LinkedHashSet<>();

    public MongoCondition setOrderBy(String orderBy, boolean isAsc) {
        this.orderBy = orderBy;
        setDesc(!isAsc);
        return this;
    }

    public MongoCondition addItem(QueryItem item) {
        queryItems.add(item);
        return this;
    }

    public MongoCondition addItem(String param, Object value, Formula formula) {
        queryItems.add(new QueryItem(param, value, formula));
        return this;
    }

    public MongoCondition addAll(Collection<QueryItem> items) {
        queryItems.addAll(items);
        return this;
    }

    public Map<String, Integer> getFields() {
        Map<String, Integer> includeMap = new HashMap<>();
        Map<String, Integer> excludeMap = new HashMap<>();

        // 限定排除
        if (excludeFields.size() > 0) {
            for (String tmp: excludeFields) {
                excludeMap.put(tmp, 0);
            }
        }

        if (includeFields.size() > 0) {
            for (String tmp: includeFields) {
                includeMap.put(tmp, 1);
            }
        }

        if (includeFields.size() > 0) {
            return includeMap;
        } else if (excludeFields.size() > 0) {
            return excludeMap;
        }
        return null;
    }


    public Collection<QueryItem> getQueryItems() {
        return queryItems;
    }

    public void setQueryItems(Collection<QueryItem> queryItems) {
        this.queryItems = queryItems;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Collection<String> getExcludeFields() {
        return excludeFields;
    }

    public void setExcludeFields(Collection<String> excludeFields) {
        this.excludeFields = excludeFields;
    }

    public Collection<String> getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(Collection<String> includeFields) {
        this.includeFields = includeFields;
    }

    @Override
    public String toString() {
        return "MongoCondition{" +
                "queryItems=" + queryItems +
                ", orderBy='" + orderBy + '\'' +
                ", desc=" + desc +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", hint='" + hint + '\'' +
                ", excludeFields=" + excludeFields +
                ", includeFields=" + includeFields +
                '}';
    }
}
