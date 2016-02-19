package com.bebe.lsi.bebe.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bebe.lsi.bebe.R;
import com.google.android.gms.maps.model.LatLng;

public class PlaceActivity extends AppCompatActivity implements View.OnClickListener,LocationListener{

    private ImageView gynecology; // 산부인과
    private ImageView postnatalcare; // 산후조리원
    private ImageView aesthetic; // 에스테틱
    private ImageView babyclinic; // 소아과
    private ImageView studio; // 스튜디오
    private ImageView party; // 돌잔치
    private ImageView kidscafe; // 키즈카페
    private ImageView animal; // 자연동물체험
    private ImageView culture; // 영유아문화
    private ImageView healthcenter; // 보건소
    private ImageView daycarecenter; //어린이집
    private ImageView kindergarten; // 유치원

    public LocationManager manager;
    private String provider;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("주변정보");
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.location_ColorPrimary));
        }
        setSupportActionBar(toolbar);
        gynecology = (ImageView)findViewById(R.id.gynecology);
        postnatalcare = (ImageView)findViewById(R.id.postnatalcare);
        kindergarten = (ImageView)findViewById(R.id.kindergarten);
        daycarecenter = (ImageView)findViewById(R.id.daycarecenter);
        animal = (ImageView)findViewById(R.id.animal);
        culture = (ImageView)findViewById(R.id.culture);
        aesthetic = (ImageView)findViewById(R.id.aesthetic);
        party = (ImageView)findViewById(R.id.party);
        studio = (ImageView)findViewById(R.id.studio);
        kidscafe = (ImageView)findViewById(R.id.kidscafe);
        healthcenter = (ImageView)findViewById(R.id.healthcenter);
        babyclinic = (ImageView)findViewById(R.id.babyclinic);
        gynecology.setOnClickListener(this);
        culture.setOnClickListener(this);
        animal.setOnClickListener(this);
        postnatalcare.setOnClickListener(this);
        aesthetic.setOnClickListener(this);
        kindergarten.setOnClickListener(this);
        daycarecenter.setOnClickListener(this);
        party.setOnClickListener(this);
        studio.setOnClickListener(this);
        kidscafe.setOnClickListener(this);
        healthcenter.setOnClickListener(this);
        babyclinic.setOnClickListener(this);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = manager.getBestProvider(criteria, true);
        if (provider.trim().equals("passive")) {
            checkGPSService();
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, PlaceActivity.this);
    }

    private boolean checkGPSService() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
            return false;

        } else {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, PlaceActivity.this);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent in = new Intent(getApplicationContext() , LocationActivity.class);
        switch (v.getId()){
            case R.id.gynecology :
                in.putExtra("data","%EC%82%B0%EB%B6%80%EC%9D%B8%EA%B3%BC");
                break;
            case R.id.animal :
                in.putExtra("data","%EC%9E%90%EC%97%B0%EC%B2%B4%ED%97%98");
                break;
            case R.id.aesthetic :
                in.putExtra("data","%EC%97%90%EC%8A%A4%ED%85%8C%ED%8B%B1");
                break;
            case R.id.postnatalcare :
                in.putExtra("data","%EC%82%B0%ED%9B%84%EC%A1%B0%EB%A6%AC%EC%9B%90");
                break;
            case R.id.culture :
                in.putExtra("data","%EC%9C%A0%EC%95%84%EB%AC%B8%ED%99%94%EC%84%BC%ED%84%B0");
                break;
            case R.id.kindergarten :
                in.putExtra("data","%EC%9C%A0%EC%B9%98%EC%9B%90");
                break;
            case R.id.daycarecenter :
                in.putExtra("data","%EC%96%B4%EB%A6%B0%EC%9D%B4%EC%A7%91");
                break;
            case R.id.party :
                in.putExtra("data","%EB%8F%8C%EC%9E%94%EC%B9%98");
                break;
            case R.id.studio :
                in.putExtra("data","%EC%8A%A4%ED%8A%9C%EB%94%94%EC%98%A4");
                break;
            case R.id.kidscafe :
                in.putExtra("data","%ED%82%A4%EC%A6%88%EC%B9%B4%ED%8E%98");
                break;
            case R.id.healthcenter :
                in.putExtra("data","%EB%B3%B4%EA%B1%B4%EC%86%8C");
                break;
            case R.id.babyclinic :
                in.putExtra("data","%EC%86%8C%EC%95%84%EA%B3%BC");
                break;
        }
        if(latLng!= null) {
            in.putExtra("latitude",latLng.latitude);
            in.putExtra("longtitude",latLng.longitude);
            startActivity(in);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
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
}
