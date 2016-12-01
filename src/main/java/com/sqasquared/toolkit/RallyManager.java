package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.TaskRallyObject;

import java.io.IOException;

/**
 * Created by jimmytran on 11/29/16.
 */
public class RallyManager extends DataManager<TaskRallyObject> {

    public RallyManager() {
        super();
    }

    @Override
    public void add(TaskRallyObject task) {
        objectContainer.put(task.getFormattedID(), task);
    }

    public void loadTasks() throws IOException {
        ((RallyLoader) loader).loadTasks();
    }

    public void loadUserStory() throws IOException {
        ((RallyLoader) loader).loadUserStory();
    }

    public void refreshTasks() throws IOException {
        clearObjectContainer();
        loadTasks();
        loadUserStory();
        run();
    }

    public void loadUserInfo() throws IOException {
        ((RallyLoader) loader).loadUserInfo();
    }
}
