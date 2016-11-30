package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TaskRallyObject;
import com.sun.javafx.tk.Toolkit;

import java.io.IOException;

/**
 * Created by jimmytran on 11/29/16.
 */
public class RallyManager extends DataManager<TaskRallyObject>{

    public RallyManager(){
        super();
    }

    @Override
    public void add(TaskRallyObject task){
        objectContainer.put(task.getFormattedID(), task);
    }

    public void loadTasks() throws IOException {
        loader.loadTasks(this);
    }

    public void loadUserStory() throws IOException {
        loader.loadUserStory(this);

    }

    public void refreshTasks() throws IOException {
        clearObjectContainer();
        loadTasks();
        loadUserStory();
        run();
    }

}
