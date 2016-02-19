package com.bebe.lsi.bebe.GoogleMap;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LSJ on 2015-08-26.
 */
public class GooglePlacesReadTask extends MyAsyncTask<Object, Integer, String> {

    String googlePlacesData = null;
    GoogleMap googleMap;

    public GooglePlacesReadTask(ExcuteFinish excuteFinish){
        setOnExcuteFinish(excuteFinish);
    }

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];

            String googlePlacesUrl = (String) inputObj[1];
            Https http = new Https();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
        placesDisplayTask.setOnExcuteFinish(getOnExcuteFinish());
        placesDisplayTask.setOnExcuteFinishListener(getOnExcuteFinishListener());
    }
}