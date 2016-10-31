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
        RallyWrapper rally = new RallyWrapper("https://rally1.rallydev.com", "api_credentials");
        try {
            new EmailGenerator();
        } catch (EmailException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
