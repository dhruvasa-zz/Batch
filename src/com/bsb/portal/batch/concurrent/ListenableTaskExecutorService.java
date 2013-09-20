package com.bsb.portal.batch.concurrent;

import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */

/*
   A listenable executor service through which we can attach callbacks to Future's
 */
public class ListenableTaskExecutorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenableTaskExecutorService.class);
    private final ExecutorService service;

    public ListenableTaskExecutorService(int threadsCount) {
        service = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(0, threadsCount, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>()));
    }

    public Future submit(Callable callable) {
        return service.submit(callable);
    }
}
