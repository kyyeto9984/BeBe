package com.bebe.lsi.bebe.VO;

/**
 * Created by LSJ on 2015-08-18.
 */
public class GridItemVO {

    private String month;
    private String height;

    public GridItemVO(){}

    public GridItemVO(String month, String height) {
        this.month = month;
        this.height = height;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
