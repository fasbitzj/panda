package test;

import org.jeecgframework.web.system.pojo.oms.DeviceCountHourEntity;
import org.jeecgframework.web.system.sms.service.impl.DeviceCountServiceImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jiang.zheng on 2018/1/3.
 */
public class SpringTest {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring-*.xml");
    DeviceCountServiceImpl deliverReturnCountService = applicationContext.getBean(DeviceCountServiceImpl.class);

    @Test
    public void test() {
        deliverReturnCountService.get(DeviceCountHourEntity.class, 1);
    }
}
