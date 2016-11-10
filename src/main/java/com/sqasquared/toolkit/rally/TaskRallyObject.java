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

public class TaskRallyObject extends RallyObject{
    public final String formattedUserTasksLink = "https://rally1.rallydev.com/#/%s/detail/userstory/%s/tasks";

    private String objectID, state, storyName, formattedID, projectName;
    private String storyID, projectID, storyLink;
    private double estimate;
    private Date lastUpdateDate;
    private Date creationDate;
    private List<String> storyTags;
    private String baseStoryName;


    public String getFormattedID() {
        return formattedID;
    }

    public TaskRallyObject(String taskName, String objectID, String formattedID, String state, String storyName,
                           String storyRef, String projectName, String projectRef, String creationDate, String lastUpdateDate,
                           double estimate) {
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

    public String parseID(String ref){
        Matcher m = Pattern.compile("[0-9]+$").matcher(ref);
        if(m.find()){
            return m.group();
        }
        return null;
    }

    public String generateStoryTasksLink(String pID, String sID){
        return String.format(formattedUserTasksLink, pID, sID);
    }

    public Date stringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat(super.DATEFORMAT);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getLastUpdateDate(){
        return lastUpdateDate;
    }

    public String getStoryID() {return storyID;}

    public String getStoryName() {
        return storyName;
    }

    public String getState(){
        return state;
    }

    public String getStoryLink(){
        return storyLink;
    }

    public void print(int indentation, int relictIndentation){
        String indent;
        try {
            indent = String.format("%" + relictIndentation + "s", "");
        }catch (Exception ex){
            indent = "";
        }
        String toBePrinted = indent + "Type: " + type + ", ID: " + id + ", Name: " + name +
                ", Status: " + state + ", LastUpdated: " + lastUpdateDate;
        System.out.println(toBePrinted);
//        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public void setSplitTags(String storyName){
        String[] story = storyName.split(" ");
        List<String> parsedTags = new ArrayList<String>();
        String baseStoryName = null;
        for (int i = 0; i < story.length; i++) {
            if(story[i].startsWith("[")){
                parsedTags.add(story[i]);
            } else {
                String[] base = storyName.split(" ", i+1);
                baseStoryName = base[base.length - 1];
                break;
            }
        }
        this.baseStoryName = baseStoryName;
        this.storyTags = parsedTags;
    }

    public String getBaseStoryName(){
        return this.baseStoryName;
    }

    public String getSubProjectTag(){
        int size = this.storyTags.size();
        if(size == 0){
            return null;
        }else if(size == 1){
            //first tag
            return storyTags.get(0);
        }else if (size >= 2){
            //second tag
            return storyTags.get(1);
        }
        return null;
    }

}
