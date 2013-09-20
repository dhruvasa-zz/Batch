package com.bsb.portal.batch.concurrent;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 6:19 PM
 * To change this template use File | Settings | File Templates.
 */

public interface TaskObserver {
    public void update(ObservableTask obs);
}
