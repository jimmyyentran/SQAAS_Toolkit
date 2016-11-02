package com.sqasquared.toolkit.rally;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jimmytran on 10/30/16.
 */
public class TaskObject {
    private String formattedUserTasksLink = "https://rally1.rallydev.com/#/%s/detail/userstory/%s/tasks";

    private String taskName, creationDate, lastUpdateDate, state, storyName, storyRef, projectRef, formattedID;
    private String storyID, projectID, storyTaskLink;
    private double estimate;

    public String getFormattedID() {
        return formattedID;
    }

    public TaskObject(String taskName, String formattedID, String state, String storyName, String storyRef,
                      String projectRef, String creationDate, String lastUpdateDate, double estimate) {
        this.taskName = taskName;
        this.formattedID = formattedID;
        this.state = state;
        this.storyName = storyName;
        this.storyRef = storyRef;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.estimate = estimate;

        this.storyID = parseID(storyRef);
        this.projectID = parseID(projectRef);
        this.storyTaskLink = generateStoryTasksLink(projectID, storyID);
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

}
