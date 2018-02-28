package org.jeecgframework.web.system.sms.service.impl;

import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.web.system.sms.service.WarehouseCountServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiang.zheng on 2018/1/2.
 */
@Service("warehouseCountService")
@Transactional
public class WarehouseCountServiceImpl extends CommonServiceImpl implements WarehouseCountServiceI {
}
