package common;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static common.ForexConstants.propFileName;


public class Settings{
    ArrayList<AdjustValue> adjustValues;
    ArrayList userConstants;
    public static Properties properties;

    public Settings(){
        adjustValues = new ArrayList<>();
        userConstants = new ArrayList();
        properties = new Properties();
    }

    public void setFromFile(){
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
                addProperty(section,line);
            }
            br.close();
        }
        catch (IOException e){
            System.out.println("WARNING: Unable to read properties");
        }
    }

    private void addProperty(String section,String line)
    {
        int equalIndex = line.indexOf("=");
        if( equalIndex > 0 )
        {
            String name = section+'.'+line.substring(0,equalIndex).trim();
            String value = line.substring(equalIndex+1).trim();
            properties.setProperty(name,value);
            if(section.equals("adjustable")){
                adjustValues.add(new AdjustValue(line.substring(0,equalIndex).trim(), Double.parseDouble(value)));
            }
        }
    }

}
