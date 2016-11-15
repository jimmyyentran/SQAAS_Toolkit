package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.rally.RallyWrapper;
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

//        userSession.getTopNode().print(4);
        System.out.println("userSession = " + userSession);
        loader.loadTemplates(userSession);
        try {
            gen.generate(userSession, UserSession.SSU);
//            gen.generate(userSession, UserSession.EOD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
