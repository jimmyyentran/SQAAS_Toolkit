package com.sqasquared.toolkit.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * Created by jimmytran on 10/30/16.
 */
public class EmailGenerator {
    public EmailGenerator() throws IOException, EmailException, MessagingException {
        String htmlEmailTemplate = ".... <img src=\"http://www.apache.org/images/feather.gif\"> ....";

        // define you base URL to resolve relative resource locations
        URL url = new URL("http://www.apache.org");

        // create the email message
        ImageHtmlEmail email = new ImageHtmlEmail();
        email.setDataSourceResolver(new DataSourceUrlResolver(url));
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("username@gmail.com", "password"));
        email.setSSLOnConnect(true);
        email.setFrom("me@apache.org", "Me");
        email.addTo("JTran@sqasquared.com");
        email.setSubject("Test email with inline image");

        // set the html message
        email.setHtmlMsg(htmlEmailTemplate);

//        email.send();

        email.buildMimeMessage();
        MimeMessage mimeMessage = email.getMimeMessage();
        System.out.println(mimeMessage);

        Date now = new Date();
        File tempFile = new File(now.toString() + ".eml");
        System.out.println("tempFile = " + tempFile);
        FileOutputStream fos = new FileOutputStream(tempFile);
        mimeMessage.writeTo(fos);
        fos.flush();
        fos.close();
    }
}
