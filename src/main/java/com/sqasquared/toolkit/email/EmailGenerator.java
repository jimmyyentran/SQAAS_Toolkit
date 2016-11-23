package com.sqasquared.toolkit.email;

import com.sqasquared.toolkit.UserSession;
import com.sqasquared.toolkit.rally.RallyObject;
import com.sqasquared.toolkit.rally.TaskRallyObject;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by jimmytran on 10/30/16.
 */
public class EmailGenerator {
    RallyObject lastUpdatedEmail;
    public EmailGenerator() {

    }

    private Element mapListItem(String formattedId, String taskName, String estimate, Element listItemTemplate) {
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

    private Element mapStory(RallyObject node, Element completedRoot, int order) {
        Element completed = completedRoot.clone();
        String storyName = "";
        String storyLink = "";
        String storySubTag = "";
        String storyId = "";
        String storySubTagBracket = "";
        Element list = completed.select("ul").first();
        Element listItem = completed.select("sqaas[type='listItem']").first();
        for (RallyObject st : node.getChildren().values()) {
            // loop over tasks
            if (st.getType().equals("task")) {
                TaskRallyObject task = (TaskRallyObject) st;
                if (storyName.equals("") || storyLink.equals("")) {
                    storyName = task.getStoryName().trim();
                    storyLink = task.getStoryLink();
                    storySubTag = task.getSubProjectTag(false);
                    storySubTagBracket = task.getSubProjectTag(true);
                    storyId = task.getStoryFormattedID();
                }
                Element listItemMapped = mapListItem(task.getFormattedID(), task.getName(), task.getEstimate(), listItem);
                list.appendChild(listItemMapped);
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

        // sub-project
        Element subProject = completed.select("sqaas[type='subProject'").first();
        if(subProject != null) {
            subProject.replaceWith(new TextNode(storySubTag, ""));
        }

        // sub-project bracket
        Element subProjectBracket = completed.select("sqaas[type='subProjectBracket'").first();
        if(subProjectBracket != null) {
            subProjectBracket.replaceWith(new TextNode(storySubTagBracket, ""));
        }

        // href links
        Element a = completed.select("a").first();
        if(a != null){
            a.attr("href", storyLink);
        }

        // story name
        Element sn = completed.select("sqaas[type='storyName']").first();
        if(sn != null){
            sn.replaceWith(new TextNode(storyName, ""));
        }

        // story id
        Element si = completed.select("sqaas[type='storyId']").first();
        if(si != null) {
            si.replaceWith(new TextNode(storyId, ""));
        }

        // delete the list item template
        listItem.remove();

        return completed;
    }

    private void mapStory(RallyObject node, Element completed) {
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

    private RallyObject mapLastUpdatedStory(RallyObject node, Element completed) {
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

    private String generateSubject(RallyObject ro) {
        String subject = UserSession.SSU_TAG;
        TaskRallyObject task = (TaskRallyObject) ro.getChildren().values().iterator().next();
        if (task.getType().equals("task")) {
            String storyName = task.getStoryName();
            int i = storyName.lastIndexOf("]");
            String tags = storyName.substring(0, i + 1);
            String base = storyName.substring(i + 1, storyName.length());
            subject += (" " + tags + " " + task.getStoryFormattedID() + " " + base);
        } else {
            throw new RuntimeException(
                    String.format("Wrong children node type. Expected %s, got %s", "task", task.getType()));
        }
        return subject;
    }

    public String getLastEmailSubject(){
        return generateSubject(lastUpdatedEmail);
    }

    public String generate(UserSession userSession, String template) throws EmailGeneratorException {
        String htmlEmailTemplate = userSession.getTemplate(template);
        Document doc = Jsoup.parse(htmlEmailTemplate);
        if (template.equals(UserSession.SSU)) {
            // story status updates
            Element inProgress = doc.select("sqaas[type='notCompleted']").first();

            // use today's in-progress task
            RallyObject inProgressNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.INPROGRESS);
            if (!inProgressNode.isEmpty()) {
                lastUpdatedEmail = mapLastUpdatedStory(inProgressNode, inProgress);
            } else {
                // Use yesterday's in-progress task
                RallyObject pastInProgressNode = userSession.getTopNode().getChildren().get("past")
                        .getChildren().get(RallyObject.INPROGRESS);
                if (!pastInProgressNode.isEmpty()) {
                    lastUpdatedEmail = mapLastUpdatedStory(pastInProgressNode, inProgress);
                } else {
                    throw new EmailGeneratorException("No in-progress tasks today. Get to work!!");
                }
            }
        } else {
            Element completed = doc.select("sqaas[type='completed']").first();
            // End of day
            RallyObject completedNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.COMPLETED);
            if (!completedNode.isEmpty()) {
                mapStory(completedNode, completed);
            } else {
                throw new EmailGeneratorException("No completed tasks today. Get to work!!");
            }

            Element notCompleted = doc.select("sqaas[type='notCompleted']").first();
            RallyObject notCompletedNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.DEFINED);
            if (!notCompletedNode.isEmpty()) {
                mapStory(notCompletedNode, notCompleted);
            } else {
                notCompletedNode = userSession.getTopNode().getChildren().get("today").getChildren().get(RallyObject.INPROGRESS);
                if (!notCompletedNode.isEmpty()) {
                    mapStory(notCompletedNode, notCompleted);
                } else {
                    throw new EmailGeneratorException("No in-progress or declared tasks today");
                }
            }
        }

        // Map full name
        Element fullName = doc.select("sqaas[type='fullName']").first();
        if (fullName != null) {
            fullName.replaceWith(new TextNode(userSession.getFullName(), ""));
        }

        return doc.toString();
    }

    public void createEmail(String to, String cc, String subject, String html, String from, String loc) throws EmailException, IOException, MessagingException {
        // Compose email
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.googlemail.com");
        email.addTo(to.split(UserSession.EMAIL_SEPARATOR));
        email.setFrom(from);
        email.addCc(cc.split(UserSession.EMAIL_SEPARATOR));
        email.setSubject(subject);
        email.setHtmlMsg(html);
        email.buildMimeMessage();
        MimeMessage mimeMessage = email.getMimeMessage();

        // Create random output
//        String uuid = UUID.randomUUID().toString();
//        File tempFile = null;
//        if (System.getProperty("os.name").startsWith("Mac")) {
//            tempFile = new File("resources/email/" + template + uuid + ".eml");
//            tempFile.getParentFile().mkdir();
//        } else {
//            tempFile = new File(System.getProperty("user.home") + "\\Desktop\\newemail" + uuid + ".eml");
//        }
//        tempFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(new File(loc));
        mimeMessage.writeTo(fos);
        fos.flush();
        fos.close();
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

        // Create random output
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
