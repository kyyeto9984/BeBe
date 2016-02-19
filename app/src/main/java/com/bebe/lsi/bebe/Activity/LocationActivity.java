package com.bebe.lsi.bebe.Activity;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bebe.lsi.bebe.Adapter.ListAdapter;
import com.bebe.lsi.bebe.GoogleMap.ExcuteFinish;
import com.bebe.lsi.bebe.GoogleMap.GooglePlacesReadTask;
import com.bebe.lsi.bebe.GoogleMap.MapVO;
import com.bebe.lsi.bebe.GoogleMap.OnExcuteFinishListener;
import com.bebe.lsi.bebe.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnCameraChangeListener,LocationListener,GoogleMap.OnMarkerClickListener,OnExcuteFinishListener{

    private final String GOOGLE_API_KEY = "AIzaSyCigPN5NyHt49X1Z62pFrDlDE4f1ZivHi4";
    private final String SERVER_KEY = "AIzaSyCT5ujpv3EFs1d5-Yp5ot-iftQjOH7Ctl0";
    private GoogleMap map;
    public MarkerOptions markerOptions;
    public LocationManager manager;
    private final int PROXIMITY_RADIUS = 5000;
    private String provider;
    public LatLng latLng;
    private boolean locationTag = true;
    private String LocationData = null;
    private ExcuteFinish CallbackVO;
    private ArrayList<MapVO> list = new ArrayList<>();
    private ListView listview;
    private ListAdapter Adapter;
    private ScrollView scrollView;
    /*
        산부인과 -> %EC%82%B0%EB%B6%80%EC%9D%B8%EA%B3%BC
        산후조리원 -> %EC%82%B0%ED%9B%84%EC%A1%B0%EB%A6%AC%EC%9B%90
        유치원 -> %EC%9C%A0%EC%B9%98%EC%9B%90
        어린이집 -> %EC%96%B4%EB%A6%B0%EC%9D%B4%EC%A7%91
        소아과 -> %EC%86%8C%EC%95%84%EA%B3%BC
        스튜디오 -> %EC%8A%A4%ED%8A%9C%EB%94%94%EC%98%A4
        키즈카페 -> %ED%82%A4%EC%A6%88%EC%B9%B4%ED%8E%98
        보건소 -> %EB%B3%B4%EA%B1%B4%EC%86%8C
        소아한의원 -> %EC%86%8C%EC%95%84%ED%95%9C%EC%9D%98%EC%9B%90
        https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=35.1104586,126.8773861&name=%EC%82%B0%EB%B6%80%EC%9D%B8%EA%B3%BC&radius=8000&sensor=true&key=AIzaSyCT5ujpv3EFs1d5-Yp5ot-iftQjOH7Ctl0
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("위치찾기");
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.location_ColorPrimary));
        }
        CallbackVO = new ExcuteFinish(list);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        LocationData = bundle.getString("data");
        double latitude = bundle.getDouble("latitude");
        double longtitude = bundle.getDouble("longtitude");
        latLng = new LatLng(latitude,longtitude);
        MapFragment googleMap;
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        googleMap.getMapAsync(this);
        map = googleMap.getMap();
        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(getApplicationContext());
        listview = (ListView)findViewById(R.id.listview);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = manager.getBestProvider(criteria, true);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, LocationActivity.this);
    }

    private void searchOnMap(double latitude, double longitude) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=");
        googlePlacesUrl.append(latitude);
        googlePlacesUrl.append(",");
        googlePlacesUrl.append(longitude);
        googlePlacesUrl.append("&name=");
        googlePlacesUrl.append(LocationData==null?"%EC%82%B0%EB%B6%80%EC%9D%B8%EA%B3%BC":LocationData);
        googlePlacesUrl.append("&radius=");
        googlePlacesUrl.append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=");
        googlePlacesUrl.append(SERVER_KEY);
        CallbackVO = new ExcuteFinish(list);
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(CallbackVO);
        googlePlacesReadTask.setOnExcuteFinishListener(this);
        Object[] toPass = new Object[2];
        toPass[0] = map;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);
    }

    private void showCurrentLocation(double latitude, double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 12));
        map.setOnMarkerClickListener(this);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        searchOnMap(latitude, longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (locationTag) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.i("GPS",latitude+":"+longitude);
            //map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("내위치"));
            showCurrentLocation(latitude, longitude);
            locationTag = false;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        searchOnMap(cameraPosition.target.latitude,cameraPosition.target.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    public void OnExcuteFinish(ExcuteFinish excuteFinish) {
        Adapter = new ListAdapter(getApplicationContext() , excuteFinish.getList() , getLayoutInflater());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) listview.getLayoutParams();
        lp.height = (283 * excuteFinish.getList().size());
        Log.i("asdasd", "height=" + lp.height + " : " + excuteFinish.getList().size());
        listview.setLayoutParams(lp);
        listview.requestLayout();
        listview.setAdapter(Adapter);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0,0);
    }
}
