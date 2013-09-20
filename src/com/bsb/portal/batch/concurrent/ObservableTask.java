package com.bsb.portal.batch.concurrent;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 7:14 PM
 * To change this template use File | Settings | File Templates.
 */

public interface ObservableTask {
    enum Status {
        cSTATUS_INIT,
        cSTATUS_SUCCESS,
        cSTATUS_FAIL,
        cSTATUS_RUNNNING,
        cSTATUS_INTERRUPT
    }

    public String getName();

    public void addObserver(TaskObserver observer);

    public void removeObserver(TaskObserver observer);

    public void notifyObservers();

    public Throwable getException();

    public Status getStatus();
}
