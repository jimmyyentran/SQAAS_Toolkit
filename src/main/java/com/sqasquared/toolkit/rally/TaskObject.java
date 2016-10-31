package com.sqasquared.toolkit.rally;

import java.lang.reflect.Field;

/**
 * Created by jimmytran on 10/30/16.
 */
public class TaskObject {

    String taskName, creationDate, lastUpdateDate, state, story, formattedID;
    double estimate;

    public TaskObject(String taskName, String formattedID, String state, String story, String creationDate,
                      String lastUpdateDate, double estimate) {
        this.taskName = taskName;
        this.formattedID = formattedID;
        this.state = state;
        this.story = story;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.estimate = estimate;
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
