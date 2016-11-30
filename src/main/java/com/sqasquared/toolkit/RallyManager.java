package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TaskRallyObject;
import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.email.EmailGeneratorException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.apache.commons.mail.EmailException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by jimmytran on 11/29/16.
 */
public class RallyManager extends ObjectManager{
    private TreeAlgorithmInterface alg = null;
    private Loader loader = new Loader();

    public RallyManager(){
    }

    @Override
    public void addObject(DataObject task) {
        objectContainer.put(((TaskRallyObject)task).getFormattedID(), task);
    }

    private void run() {
        topNode = this.alg.constructTree(objectContainer);
    }

    private void setAlg(TreeAlgorithmInterface alg) {
        this.alg = alg;
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

    public void setAlgorithm(TreeAlgorithmInterface alg){
        this.alg = alg;
    }
}
