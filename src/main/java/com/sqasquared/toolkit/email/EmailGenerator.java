package com.sqasquared.toolkit.email;

import com.sqasquared.toolkit.UserSession;
import com.sqasquared.toolkit.rally.RallyObject;
import com.sqasquared.toolkit.rally.TaskRallyObject;
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
import java.util.Map;
import java.util.UUID;

/**
 * Created by jimmytran on 10/30/16.
 */
public class EmailGenerator {
    public EmailGenerator(){

    }

    public void mapListItem(String formattedId, String taskName, Element listItem){
        Element fi = listItem.select("sqaas[type='formattedID']").first();
        fi.replaceWith(new TextNode(formattedId, ""));
        Element tn = listItem.select("sqaas[type='taskName']").first();
        tn.replaceWith(new TextNode(taskName, ""));
    }

    public void mapStory(RallyObject node, Element completed, int order){
        String storyName = null;
        String storyLink = null;
        Element list = completed.select("ul").first();
        Element listItem = completed.select("li").first();
        for(RallyObject st: node.getChildren().values()){
            if(st.getType().equals("task")){
                TaskRallyObject task = (TaskRallyObject)st;
                if(storyName == null || storyLink == null){
                    storyName = task.getStoryName();
                    storyLink = task.getStoryLink();
                }
                Element listItemMapped = listItem.clone();
                mapListItem(task.getFormattedID(), task.getName(), listItemMapped);
                list.appendChild(listItemMapped);
            }else{
                throw new RuntimeException(
                        String.format("Wrong children node type. Expected %s, got %s", "task", node.getType()));
            }

            // remove order template
            if(order > 1){
                completed.select("sqaas[type='first']").remove();
            } else {
                completed.select("sqaas[type='second']").remove();
            }

            // sub-project
            Elements project = completed.select("sqaas[type='subProject'");
            project.first().text("[PROJECT]");

            // href links
            Element a = completed.select("a").first();
            a.attr("href", storyLink);

            // story name
            System.out.println(completed);
            Element sn = completed.select("sqaas[type='storyName']").first();
            sn.replaceWith(new TextNode(storyName, ""));

            // delete the list item template
            listItem.remove();
        }
    }

    public void mapCompleted(RallyObject node, Element completed){
        int order = 1;
        for(RallyObject story : node.getChildren().values()){
            if(story.getType().equals("story")){
                Element completedItemMapped = completed.clone();
                System.out.println(completedItemMapped);
                mapStory(story, completedItemMapped, order);
                completed.before(completedItemMapped);
            }else{
                throw new RuntimeException(
                        String.format("Wrong children node type. Expected %s, got %s", "story", story.getType()));
            }
            order++;
        }
        // remove template
        completed.remove();
    }

    public void generate(UserSession userSession, String temp) throws EmailException, IOException, MessagingException {
        String htmlEmailTemplate = userSession.getTemplate(temp);

        Document doc = Jsoup.parse(htmlEmailTemplate);
        Element completed = doc.select("sqaas[type='completed']").first();
        mapCompleted(userSession.getTopNode().getChildren().get("today").getChildren().get("completed"), completed);
//        System.out.println("completed = " + completed);
//        System.out.println("doc.toString() = " + doc.toString());

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
