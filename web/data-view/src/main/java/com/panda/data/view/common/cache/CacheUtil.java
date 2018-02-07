package com.panda.data.view.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * Created by jiang.zheng on 2018/2/7.
 */
public class CacheUtil {

    private static Cache<String, Authority> cache;

    public static void init() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100).build();
    }


}
