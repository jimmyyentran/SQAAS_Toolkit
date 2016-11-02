package com.sqasquared.toolkit.email;

import com.sqasquared.toolkit.UserSession;
import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * Created by jimmytran on 10/30/16.
 */
public class EmailGenerator {
    public EmailGenerator(){

    }

    public void generate(UserSession userSession, String temp) throws EmailException, IOException, MessagingException {
        String htmlEmailTemplate = userSession.getTemplate(temp);

        Document doc = Jsoup.parse(htmlEmailTemplate);
        Elements completed = doc.select("sqaas[type='completed']");
        Elements project = doc.select("sqaas[type='project'");
        project.first().text("[PROJECT]");

        Element a = doc.select("a").first();
        a.attr("href", "http://link.com");

        Element story = doc.select("sqaas[type='story']").first();
        story.replaceWith(new TextNode("testing", ""));

//        sq.first().replaceWith(new TextNode("test", "balh"));
//        System.out.println(doc.toString());
        System.out.println("completed = " + completed);

//        return;

//        Compose email
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.googlemail.com");
        email.setFrom("me@apache.org");
        email.addTo("JTran@sqasquared.com");
        email.setSubject("Test email with inline image");
//        email.setHtmlMsg(htmlEmailTemplate);
        email.setHtmlMsg(doc.toString());
        email.buildMimeMessage();
        MimeMessage mimeMessage = email.getMimeMessage();

        //Create random output
        String uuid = UUID.randomUUID().toString();
        File tempFile = new File(System.getProperty("user.home") + "\\Desktop\\newemail"+uuid+".eml");
        tempFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(tempFile);
        mimeMessage.writeTo(fos);
        fos.flush();
        fos.close();
    }
}
