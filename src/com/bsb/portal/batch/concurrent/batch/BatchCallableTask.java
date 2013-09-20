package com.bsb.portal.batch.concurrent.batch;

import com.bsb.portal.batch.concurrent.CallableTask;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BatchCallableTask extends CallableTask {
    private List<BatchCallableTaskData> batchCallableTaskDataList;

    protected BatchCallableTask(String name, List<BatchCallableTaskData> batchCallableTaskDataList) {
        super(name);
        this.batchCallableTaskDataList = batchCallableTaskDataList;
    }

    @Override
    public Object process() throws Exception {
        for (BatchCallableTaskData batchCallableTaskData : batchCallableTaskDataList) {
            Class klass = Class.forName(batchCallableTaskData.getClassName());
            Method m = klass.getMethod(batchCallableTaskData.getMethodName());
            m.invoke(klass.newInstance());
        }
        return null;
    }
}
