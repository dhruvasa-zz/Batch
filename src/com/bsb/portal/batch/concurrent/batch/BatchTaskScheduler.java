package com.bsb.portal.batch.concurrent.batch;

import au.com.bytecode.opencsv.CSVReader;
import com.bsb.portal.batch.concurrent.CallableTask;
import com.bsb.portal.batch.concurrent.TaskRunner;
import com.bsb.portal.batch.concurrent.TaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/09/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class BatchTaskScheduler implements TaskScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchTaskScheduler.class);
    private TaskRunner taskRunner;

    private Map<String, List<BatchCallableTaskData>> taskMap;

    @PostConstruct
    private void init() {
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("delivery.properties")));
            List<String[]> fileData = csvReader.readAll();
            taskMap = new LinkedHashMap<String, List<BatchCallableTaskData>>();

            for (String[] row : fileData) {
                if (row.length < 3) {
                    continue;
                }

                List<BatchCallableTaskData> batchCallableTaskDataList = taskMap.get(row[0]);
                if (batchCallableTaskDataList == null) {
                    batchCallableTaskDataList = new ArrayList<BatchCallableTaskData>();
                    taskMap.put(row[0], batchCallableTaskDataList);
                }
                BatchCallableTaskData batchCallableTaskData = new BatchCallableTaskData(row[1], row[2]);
                batchCallableTaskDataList.add(batchCallableTaskData);
            }
        } catch (Exception e) {
            LOGGER.error("Error preparing taskMap from configuration file", e);
        }
    }

    public List<CallableTask> prepareTaskList() {
        List<CallableTask> taskList = new ArrayList<CallableTask>();
        Iterator<String> itr = taskMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            List<BatchCallableTaskData> batchCallableTaskDataList = taskMap.get(key);
            taskList.add(new BatchCallableTask(key, batchCallableTaskDataList));
        }
        return taskList;
    }

    @Scheduled(cron = "0/05 0/1 * 1/1 * ?")
    public void schedule() {
        try {
            taskRunner.waitForFinish(5, TimeUnit.SECONDS);

            if (taskRunner.getFutureSize() > 0) {
                LOGGER.info("{} tasks are still in processing..Submitting new tasks..", taskRunner.getFutureSize());
            }

            List<CallableTask> taskList = prepareTaskList();

            for (CallableTask callableTask : taskList) {
                taskRunner.addTaskToQueue(callableTask);
            }
        } catch (Exception e) {
            LOGGER.error("Error submitting tasks to task runner for processing", e);
        }
    }

    @Override
    public void setTaskRunner(TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }
}
