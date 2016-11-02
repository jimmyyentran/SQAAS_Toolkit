package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGenerator;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by jimmytran on 10/29/16.
 */
public class App {

    public static void main(String[] args) throws URISyntaxException, IOException {
        UserSession userSession = new UserSession();
        Loader loader = new Loader();
        EmailGenerator gen = new EmailGenerator();
        loader.loadUserSession(userSession);
        userSession.setAlg(new TimeAlgorithm()).run();
//        System.out.println("userSession = " + userSession);
//        System.out.println(java.util.TimeZone.getDefault());
//        loader.loadTemplates(userSession);
//        try {
//            gen.generate(userSession, "eod_template_org");
//        } catch (EmailException e) {
//            e.printStackTrace();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
    }
}
