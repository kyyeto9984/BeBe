package com.bebe.lsi.bebe.GoogleMap;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LSJ on 2015-08-26.
 */
public class PlacesDisplayTask extends MyAsyncTask<Object, Integer, List<HashMap<String, String>>>{

    public JSONObject googlePlacesJson;
    public GoogleMap googleMap;
    public ArrayList<MapVO> datalist = new ArrayList<>();
    public ExcuteFinish excuteFinish;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();
        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
        excuteFinish = getOnExcuteFinish();
        datalist = excuteFinish.getList();
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                //BitmapDescriptor dis = (BitmapDescriptor)googlePlace.get("icon");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                // markerOptions.icon(icon)/* + " : " + vicinity*/
                markerOptions.title(placeName);
                MapVO vo = new MapVO(lat, lng, placeName, vicinity);
                googleMap.addMarker(markerOptions);
                datalist.add(vo);
            }
        }
        excuteFinish.setList(datalist);
        getOnExcuteFinishListener().OnExcuteFinish(excuteFinish);
    }
}
