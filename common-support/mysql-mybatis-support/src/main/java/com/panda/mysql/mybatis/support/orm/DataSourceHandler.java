package com.panda.mysql.mybatis.support.orm;

public class DataSourceHandler {
    // 数据源名称线程池
    public static final ThreadLocal<String> holder = new ThreadLocal<>();

    /**
     * 项目启动时将配置读、写数据源加到holder
     * @param dataSource
     */
    public static void putDataSource(String dataSource) {
        holder.set(dataSource);
    }

    /**
     * 获取数据源字符串
     * @return
     */
    public static String getDataSource() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }
}
