package com.sqasquared.toolkit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jimmytran on 11/30/16.
 */
public class FileResourceManager extends ObjectManager<String> {
    private static Logger LOG = Logger.getLogger(FileResourceManager.class
            .getName());
    private RallyLoader rallyLoader = new RallyLoader();

    public void loadTemplates() {
        LOG.log(Level.FINE, "Loading templates");
        add("/template/end_of_day.html");
        add("/template/story_status_update.html");
        add("/template/story_status_update_progress.html");
        add("/template/test_case.html");
    }

    // Load html templates from class path
    private String getTemplateResource(String path) {
        InputStream in = UserSession.class.getResourceAsStream(path);
        try {
            return IOUtils.toString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Convert path to template variable
    private String pathToTemplateVariable(String path) {
        return FilenameUtils.removeExtension(FilenameUtils.getName(path));
    }

    @Override
    public void add(String path) {
        objectContainer.put(pathToTemplateVariable(path), getTemplateResource
                (path));
    }


    public String getTemplate(String template) {
        return objectContainer.get(template);
    }
}
