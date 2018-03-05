package com.panda.mysql.mybatis.support.orm;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingDataSource extends AbstractRoutingDataSource {
    public static Map<String, List<String>> method_type_map = new HashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHandler.getDataSource();
    }

    public void setMethodType(Map<String, String> map) {
        for (String key : map.keySet()) {
            List<String> values = new ArrayList<>();
            String[] types = map.get(key).split(",");
            for (String type: types) {
                if (!StringUtils.isEmpty(type)) {
                    values.add(type);
                }
            }
            method_type_map.put(key, values);
        }
    }
}
