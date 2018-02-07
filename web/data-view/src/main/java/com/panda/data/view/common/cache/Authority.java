package com.panda.data.view.common.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiang.zheng on 2018/2/7.
 */
public class Authority {

    /**
     * 菜单权限
     */
    private Map<String, Object> menuMap = new HashMap<>();

    /**
     * 数据权限
     */
    private Map<String, Object> dadaMap = new HashMap<>();

    public Map<String, Object> getMenuMap() {
        return menuMap;
    }

    public void setMenuMap(Map<String, Object> menuMap) {
        this.menuMap = menuMap;
    }

    public Map<String, Object> getDadaMap() {
        return dadaMap;
    }

    public void setDadaMap(Map<String, Object> dadaMap) {
        this.dadaMap = dadaMap;
    }
}
