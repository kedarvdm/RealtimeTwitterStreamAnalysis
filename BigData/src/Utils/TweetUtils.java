/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author rishikaidnani
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TweetUtils {

    public static String OUTPUT_PREFIX="/Users/rishikaidnani/Desktop/BigData/";
    public static String HBASE_CONF="/Users/rishikaidnani/Downloads/hbase-1.1.2/conf/hbase-site.xml";
    public static String getText(String htmlString) {
        Document html = Jsoup.parse(htmlString);
        return html.text();
    }
    
    public static ArrayList<State> getStateList() throws FileNotFoundException
    {
        ArrayList<State> stateList= new ArrayList<>();
        BufferedReader br = null;
        String line = "";
        
        final File f = new File(TweetUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String resourceFile = f.getAbsolutePath().replace("/build/classes", "") + "/src/Resources/";
        
        try {

            br = new BufferedReader(new FileReader(resourceFile+"StateList.tsv"));
            //line = br.readLine();
            while ((line = br.readLine()) != null) {
                String words[] = line.toString().split("\t");
                State st= new State();
                st.setName(words[0].trim());
                st.setAbbreviation(words[1].trim());
                stateList.add(st);
            }
        }
        catch(Exception ex)
        {
        }
        return stateList;
    }
    
    public static void main(String[] args)
    {
        try 
        {
            System.out.println(findState("alabama"));
            System.out.println(findState("MA "));
            System.out.println(findState("mass"));
            System.out.println(findState("new jersey"));
            System.out.println(findState("North dakota, ND"));
            
        } catch (Exception ex) {
            
        }
        
    }
    
    public static String findState(String text) throws FileNotFoundException
    {
        text= text.replace(",", " ").trim();
        ArrayList<State> stateList= getStateList();
        String state="";
        String abbr="";
        //Check state name first
        for(State st: stateList)
        {
            if(text.toLowerCase().contains(st.getName().toLowerCase()))
            {
                state= st.getName();
                abbr= st.getAbbreviation();
                break;
            }
        }
        
        if(state.equals(""))
        {
            for(State st: stateList)
            {
                String words[]= text.split(" ");
                for(String word:words)
                {
                    if(word.trim().equals(st.getAbbreviation()))
                    {
                        state= st.getName();
                        abbr= st.getAbbreviation();
                        break;
                    }
                }
                if(!state.equals(""))
                {
                    break;
                }
                
            }
        }
        return state;
    }
}
