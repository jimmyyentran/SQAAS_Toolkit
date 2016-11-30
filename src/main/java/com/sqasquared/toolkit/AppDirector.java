package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.email.EmailGeneratorException;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by jimmytran on 11/29/16.
 */
public class AppDirector {
    private EmailGenerator gen = new EmailGenerator();
    private RallyManager rallyManager;
    private FileResourceManager fileResourceManager;
    private UserSession userSession;

    public AppDirector(UserSession userSession){
        this.userSession = userSession;
    }

    public void loadRallyTasks() throws IOException {
        rallyManager.refreshTasks();
    }

    public void setRallyManager(RallyManager rallyManager) {
        this.rallyManager = rallyManager;
    }

    public void setFileResourceManager(FileResourceManager fileResourceManager) {
        this.fileResourceManager = fileResourceManager;
    }

    public String getTemplate(String template) {
        return fileResourceManager.getTemplate(template);
    }

    /******************************************

     Load session methods here

     ******************************************/

    public void loadTemplates() {
        fileResourceManager.loadTemplates();
    }

    public void loadUserInfo() throws IOException {
        Loader loader = new Loader();
        loader.loadUserInfo(userSession);
        refreshTasks();
    }

    /******************************************

     The following are events triggered by the view

     ******************************************/

    public String generateHtml(String template) throws EmailGeneratorException {
        return gen.generate(rallyManager.topNode, template);
    }

    // Save format and save email into file location
    public void generateEmail(String to, String cc, String subject, String html, String email, String loc) throws
            EmailException, MessagingException, IOException {
        gen.createEmail(to, cc, subject, html, email, loc);
    }


    public String getLastEmailSubject() {
        return gen.getLastEmailSubject();
    }


    public void refreshTasks() throws IOException {
        rallyManager.refreshTasks();
    }

    public void generateTestCases(String template){
        gen.generateTestCase(template);
    }
}
