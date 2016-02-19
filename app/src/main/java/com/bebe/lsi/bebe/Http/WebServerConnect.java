package com.bebe.lsi.bebe.Http;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jun on 2015-06-19.
 */
public class WebServerConnect extends AsyncTask<String, Void , String> {

    private String HttpURL;
    private String line;
    private final String TAG = "WEBSERVERCONNECT";

    @Override
    protected String doInBackground(String... strings) {
        HttpURL = strings[0];
        Log.i(TAG, "http location = " + HttpURL);
        try {
            URL url = new URL(HttpURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            if(connection!=null){
                connection.setConnectTimeout(10000);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "EUC-KR"));
                    String data;
                    line = "";
                    while((data = br.readLine()) != null){
                        line+=data;
                    }
                    br.close();
                }
                connection.disconnect();
            }
        }catch(Exception e){
            Log.i(TAG, "Exception cause " + e.getMessage());
        }
        Log.i(TAG, "line=" + line);
        return line;
    }
}
