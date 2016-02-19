package com.bebe.lsi.bebe.GraphClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.bebe.lsi.bebe.DB.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lsj on 2015-08-07.
 */
public class GraphData {

    private final String TAG = "GRAPHDATA";
    private float[] BoyGraph = {50.8f, 55.2f, 59.2f, 62.6f, 65.3f, 67.0f, 69.1f, 70.3f,
            71.9f, 73.5f, 74.5f, 76.2f , 77.7f , 78.5f , 79.3f , 80.2f , 81.0f , 81.8f , 82.7f ,
            83.5f , 84.3f , 85.2f , 86.0f , 87.0f , 88.0f}; // 이 배열들이 점이 찍힐 값임
    private float[] GirlGraph = {50.0f, 54.2f, 58.1f, 61.3f, 63.8f, 65.8f, 67.7f, 69.0f,
            70.6f, 72.2f, 73.6f, 75.1f , 76.6f , 77.4f , 78.2f , 79.0f , 80.0f , 81.0f , 82.0f ,
            82.7f , 83.4f , 84.3f ,85.2f , 86.1f , 87.0f}; // 이 배열들이 점이 찍힐 값임
    private String[] DefaultmonthArray = {"Jan","Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public float[] getBoyArray(int month){
        float[] array = new float[6];
        if(month > 5) {
            int q = 0;
            for (int i = 4; i >= 1; i--) {
                array[q] = BoyGraph[month - i];
                q++;
            }
            array[4] = BoyGraph[month];
            array[5] = BoyGraph[month+1];
        }else{
            for(int i=0;i<6;i++){
                array[i] = BoyGraph[i];
            }
        }
        return array;
    }

    public float[] getGirlArray(int month){ // 6   2 3 4 5 6 7
        float[] array = new float[6];
        if(month > 5) {
            int q = 0;
            for (int i = 4; i >= 1; i--) {
                array[q] = GirlGraph[month - i];
                q++;
            }
            array[4] = GirlGraph[month];
            array[5] = GirlGraph[month+1];
        }else{
            for(int i=0;i<6;i++){
                array[i] = GirlGraph[i];
            }
        }
        return array;

    }

    public int getBabyMonth(String day){
        if(day.equals("999999")){
            day = "0";
        }
        int days = Integer.parseInt(day);
        Log.i("D2DD222", days + "");
        int returnMonth = 0;
        if(days/30 > 0 ){
            returnMonth = days/30;
        }
        return returnMonth;
    }

    public static String[] getBabyMonth(String[] DefaultmonthArray , int month){
        String[] baby = new String[6];
        if(month > 5){
            int q = 0;
            for(int i=4;i>=1;i--) {
                baby[q] = DefaultmonthArray[month - i];
                q++;
            }
            baby[q] = DefaultmonthArray[month];
           baby[q+1] = DefaultmonthArray[month+1];
        }else if(month < 4){
            for(int i=0;i<6;i++){
                baby[i] = DefaultmonthArray[(i)];
            }
        }
        return baby;
    }


    public String getBabyBirth(Context context){
        String babyName = "";
        String babyBirth = "";
        long diffDays=0;
        try {
            SharedPreferences pref = context.getSharedPreferences("currentBabyName",context.MODE_PRIVATE);
            babyName = pref.getString("Name", null);
            babyBirth = pref.getString("LastDate",null);
            if(babyName != null) {
                if (babyBirth != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date currentTime = new Date();
                    String dTime = formatter.format(currentTime);
                    Date birth = formatter.parse(babyBirth);
                    Date currentDay = formatter.parse(dTime);
                    long diff = currentDay.getTime() - birth.getTime();
                    diffDays = diff / (24 * 60 * 60 * 1000);
                    if(diffDays == 0){
                        diffDays = 999999;
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception by getBabyBirth =" + e.getMessage());
        }
        return diffDays+"";
    }

    public float[] getBabyHeight(Context context , String babyname){
        ArrayList<Float> array = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences("Login",context.MODE_PRIVATE);
        String nickname= pref.getString("nickname",null);
        DBHelper helper = new DBHelper();
        helper.open(context);
        Cursor cursor = helper.getBabyHeightArray(babyname,nickname);
        int i=0;
        while(cursor.moveToNext()){
            float num = cursor.getFloat(cursor.getColumnIndex("height"));
            if(num != 0.0f) {
                array.add(num);
            }
            i++;
        }
        float[] floatarray = new float[array.size()];
        for(int j=0;j<array.size();j++){
            floatarray[j] = array.get(j);
        }
        return floatarray;
    }



    public float[] getBoyGraph() {
        return BoyGraph;
    }

    public void setBoyGraph(float[] boyGraph) {
        BoyGraph = boyGraph;
    }

    public float[] getGirlGraph() {
        return GirlGraph;
    }

    public void setGirlGraph(float[] girlGraph) {
        GirlGraph = girlGraph;
    }

    public String[] getDefaultmonthArray() {
        return DefaultmonthArray;
    }

    public void setDefaultmonthArray(String[] defaultmonthArray) {
        DefaultmonthArray = defaultmonthArray;
    }
}
