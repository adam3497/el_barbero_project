package develop.elbarberoapptest.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adma9717 on 28/05/18.
 */

public class ServiceXmlFileUtils {

    public static String getImageUrlFor(String xmlFile) {
        String imageUrl = null;

        try{
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput (new ByteArrayInputStream(xmlFile.getBytes()),"UTF-8");
            int eventType = parser.getEventType();
            boolean imageFound = false;
            while(eventType != XmlPullParser.END_DOCUMENT && ! imageFound ){
                if(eventType == XmlPullParser.START_TAG && parser.getName().equals("metadata")){
                    while(!(eventType == XmlPullParser.START_TAG && parser.getName().equals("image"))){
                        eventType = parser.next();
                    }
                    eventType = parser.next();//Step from the start of the tag to its content
                    imageUrl = parser.getText();
                    imageFound = true;
                }
                eventType = parser.next();
            }
        }
        catch (Exception ex){
            Log.i("GetImageUrl", "No se pudo obtener la imagen");
            ex.printStackTrace();
        }

        return imageUrl;
    }

    public static String getPriceFor(String xmlFile) {
        String price = null;

        try{
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput (new ByteArrayInputStream(xmlFile.getBytes()),"UTF-8");
            int eventType = parser.getEventType();
            boolean priceFound = false;
            while(eventType != XmlPullParser.END_DOCUMENT && ! priceFound ){
                if(eventType == XmlPullParser.START_TAG && parser.getName().equals("metadata")){
                    while(!(eventType == XmlPullParser.START_TAG && parser.getName().equals("price"))){
                        eventType = parser.next();
                    }
                    eventType = parser.next();//Step from the start of the tag to its content
                    price = parser.getText();
                    priceFound = true;
                }
                eventType = parser.next();
            }
        }
        catch (Exception ex){
            Log.i("GetPrice", "No se pudo obtener el precio");
            ex.printStackTrace();
        }

        return price;
    }

    public static String getTitleFor(String xmlFile) {
        String title = null;

        try{
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput (new ByteArrayInputStream(xmlFile.getBytes()),"UTF-8");
            int eventType = parser.getEventType();
            boolean titleFound = false;
            while(eventType != XmlPullParser.END_DOCUMENT && ! titleFound ){
                if(eventType == XmlPullParser.START_TAG && parser.getName().equals("metadata")){
                    while(!(eventType == XmlPullParser.START_TAG && parser.getName().equals("title"))){
                        eventType = parser.next();
                    }
                    //We are at start of desc tag must get Text
                    eventType = parser.next();//Step from the start of the tag to its content
                    title = parser.getText();
                    titleFound = true;
                }
                eventType = parser.next();
            }
        }
        catch (Exception ex){
            Log.i("GetTitle", "No se pudo obtener el titulo");
            ex.printStackTrace();
        }

        return title;
    }

    /**
     * xmlFile is the XML meta file parsed to string
     * Searches a description and returns it if it is in xmlFile
     * or null if it is not there
     */
    public static String getDescriptionFor(String xmlFile){
        String description = null;

        try{
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput (new ByteArrayInputStream(xmlFile.getBytes()),"UTF-8");
            int eventType = parser.getEventType();
            boolean descriptionFound = false;
            while(eventType != XmlPullParser.END_DOCUMENT && ! descriptionFound ){
                if(eventType == XmlPullParser.START_TAG && parser.getName().equals("metadata")){
                    while(!(eventType == XmlPullParser.START_TAG && parser.getName().equals("description"))){
                        eventType = parser.next();
                    }
                    //We are at start of desc tag must get Text
                    eventType = parser.next();//Step from the start of the tag to its content
                    description = parser.getText();
                    descriptionFound = true;
                }
                eventType = parser.next();
            }
        }
        catch (Exception ex){
            Log.i("GetDescription", "No se pudo obtener la descripción");
            ex.printStackTrace();
        }

        return description;
    }

    /**
     * parse the string (representation of a json) to get only the values associated with
     * key "name", which are the file names of the folder requested before.
     */
    public static List<String> parseResponse(String response) {
        List<String> options = new ArrayList<String>();
        try {
            // create JSON Object
            JSONArray jsonArray = new JSONArray(response);
            for (int i= 0; i < jsonArray.length(); i++) {
                // create json object for every element of the array
                JSONObject object = jsonArray.getJSONObject(i);
                // get the value associated with
                options.add( object.getString("name") );
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return options;
    }

}
