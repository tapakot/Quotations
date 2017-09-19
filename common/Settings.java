package common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static common.ForexConstants.propFileName;


public class Settings{
    public static ArrayList<AdjustValue> adjustValues;
    public static Properties properties;
    public static Properties defProperties;


    public Settings(){
        adjustValues = new ArrayList<>();
        properties = new Properties();
        defProperties = new Properties();
    }

    public void setFromFile(String path, Properties prop){
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path)));
        }
        catch (FileNotFoundException e){
            System.out.println("WARNING: Properties file not found");
        }
        try
        {
            String section = "default";
            String line;

            while( (line = br.readLine())!=null )
            {
                if( line.startsWith(";") ) continue;
                if( line.startsWith("[") )
                {
                    section = line.substring(1,line.lastIndexOf("]")).trim();
                    continue;
                }
                addProperty(section,line,prop);
            }
            br.close();
        }
        catch (IOException e){
            System.out.println("WARNING: Unable to read properties");
        }
    }

    private void addProperty(String section,String line,Properties prop) {
        int equalIndex = line.indexOf("=");
        if( equalIndex > 0 ) {
            String name = section+'.'+line.substring(0,equalIndex).trim();
            String value = line.substring(equalIndex+1).trim();
            prop.setProperty(name,value);
        }
    }

    public static void setAdjustValues(){
        adjustValues.clear();
        for(Enumeration e = properties.propertyNames(); e.hasMoreElements();){
            Object a = e.nextElement();
            String name = a.toString().trim();
            String sec = name.substring(0, name.indexOf("."));
            if(sec.equals("adjustable")){
                adjustValues.add(new AdjustValue(name.substring(name.indexOf(".")+1).trim(), Double.parseDouble(properties.getProperty(name))));
            }
        }
    }

    public static void intoFile(){
        ArrayList<String> allLines = new ArrayList<>();

        //creating lines
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(propFileName)));
        }
        catch (FileNotFoundException e){
            System.out.println("WARNING: Properties file not found");
        }
        try
        {
            String section = "";
            String line;

            while( (line = br.readLine())!=null )
            {
                if( line.startsWith(";") ) {
                    allLines.add(line);
                    continue;
                }
                if( line.startsWith("[") )
                {
                    section = line.substring(1,line.lastIndexOf("]")).trim();
                    allLines.add(line);
                    continue;
                }
                //if it's a property or blank
                allLines.add(createPropLine(section, line));
            }
            br.close();
        }
        catch (IOException e){
            System.out.println("WARNING: Unable to read properties");
        }

        //writing into file
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(propFileName)));
        }
        catch (FileNotFoundException e){
            System.out.println("WARNING: Properties file not found");
        }
        try {
            for (String line : allLines) {
                bw.write(line+"\r\n");
            }
            bw.close();
        }
        catch (IOException e){
            System.out.println("WARNING: Unable to write properties");
        }
    }

    private static String createPropLine(String section, String line){
        String result = "";
        int equalIndex = line.indexOf("=");
        if( equalIndex > 0 )
        {
            String name = line.substring(0, equalIndex).trim();
            String value = Settings.properties.getProperty(section+"."+name);
            result = name+" = "+value;
        }

        return result;
    }
}
