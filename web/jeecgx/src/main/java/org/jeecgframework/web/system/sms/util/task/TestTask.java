package org.jeecgframework.web.system.sms.util.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

/**
 * Created by jiang.zheng on 2017/12/26.
 */
@Service("TestTask")
public class TestTask implements Job {

    private static final Logger logger = Logger.getLogger(TestTask.class);

    private void run() {
        logger.info("-------- TestTask run start ----------");
        logger.info("-------- TestTask run end ----------");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        run();
    }
}
