package com.bebe.lsi.bebe.GoogleMap;

/**
 * Created by LSJ on 2015-08-27.
 */
public class MapVO {

    private double latitude;
    private double longtitude;
    private String place_name;
    private String vicinity;

    public MapVO(){}

    public MapVO(double latitude, double longtitude, String place_name, String vicinity) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.place_name = place_name;
        this.vicinity = vicinity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
