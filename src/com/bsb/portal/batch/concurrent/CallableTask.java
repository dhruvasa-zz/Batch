package com.bsb.portal.batch.concurrent;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */

public abstract class CallableTask<T> implements Callable<T>, ObservableTask {
    protected List<TaskObserver> observers = Lists.newLinkedList();
    protected String name;
    protected Throwable exception;
    protected Status status;
    protected T result;

    protected CallableTask(String name) {
        this.name = name;
    }

    @Override
    public T call() throws Exception {
        try {
            status = ObservableTask.Status.cSTATUS_RUNNNING;
            result = process();
            status = ObservableTask.Status.cSTATUS_SUCCESS;
        } catch (Throwable e) {
            status = ObservableTask.Status.cSTATUS_FAIL;
            exception = e;
            throw new Exception(e);
        } finally {
            notifyObservers();
        }

        return result;
    }

    public abstract T process() throws Exception;

    public String getName() {
        return name;
    }

    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (TaskObserver observer : observers) {
            observer.update(this);
        }
    }

    public Throwable getException() {
        return exception;
    }

    public Status getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }
}
