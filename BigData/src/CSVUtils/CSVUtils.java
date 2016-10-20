/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVUtils;

import Helper.KeyValueTuple;
import Utils.GeoCoordinates;
import Utils.TweetUtils;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import org.apache.hadoop.fs.FileUtil;

/**
 *
 * @author kedarvdm
 */
public class CSVUtils {

    private static ArrayList<DeviceAndCountryUtil> listOfCountries = new ArrayList<>();
    private static ArrayList<GenderPerTimezoneUtil> listOfTimezonesGender = new ArrayList<>();
    private static ArrayList<SentimentAndTimezone> listOfTimezones = new ArrayList<>();

    public static void createCSV(ArrayList<KeyValueTuple> data, String headers, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(headers);

            for (KeyValueTuple kvt : data) {
                writer.println(kvt.getKey() + "," + kvt.getValue());
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static void createCSVForSentiment(ArrayList<KeyValueTuple> data, String headers, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(headers);

            for (KeyValueTuple kvt : data) {
                writer.println(kvt.getKey() + "," + kvt.getValue() + "," + getSentimentColor(kvt.getKey()));
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static void createCSVLanguageAsterPlot(ArrayList<KeyValueTuple> data, String headers, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            NumberFormat formatter = new DecimalFormat("#0.0000");
            long total = 0;
            for (KeyValueTuple kvt : data) {
                total = total + kvt.getValue();
            }

            long maxcount = data.get(0).getValue();
            double order = 0.0;
            int count = 0;

            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(headers);

            for (KeyValueTuple kvt : data) {
                double weight = kvt.getValue() / (double) total;
                double score = (kvt.getValue() / (double) maxcount) * 100;
                //"label", "order", "score", "weight" ,"color", "count"

                String str = kvt.getKey().toUpperCase() + "," + formatter.format(order) + "," + formatter.format(score) + "," + formatter.format(weight) + "," + get20Colors(count) + "," + kvt.getValue();
                writer.println(str);
                order++;
                count++;
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static String getSentimentColor(String sentiment) {
        if (sentiment.equals("NEGATIVE")) {
            return "#FF0000";
        } else if (sentiment.equals("POSITIVE")) {
            return "#008000";
        } else if (sentiment.equals("NEUTRAL")) {
            return "#6495ED";
        } else if (sentiment.equals("NOT_UNDERSTOOD")) {
            return "#FAFAD2";
        } else {
            return "#000000";
        }
    }

    public static String get20Colors(int id) {
        String color = "#FF6347";
        switch (id) {
            case 1:
                color = "#FF6347";
                break;
            case 2:
                color = "#F5DEB3";
                break;
            case 3:
                color = "#008080";
                break;
            case 4:
                color = "#D8BFD8";
                break;
            case 5:
                color = "#D2B48C";
                break;
            case 6:
                color = "#4682B4";
                break;
            case 7:
                color = "#00FF7F";
                break;
            case 8:
                color = "#708090";
                break;
            case 9:
                color = "#6A5ACD";
                break;
            case 10:
                color = "#A0522D";
                break;
            case 11:
                color = "#FA8072";
                break;
            case 12:
                color = "#F4A460";
                break;
            case 13:
                color = "#2E8B57";
                break;
            case 14:
                color = "#4169E1";
                break;
            case 15:
                color = "#663399";
                break;
            case 16:
                color = "#800080";
                break;
            case 17:
                color = "#DB7093";
                break;
            case 18:
                color = "#FFA500";
                break;
            case 19:
                color = "#7B68EE";
                break;
            case 20:
                color = "#20B2AA";
                break;
            default:
                color = "#FF6347";
                break;
        }
        return color;
    }

    public static void createCSVForDeviceCount(ArrayList<KeyValueTuple> data, String headers, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(headers);

            for (KeyValueTuple kvt : data) {
                String country = (kvt.getKey().split("-")[0]).trim();
                String deviceType = (kvt.getKey().split("-")[1]).trim();
                DeviceAndCountryUtil dc = findCountryPresent(country);
                if (dc == null) {
                    dc = new DeviceAndCountryUtil();
                    dc.setCountry(country);
                    listOfCountries.add(dc);
                    if (deviceType.equals("Android")) {
                        dc.setAndroid(kvt.getValue());
                    } else if (deviceType.equals("ioS")) {
                        dc.setIoS(kvt.getValue());
                    } else if (deviceType.equals("Web")) {
                        dc.setWeb(kvt.getValue());
                    } else if (deviceType.equals("Others")) {
                        dc.setOthers(kvt.getValue());
                    }
                } else {
                    if (deviceType.equals("Android")) {
                        dc.setAndroid(kvt.getValue());
                    } else if (deviceType.equals("ioS")) {
                        dc.setIoS(kvt.getValue());
                    } else if (deviceType.equals("Web")) {
                        dc.setWeb(kvt.getValue());
                    } else if (deviceType.equals("Others")) {
                        dc.setOthers(kvt.getValue());
                    }
                }
            }

            for (DeviceAndCountryUtil dc : listOfCountries) {
                dc.formString(writer);
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static DeviceAndCountryUtil findCountryPresent(String country) {
        for (DeviceAndCountryUtil dc : listOfCountries) {
            if (dc.getCountry().equals(country)) {
                return dc;
            }
        }
        return null;
    }
    
    
    public static void createCSVForGenderTimezone(ArrayList<KeyValueTuple> data, String headers, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(headers);

            for (KeyValueTuple kvt : data) {
                String timezone = (kvt.getKey().split("\t")[0]).trim();
                String gender = (kvt.getKey().split("\t")[1]).trim();
                GenderPerTimezoneUtil gtpz = findGenderTimezonePresent(timezone);
                if (gtpz == null) {
                    gtpz = new GenderPerTimezoneUtil();
                    gtpz.setValue(timezone);
                    listOfTimezonesGender.add(gtpz);
                }
                if (gender.equals("MALE")) {
                    gtpz.setMale(kvt.getValue());
                } else if (gender.equals("FEMALE")) {
                    gtpz.setFemale(kvt.getValue());
                }
            }

            for (GenderPerTimezoneUtil gtpz : listOfTimezonesGender) {
                gtpz.formString(writer);
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static GenderPerTimezoneUtil findGenderTimezonePresent(String timezone) {
        for (GenderPerTimezoneUtil gptz : listOfTimezonesGender) {
            if (gptz.getValue().equals(timezone)) {
                return gptz;
            }
        }
        return null;
    }
    

    public static void generateGeoJs(ArrayList<KeyValueTuple> data, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println("var statesData = {\"type\":\"FeatureCollection\",\"features\":[");
            for (int i = 0; i < data.size() - 1; i++) {
                writer.println(GeoCoordinates.getCoordinatesJS(data.get(i).getKey(), "Tweets", String.valueOf(data.get(i).getValue())) + ",");
            }
            writer.println(GeoCoordinates.getCoordinatesJS(data.get(data.size() - 1).getKey(), "Tweets", String.valueOf(data.get(data.size() - 1).getValue())));    //Print last without comma
            writer.println("]};");
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static void createCSVForSentimentTimezone(ArrayList<KeyValueTuple> data, String headers, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(headers);

            for (KeyValueTuple kvt : data) {
                String timezone = (kvt.getKey().split("-")[0]).trim();
                String sentiment = (kvt.getKey().split("-")[1]).trim();
                SentimentAndTimezone st = findTimezonePresent(timezone);
                if (st == null) {
                    st = new SentimentAndTimezone();
                    st.setTimeZone(timezone);
                    listOfTimezones.add(st);
                }
                if (sentiment.equals("NEUTRAL")) {
                    st.setNumberOfNeutral(kvt.getValue());
                } else if (sentiment.equals("NEGATIVE")) {
                    st.setNumberOfNegative(kvt.getValue());
                } else if (sentiment.equals("POSITIVE")) {
                    st.setNumberOfPositive(kvt.getValue());
                }
            }

            for (SentimentAndTimezone st : listOfTimezones) {
                st.formString(writer);
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static SentimentAndTimezone findTimezonePresent(String timezone) {
        for (SentimentAndTimezone st : listOfTimezones) {
            if (st.getTimeZone().equals(timezone)) {
                return st;
            }
        }
        return null;
    }

    public static void createCSVForSentimentTimezoneDashboard(ArrayList<KeyValueTuple> data, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer1 = new PrintWriter(outputFile);

            for (KeyValueTuple kvt : data) {
                String timezone = (kvt.getKey().split("-")[0]).trim();
                String sentiment = (kvt.getKey().split("-")[1]).trim();
                SentimentAndTimezone st = findTimezonePresent(timezone);
                if (st == null) {
                    st = new SentimentAndTimezone();
                    st.setTimeZone(timezone);
                    listOfTimezones.add(st);
                }
                if (sentiment.equals("NEUTRAL")) {
                    st.setNumberOfNeutral(kvt.getValue());
                } else if (sentiment.equals("NEGATIVE")) {
                    st.setNumberOfNegative(kvt.getValue());
                } else if (sentiment.equals("POSITIVE")) {
                    st.setNumberOfPositive(kvt.getValue());
                }
            }
            String finalDashboardString = "";
            for (SentimentAndTimezone st : listOfTimezones) {
                finalDashboardString += st.formStringForDashboard1();
            }
            finalDashboardString = "var freqData=[ \n" + finalDashboardString;
            String finalDashboardString1 = finalDashboardString.substring(0, finalDashboardString.length() - 2);
            String finalDashboardString2 = finalDashboardString1 + "];";
            writer1.write(finalDashboardString2);
            writer1.println();
            writer1.println();
            writer1.println("dashboard('#graph',freqData);");
            writer1.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }

    public static void createTopUserMentionsCSV(ArrayList<KeyValueTuple> data, String outPut) {
        File outputFile = new File(outPut);
        if (outputFile.exists()) {
            FileUtil.fullyDelete(outputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (Exception ex) {
            System.out.println("File creation failed");
        }
        try {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println("Top User Mentions,");
            writer.println("Source: D3JS,");
            writer.println("Metadata Notes: XYZ,");
            writer.println(",");
            writer.println("Mention,Count");
            for (KeyValueTuple kvt : data) {
                writer.println(kvt.getKey() + "," + kvt.getValue());
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error Reading Data");
        }
    }
}
