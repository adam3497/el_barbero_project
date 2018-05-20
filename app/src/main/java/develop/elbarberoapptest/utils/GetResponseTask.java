package develop.elbarberoapptest.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by adma9717 on 20/05/18.
 */

public class GetResponseTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... dirImages) {
        try{
            URL url = new URL(dirImages[0]);
            HttpsURLConnection.setDefaultSSLSocketFactory(new TLSSocketFactory());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            return getStringFromStream(urlConnection.getInputStream());
        } catch (Exception e) {
            Log.e("GetResponseTask", "Error. Exception: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param stream
     * @return all the characters in the stream as a single String
     * @throws IOException
     */
    private static String getStringFromStream(InputStream stream) throws IOException {
        if (stream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[2048];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                int counter;
                while ((counter = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, counter);
                }
            }finally {
                stream.close();
            }
            return writer.toString();
        } else {
            throw new  IOException();
        }
    }
}
