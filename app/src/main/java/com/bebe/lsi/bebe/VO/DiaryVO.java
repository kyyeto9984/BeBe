package com.bebe.lsi.bebe.VO;

import android.graphics.Bitmap;

/**
 * Created by LSJ on 2015-08-18.
 */
public class DiaryVO {

    private String title;
    private String content;
    private Bitmap bitmap;
    private String date;

    public DiaryVO(){}

    public DiaryVO(String title, String content, Bitmap bitmap, String date) {
        this.title = title;
        this.content = content;
        this.bitmap = bitmap;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
