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
    public EmailGenerator() {

    }

    public Element mapListItem(String formattedId, String taskName, String estimate, Element listItemTemplate) {
        Element listItem = listItemTemplate.clone();
        Element fi = listItem.select("sqaas[type='formattedID']").first();
        fi.replaceWith(new TextNode(formattedId, ""));
        Element tn = listItem.select("sqaas[type='taskName']").first();
        tn.replaceWith(new TextNode(taskName, ""));
        Element eta = listItem.select("sqaas[type='ETA']").first();
        if (eta != null) {
            eta.replaceWith(new TextNode(estimate, ""));
        }
        return listItem;
    }

    public Element mapStory(RallyObject node, Element completedRoot, int order) {
        Element completed = completedRoot.clone();
        String storyName = null;
        String storyLink = null;
        String storySubTag = null;
        String storyId = null;
        Element list = completed.select("ul").first();
        Element listItem = completed.select("sqaas[type='listItem']").first();
        for (RallyObject st : node.getChildren().values()) {
            // loop over tasks
            if (st.getType().equals("task")) {
                TaskRallyObject task = (TaskRallyObject) st;
                if (storyName == null || storyLink == null) {
                    storyName = task.getStoryName();
                    storyLink = task.getStoryLink();
                    storySubTag = task.getSubProjectTag();
                    storyId = task.getStoryFormattedID();
                }
                Element listItemMapped = mapListItem(task.getFormattedID(), task.getName(), task.getEstimate(), listItem);
                list.appendChild(listItemMapped);
//                list.prependChild(listItemMapped);
            } else {
                throw new RuntimeException(
                        String.format("Wrong children node type. Expected %s, got %s", "task", node.getType()));
            }
        }

        // remove order template
        if (order > 1) {
            completed.select("sqaas[type='first']").remove();
        } else {
            completed.select("sqaas[type='second']").remove();
        }

//        // sub-project
        Element subProject = completed.select("sqaas[type='subProject'").first();
        subProject.replaceWith(new TextNode(storySubTag, ""));

        // href links
        Element a = completed.select("a").first();
        a.attr("href", storyLink);

        // story name
        Element sn = completed.select("sqaas[type='storyName']").first();
        sn.replaceWith(new TextNode(storyName, ""));

        // story id
        Element si = completed.select("sqaas[type='storyId']").first();
        si.replaceWith(new TextNode(storyId, ""));

        // delete the list item template
        listItem.remove();

        return completed;
    }

    public void mapCompleted(RallyObject node, Element completed) {
        int order = 1;
        for (RallyObject story : node.getChildren().values()) {
            if (story.getType().equals("story")) {
                Element completedMapped = mapStory(story, completed, order);
                completed.before(completedMapped);
            } else {
                throw new RuntimeException(
                        String.format("Wrong children node type. Expected %s, got %s", "story", story.getType()));
            }
            order++;
        }

        // remove template
        completed.remove();
    }

    public RallyObject mapLastUpdatedStory(RallyObject node, Element completed) {
        TaskRallyObject latestTask = null;
        RallyObject latestStory = null;
        // Loop stories
        for (RallyObject story : node.getChildren().values()) {
            if (story.getType().equals("story")) {
                // Loop tasks
                for (RallyObject st : story.getChildren().values()) {
                    if (st.getType().equals("task")) {
                        TaskRallyObject task = (TaskRallyObject) st;
                        if (latestTask == null || latestStory == null) {
                            latestTask = task;
                            latestStory = story;
                        } else if (latestTask.getLastUpdateDate().before(task.getLastUpdateDate())) {
                            latestTask = task;
                            latestStory = story;
                        }
                    } else {
                        throw new RuntimeException(
                                String.format("Wrong children node type. Expected %s, got %s", "task", node.getType()));
                    }
                }
            } else {
                throw new RuntimeException(
                        String.format("Wrong children node type. Expected %s, got %s", "story", story.getType()));
            }
        }
        Element mapLatestStory = mapStory(latestStory, completed, 1);
        completed.before(mapLatestStory);
        completed.remove();
        return latestStory;
    }

    public String generateSubject(RallyObject ro){
        String subject = UserSession.SSU_TAG;
        TaskRallyObject task = (TaskRallyObject)ro.getChildren().values().iterator().next();
        if(task.getType().equals("task")){
            String storyName = task.getStoryName();
            int i = storyName.lastIndexOf("]");
            String tags = storyName.substring(0, i+1);
            String base = storyName.substring(i+1,storyName.length());
            subject += (" " + tags + " " + task.getStoryFormattedID() + " " + base);
        } else {
            throw new RuntimeException(
                    String.format("Wrong children node type. Expected %s, got %s", "task", task.getType()));
        }
        return subject;
    }

    public String generate(UserSession userSession, String template) throws EmailGeneratorException{
        String htmlEmailTemplate = userSession.getTemplate(template);
        Document doc = Jsoup.parse(htmlEmailTemplate);
//        String subject = "";
        if (template.equals(UserSession.SSU)) {
            Element inProgress = doc.select("sqaas[type='notCompleted']").first();
            RallyObject inProgressNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.INPROGRESS);
            if (!inProgressNode.isEmpty()) {
                RallyObject lastUpdated = mapLastUpdatedStory(inProgressNode, inProgress);
//                subject = generateSubject(lastUpdated);
            } else {
                RallyObject pastInProgressNode = userSession.getTopNode().getChildren().get("past")
                        .getChildren().get(RallyObject.INPROGRESS);
                if (!pastInProgressNode.isEmpty()) {
                    RallyObject lastUpdated = mapLastUpdatedStory(pastInProgressNode, inProgress);
//                    subject = generateSubject(lastUpdated);
                } else {
                    throw new EmailGeneratorException("No in-progress tasks today. Get to work!!");
                }
            }
        } else {
            Element completed = doc.select("sqaas[type='completed']").first();
            RallyObject completedNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.COMPLETED);
            if (!completedNode.isEmpty()) {
                mapCompleted(completedNode, completed);
            } else {
                throw new EmailGeneratorException("No completed tasks today. Get to work!!");
            }

            Element notCompleted = doc.select("sqaas[type='notCompleted']").first();
            RallyObject notCompletedNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.DEFINED);
            if (!notCompletedNode.isEmpty()) {
                mapCompleted(notCompletedNode, notCompleted);
            } else {
                notCompletedNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.INPROGRESS);
                if (!notCompletedNode.isEmpty()) {
                    mapCompleted(notCompletedNode, notCompleted);
                } else {
                    throw new EmailGeneratorException("No in-progress or declared tasks today");
                }
            }
        }

        // Map full name
        Element fullName = doc.select("sqaas[type='fullName']").first();
        if(fullName != null) {
            fullName.replaceWith(new TextNode(userSession.getFullName(), ""));
        }

        return doc.toString();
    }



    public void createEmail(UserSession userSession, String template) throws Exception {
        String subject = "";
        String doc = "";
        // Compose email
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.googlemail.com");
        email.setFrom(userSession.getEmail());
        email.addTo(userSession.getEmailTo(template));
        email.addCc(userSession.getEmailCC());
        email.setSubject(subject);
        email.setHtmlMsg(doc.toString());
        email.buildMimeMessage();
        MimeMessage mimeMessage = email.getMimeMessage();

//      // Create random output
        String uuid = UUID.randomUUID().toString();
        File tempFile = null;
        if (System.getProperty("os.name").startsWith("Mac")) {
            tempFile = new File("resources/email/" + template + uuid + ".eml");
            tempFile.getParentFile().mkdir();
        } else {
            tempFile = new File(System.getProperty("user.home") + "\\Desktop\\newemail" + uuid + ".eml");
        }
        tempFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(tempFile);
        mimeMessage.writeTo(fos);
        fos.flush();
        fos.close();
    }
}
