/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVUtils;

import java.io.PrintWriter;

/**
 *
 * @author rishikaidnani
 */
public class SentimentAndTimezone {

    private String timeZone;
    private long numberOfPositive;
    private long numberOfNeutral;
    private long numberOfNegative;
    private String finalString;

    public String getFinalString() {
        return finalString;
    }

    public void setFinalString(String finalString) {
        this.finalString = finalString;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getNumberOfPositive() {
        return numberOfPositive;
    }

    public void setNumberOfPositive(long numberOfPositive) {
        this.numberOfPositive = numberOfPositive;
    }

    public long getNumberOfNeutral() {
        return numberOfNeutral;
    }

    public void setNumberOfNeutral(long numberOfNeutral) {
        this.numberOfNeutral = numberOfNeutral;
    }

    public long getNumberOfNegative() {
        return numberOfNegative;
    }

    public void setNumberOfNegative(long numberOfNegative) {
        this.numberOfNegative = numberOfNegative;
    }

    public void formString(PrintWriter writer) {
        String finalString = timeZone + "," + numberOfPositive + "," + numberOfNeutral + "," + numberOfNegative;
        writer.write(finalString);
        writer.println();
    }

    public String formStringForDashboard(PrintWriter writer) {
        String finalDashboardString = "{Timezone: '" + timeZone.replace("'", " ") + "', freq:{Positive:" + numberOfPositive + ", Neutral:" + numberOfNeutral + ", Negative:" + numberOfNegative + "}},";
        writer.write(finalDashboardString);
        writer.println();
        return finalDashboardString;
    }
    
        public String formStringForDashboard1() {
        String finalDashboardString = "{Timezone: '" + timeZone.replace("'", " ") + "', freq:{Positive:" + numberOfPositive + ", Neutral:" + numberOfNeutral + ", Negative:" + numberOfNegative + "}},\n";
        return finalDashboardString;
    }
}
