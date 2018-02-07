package test;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiang.zheng on 2018/1/10.
 */
public class MapTest {

    @Test
    public void test() {
        Map map = new HashMap();
        map.put("key1", "value1");
        System.out.println(map.isEmpty());
    }
}
