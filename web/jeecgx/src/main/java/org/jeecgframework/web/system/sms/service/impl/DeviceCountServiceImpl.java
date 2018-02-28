package org.jeecgframework.web.system.sms.service.impl;

import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.web.system.sms.service.DeviceCountServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiang.zheng on 2018/1/2.
 */

@Service(value = "deliverReturnCountService")
@Transactional
public class DeviceCountServiceImpl extends CommonServiceImpl implements DeviceCountServiceI {
}
