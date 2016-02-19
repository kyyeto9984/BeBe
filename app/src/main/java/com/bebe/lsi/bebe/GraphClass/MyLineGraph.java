package com.bebe.lsi.bebe.GraphClass;

/**
 * Created by Jun on 2015-05-12.
 */
public class MyLineGraph {
    private String name = null;
    private int color = 0xFFFF4081;
    private float[] coordinateArr = null;
    private int bitmapResource = -1;

    public MyLineGraph(int color, float[] coordinateArr) {
        this.color = color;
        this.setCoordinateArr(coordinateArr);
    }

    public MyLineGraph(String name, int color, float[] coordinateArr) {
        this.name = name;
        this.color = color;
        this.setCoordinateArr(coordinateArr);
    }

    public MyLineGraph(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public MyLineGraph(String name, int color, float[] coordinateArr, int bitmapResource) {
        this.name = name;
        this.color = color;
        this.setCoordinateArr(coordinateArr);
        this.bitmapResource = bitmapResource;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float[] getCoordinateArr() {
        return this.coordinateArr;
    }

    public void setCoordinateArr(float[] coordinateArr) {
        this.coordinateArr = coordinateArr;
    }

    public int getBitmapResource() {
        return this.bitmapResource;
    }

    public void setBitmapResource(int bitmapResource) {
        this.bitmapResource = bitmapResource;
    }
}
