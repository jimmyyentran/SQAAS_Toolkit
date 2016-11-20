package com.sqasquared.toolkit.rally;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jimmytran on 10/30/16.
 */

public class TaskRallyObject extends RallyObject {
    private final String formattedUserTasksLink = "https://rally1.rallydev.com/#/%s/detail/userstory/%s/tasks";

    private final String objectID;
    private final String state;
    private final String storyName;
    private final String formattedID;
    private final String projectName;
    private final String storyID;
    private final String projectID;
    private final String storyLink;
    private final String estimate;
    private final Date lastUpdateDate;
    private final Date creationDate;
    private List<String> storyTags;
    private String baseStoryName;
    private String storyFormattedID;

    public TaskRallyObject(String taskName, String objectID, String formattedID, String state, String storyName,
                           String storyRef, String projectName, String projectRef, String creationDate, String lastUpdateDate,
                           String estimate) {
        super("task", objectID, taskName);
        this.objectID = objectID;
        this.formattedID = formattedID;
        this.state = state;
        this.storyName = storyName;
        this.projectName = projectName;
        this.estimate = estimate;

        this.lastUpdateDate = stringToDate(lastUpdateDate);
        this.creationDate = stringToDate(creationDate);

        this.storyID = parseID(storyRef);
        this.projectID = parseID(projectRef);
        this.storyLink = generateStoryTasksLink(projectID, storyID);

        setSplitTags(storyName);
    }

    private String parseID(String ref) {
        Matcher m = Pattern.compile("[0-9]+$").matcher(ref);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    private String generateStoryTasksLink(String pID, String sID) {
        return String.format(formattedUserTasksLink, pID, sID);
    }

    private Date stringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getStoryID() {
        return storyID;
    }

    public String getStoryName() {
        return storyName;
    }

    public String getState() {
        return state;
    }

    public String getStoryLink() {
        return storyLink;
    }

    public String getStoryFormattedID() {
        return storyFormattedID;
    }

    public void setStoryFormattedID(String storyFormattedID) {
        this.storyFormattedID = storyFormattedID;
    }

    public String getFormattedID() {
        return formattedID;
    }

    public String getEstimate() {
        return estimate;
    }

    public void print(int indentation, int relictIndentation) {
        String indent;
        try {
            indent = String.format("%" + relictIndentation + "s", "");
        } catch (Exception ex) {
            indent = "";
        }
        String toBePrinted = indent + "Type: " + type + ", ID: " + id + ", Name: " + name +
                ", Status: " + state + ", LastUpdated: " + lastUpdateDate;
        System.out.println(toBePrinted);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    private void setSplitTags(String storyName) {
        List<String> parsedTags = new ArrayList<String>();
        Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(storyName);
        while (m.find()) {
            parsedTags.add(m.group(1));
        }

        // Get "]" to end of string
        int i = storyName.lastIndexOf("]");
        String a = storyName.substring(i + 1, storyName.length());
        this.baseStoryName = a.trim();

        this.storyTags = parsedTags;
    }

    public String getBaseStoryName() {
        return this.baseStoryName;
    }

    public String getSubProjectTag() {
        int size = this.storyTags.size();
        String bracketOpen = "[";
        String bracketClose = "]";
        if (size == 1) {
            //first tag
            return bracketOpen + storyTags.get(0) + bracketClose;
        } else if (size >= 2) {
            //second tag
            return bracketOpen + storyTags.get(1) + bracketClose;
        }
        return bracketOpen + "" + bracketClose;
    }

}
