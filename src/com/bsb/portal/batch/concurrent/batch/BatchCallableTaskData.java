package com.bsb.portal.batch.concurrent.batch;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */

class BatchCallableTaskData {
    private String className;
    private String methodName;

    BatchCallableTaskData(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
