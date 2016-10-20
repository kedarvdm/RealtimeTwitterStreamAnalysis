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
public class DeviceAndCountryUtil {

    private String country;
    private long android;
    private long ioS;
    private long web;
    private long others;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getAndroid() {
        return android;
    }

    public void setAndroid(long android) {
        this.android = android;
    }

    public long getIoS() {
        return ioS;
    }

    public void setIoS(long ioS) {
        this.ioS = ioS;
    }

    public long getWeb() {
        return web;
    }

    public void setWeb(long web) {
        this.web = web;
    }

    public long getOthers() {
        return others;
    }

    public void setOthers(long others) {
        this.others = others;
    }

    public void formString(PrintWriter writer) {
        String finalString = country + "," + android + "," + ioS + "," + web + "," + others;
        writer.write(finalString);
        writer.println();
    }

}
