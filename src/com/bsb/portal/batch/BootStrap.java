package com.bsb.portal.batch;

import com.bsb.portal.batch.concurrent.TaskRunner;
import com.bsb.portal.batch.concurrent.TaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class BootStrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

        TaskRunner taskRunner = new TaskRunner(5);
        TaskScheduler taskScheduler = (TaskScheduler) ctx.getBean("batchTaskScheduler");
        taskScheduler.setTaskRunner(taskRunner);

        taskRunner.process();
    }
}
