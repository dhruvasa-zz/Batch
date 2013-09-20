package com.bsb.portal.batch.concurrent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 20/09/13
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskScheduler {

    List<CallableTask> prepareTaskList();

    void schedule();

    void setTaskRunner(TaskRunner taskRunner);
}
