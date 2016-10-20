/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVUtils;

import java.io.PrintWriter;

/**
 *
 * @author kedarvdm
 */
public class GenderPerTimezoneUtil {
    private String value;
    private long female;
    private long male;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getFemale() {
        return female;
    }

    public void setFemale(long female) {
        this.female = female;
    }

    public long getMale() {
        return male;
    }

    public void setMale(long male) {
        this.male = male;
    }
    
    public void formString(PrintWriter writer) {
        writer.write(value + "," + male + "," + female);
        writer.println();
    }
}
