package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.ASM;
import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.email.EmailGeneratorException;
import org.apache.commons.mail.EmailException;
import org.apache.http.auth.InvalidCredentialsException;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by jimmytran on 11/29/16.
 */
public class AppDirector {
    private EmailGenerator gen = new EmailGenerator();
    private RallyManager rallyManager;
    private FileResourceManager fileResourceManager;
    private TfsManager tfsManager;
    private UserSession userSession;

    public AppDirector(UserSession userSession) {
        this.userSession = userSession;
    }

    public void setRallyManager(RallyManager rallyManager) {
        this.rallyManager = rallyManager;
    }

    public void setTfsManager(TfsManager tfsManager) {
        this.tfsManager = tfsManager;
    }

    public void setFileResourceManager(FileResourceManager
                                               fileResourceManager) {
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

    public void loadRallyTasks() throws IOException {
        rallyManager.refreshTasks();
    }

    public void loadUserInfo() throws IOException {
        rallyManager.loadUserInfo();
        refreshTasks();
    }

    /******************************************

     The following are events triggered by the view

     ******************************************/

    public String generateHtml(String template) throws EmailGeneratorException {
        return gen.generate(rallyManager.topNode, template);
    }

    // Save format and save email into file location
    public void generateEmail(String to, String cc, String subject, String
            html, String email, String loc) throws
            EmailException, MessagingException, IOException {
        gen.saveEmail(to, cc, subject, html, email, loc);
    }

    public void sendEmail(String to, String cc, String subject, String
            html, String email) throws
            EmailException, MessagingException, IOException {
        gen.sendEmail(to, cc, subject, html, email);
    }


    public String getLastEmailSubject() {
        return gen.getLastEmailSubject();
    }


    public void refreshTasks() throws IOException {
        rallyManager.refreshTasks();
    }

    public String generateTestCases(String template) throws IOException {
        tfsManager.loadWorkingTree(ASM.INSTEP2, ASM.PRODUCT_BACKLOG_ITEM_WIT, "12386", ASM
                .TEST_CASE_WIT);
        System.out.println(gen.generateTestCase(tfsManager.getTopNode(), template));
        return gen.generateTestCase(tfsManager.getTopNode(), template);
    }

    public void loginASM() throws IOException, InvalidCredentialsException {
        if (UserSession.getProperty("ASM_username").equals("") ||
                UserSession.getProperty("ASM_username").equals("ASM\\") ||
                UserSession.getProperty("ASM_password").equals("") ||
                !tfsManager.isValidCredentials()) {
            throw new InvalidCredentialsException("Wrong TFS credentials");
        }
    }
}
