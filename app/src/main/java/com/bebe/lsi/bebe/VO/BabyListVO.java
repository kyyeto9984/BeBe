package com.bebe.lsi.bebe.VO;

import android.graphics.Bitmap;

/**
 * Created by LSJ on 2015-08-30.
 */
public class BabyListVO {

    private int number;
    private String name;
    private String month;
    private String birth;
    private float[] height;
    private String gender;
    private Bitmap Image;

    public BabyListVO() {
    }

    public BabyListVO(int number, String name, String month, String birth, float[] height, String gender, Bitmap image) {
        this.number = number;
        this.name = name;
        this.month = month;
        this.birth = birth;
        this.height = height;
        this.gender = gender;
        Image = image;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public float[] getHeight() {
        return height;
    }

    public void setHeight(float[] height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }
}
