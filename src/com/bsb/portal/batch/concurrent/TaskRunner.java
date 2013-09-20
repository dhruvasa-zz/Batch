package com.bsb.portal.batch.concurrent;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 7:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskRunner implements TaskObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

    private BlockingQueue<CallableTask> taskQueue;
    private final ListenableTaskExecutorService service;
    private final LinkedList<Future> futures;
    private final Lock futureLock = new ReentrantLock();
    private final Condition allFuturesComplete = futureLock.newCondition();

    public TaskRunner(int threadsCount) {
        this.service = new ListenableTaskExecutorService(threadsCount);
        this.futures = new LinkedList<Future>();
        this.taskQueue = new ArrayBlockingQueue<CallableTask>(50);
    }

    public void addTaskToQueue(CallableTask callableTask) throws InterruptedException {
        taskQueue.put(callableTask);
    }

    public int getFutureSize() {
        return futures.size();
    }

    public void update(ObservableTask task) {
        if (task.getStatus() == ObservableTask.Status.cSTATUS_FAIL) {
            LOGGER.error("Error processing task [{}]. ", task.getName(), task.getException());
        } else {
            LOGGER.info("Task [{}] successfully finished.", task.getName());
        }
    }

    public void process() {
        while (true) {
            try {
                CallableTask task = taskQueue.take();
                task.addObserver(this);

                final Future future = service.submit(task);
                futures.add(future);
                LOGGER.info("Submitted task [{}], future [{}], number of current tasks [{}]", new Object[]{task.getName(), future, futures.size()});

                Futures.addCallback((ListenableFuture) future, new FutureCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        doOnFuture();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        doOnFuture();
                    }

                    private void doOnFuture() {
                        futureLock.lock();
                        try {
                            futures.remove(future);
                            if (futures.size() == 0) {
                                allFuturesComplete.signal();
                            }
                        } finally {
                            futureLock.unlock();
                        }
                    }
                });
            } catch (Exception ignore) {
                LOGGER.error("Error processing tasks. Retrying..", ignore);
            }
        }
    }

    public void waitForFinish(long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        futureLock.lock();
        try {
            if (futures.size() > 0) {
                try {
                    LOGGER.info("Waiting for processing to finish for {} {}", timeout, unit.name());
                    allFuturesComplete.awaitNanos(nanos);

                } catch (InterruptedException ignore) {
                    LOGGER.error("Thread {} interrupted while waiting", Thread.currentThread(), ignore);
                }
            }
        } finally {
            futureLock.unlock();
        }
    }
}
