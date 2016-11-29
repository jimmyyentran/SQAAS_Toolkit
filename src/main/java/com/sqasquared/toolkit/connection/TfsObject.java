package com.sqasquared.toolkit.connection;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;

/**
 * Created by JTran on 11/28/2016.
 */
public class TfsObject extends DataObject{
    @SerializedName("TeamProject") String teamProject;
    @SerializedName("State") String state;

    public TfsObject(){
        super();
        teamProject = "";
        state = "";
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
}
