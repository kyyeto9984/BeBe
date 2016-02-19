package com.bebe.lsi.bebe.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JYW on 2015-04-27.
 */
public class DBHelper {

    private static final String DB_NAME = "bebe1.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase DB;
    private static DatabaseHelper DBHelper;
    private static Context context;
    private static DBHelper helper = new DBHelper();

    public static DBHelper open(Context c) throws SQLException {
        DBHelper = new DatabaseHelper(c, DB_NAME, null, DATABASE_VERSION);
        DB = DBHelper.getWritableDatabase();
        return helper;
    }

    public void close(){
        DB.close();
    }

    public long insertColumn(String name, String birth , String path , String nickname , String gender){ // 1-2 아이등록하기 AddBabyActivity
        String selectQuery = "select * from bebe where nickname=\""+nickname+"\"";
        Cursor cursor = DB.rawQuery(selectQuery , null);
        int count = cursor.getCount();
        String number = "0";
        number = (count+1)+"";
        String query = "select * from bebe where name=\""+name+"\" and nickname=\""+nickname+"\"";
        Cursor cnt = DB.rawQuery(query, null);
        if(cnt.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DataBase.CreateDB.NAME, name);
            values.put(DataBase.CreateDB.NICKNAME,nickname);
            values.put(DataBase.CreateDB.BIRTH, birth);
            values.put(DataBase.CreateDB.GENDER , gender);
            values.put(DataBase.CreateDB.IMAGE, path);
            values.put(DataBase.CreateDB.NUMBER, number);
            return DB.insert(DataBase.CreateDB.TABLENAME, null, values);
        }
        return -1;
    }

    public int updateColumn(String name , String path , String birth , String number){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateDB.NAME, name);
        values.put(DataBase.CreateDB.IMAGE, path);
        values.put(DataBase.CreateDB.BIRTH, birth);
        return DB.update(DataBase.CreateDB.TABLENAME, values, "number=?", new String[]{number});
    }

    public Cursor selectBabyCnt(String nickname){
        String query = "select name , Count(*) cnt from bebe where nickname=\""+nickname+"\"";
        return DB.rawQuery(query, null);
    }


    public Cursor selectMyBaby(int position , String nickname){ // 2 내아이정보 가져오기 -> AddBabyActivity
        String query = "select * from bebe where number=\""+position+"\" and nickname=\""+nickname+"\"";
        Log.i("asdasd" , query);
        Cursor cursor = DB.rawQuery(query, null);
        return cursor;
    }

    public boolean deleteMyBaby(int position){ // 3  내아이 정보 삭제하기 AddBabyActivity
        return DB.delete(DataBase.CreateDB.TABLENAME, "number=?", new String[]{position+""}) > 0;
    }

    public Cursor getBabyHeightArray(String name, String nickname){ // 4 내아이 키배열 받기 GraphData
        String query = "select height from graphDB where name=\""+name+"\" and nickname=\""+nickname+"\"  order by month asc limit 6";
        Log.i("asdasd","query = "+query);
        return DB.rawQuery(query, null);
    }

    public Cursor getHeightArray(String name){ // 4 내아이 키배열 받기 GraphData
        String query = "select height,month  from graphDB where name=\""+name+"\" order by month asc limit 6";
        return DB.rawQuery(query, null);
    }

    public long insertGraphData(String name , String height , String month , String day , String nickname){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateGraphDB.NAME, name);
        values.put(DataBase.CreateGraphDB.HEIGHT, height);
        values.put(DataBase.CreateGraphDB.NICKNAME, nickname);
        values.put(DataBase.CreateGraphDB.MONTH, month);
        values.put(DataBase.CreateGraphDB.DAY, day);
        return DB.insert(DataBase.CreateGraphDB.TABLENAME, null, values);
    }

    public Cursor getGraphArray(String name){ // 4 내아이 키배열 받기 GraphData
        String query = "select * from graphDB where name=\""+name+"\" order by month desc limit 6";
        return DB.rawQuery(query, null);
    }

    public boolean deleteGraph(String month){ // 3  내아이 정보 삭제하기 AddBabyActivity
        return DB.delete(DataBase.CreateGraphDB.TABLENAME, "month=\""+month+"\"", null) > 0;
    }

    public long InsertDiary(String title , String content , String date , String myurl){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateDiary.TITLE, title);
        values.put(DataBase.CreateDiary.CONTENT, content);
        values.put(DataBase.CreateDiary.IMAGE , myurl);
        values.put(DataBase.CreateDiary.DATE, date);
        return DB.insert(DataBase.CreateDiary.TABLENAME, null, values);
    }

    public Cursor getDiaryList(){ // 4 내아이 키배열 받기 GraphData
        String query = "select * from diary";
        return DB.rawQuery(query, null);
    }

    public Cursor getBabybirth(String name){
        String query = "select * from bebe where name=\""+name+"\"";
        return DB.rawQuery(query, null);
    }


    public Cursor getBabyDatas(String nickname){
        String query = "select * from bebe where nickname=\""+nickname+"\"";
        return DB.rawQuery(query, null);
    }

    /***
     *
     * @param name 아이의 이름
     * @return 아이의 정보를 리턴
     */

    public Cursor getBabyMonth(String name){
        String query = "select * from graphDB where name=\""+name+"\"";
        return DB.rawQuery(query, null);
    }

    ////

    public long insertGraphDB(String name , String month , String childrenHeight){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA );
        Date currentTime = new Date( );
        String day = formatter.format (currentTime);
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateGraphDB.NAME, name);
        values.put(DataBase.CreateGraphDB.DAY, day);
        values.put(DataBase.CreateGraphDB.MONTH , month);
        values.put(DataBase.CreateGraphDB.HEIGHT, childrenHeight);
        return DB.insert(DataBase.CreateGraphDB.TABLENAME , null , values);
    }

    public long insertGraphDB(String name, String day , String childrenHeight , boolean identifier){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateGraphDB.NAME, name);
        values.put(DataBase.CreateGraphDB.DAY, day);
        values.put(DataBase.CreateGraphDB.HEIGHT, childrenHeight);
        return DB.insert(DataBase.CreateGraphDB.TABLENAME , null , values);
    }


    public boolean deleteColumn(String name){

        return DB.delete(DataBase.CreateDB.TABLENAME, "name=\""+name+"\"", null) > 0;

    }

    public boolean deleteAll(){

        return DB.delete(DataBase.CreateDB.TABLENAME, null, null) > 0;

    }

    public Cursor getAllColumns(){

        return DB.query(DataBase.CreateDB.TABLENAME, null, null, null, null, null, null);

    }


    public int editMyGraphBaby(String IndentifierName , String children_height){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateGraphDB.HEIGHT,children_height);
        return DB.update(DataBase.CreateGraphDB.TABLENAME, values, "_id in (select _id from graphDB where name=? order by _id desc limit 1)", new String[]{IndentifierName});
    }

    public int editMyGraphBaby(String IndentifierName  , String children_height , boolean ss){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateGraphDB.HEIGHT,children_height);
        return DB.update(DataBase.CreateGraphDB.TABLENAME, values, "name=?", new String[]{IndentifierName});
    }


    public Cursor getSelectBabyHeight(String name){ // �׷�����񿡼� �Ʊ�Ű ��������
        String query = "select children_height from graphDB where name=\""+name+"\"";
        return DB.rawQuery(query, null);
    }
    public Cursor getSelectBabyData(String name){
        String query = "select * from graphDB where name=\""+name+"\"";
        return DB.rawQuery(query , null);
    }
    public Cursor getLastCheckDay(String name){
        String query = "select day from graphDB where name=\""+name+"\"";
        return DB.rawQuery(query , null);
    }
    public Cursor getSelectBabyBirth(String name){
        String query = "select birth from bebe where name=\""+name+"\"";
        return DB.rawQuery(query , null);
    }

    public Cursor getSelectBabyDataForBeBe(String name){
        String query = "select * from bebe where name =\""+name+"\"";
        Cursor c = DB.rawQuery(query , null);
        return c;
    }


    public Cursor getSelectBabyCheckDay(String name){
        String query = "select day from graphDB where name=\""+name+"\"";
        return DB.rawQuery(query , null);
    }

    public boolean selectIndexDeleteColumn(String name , String height){

        return DB.delete(DataBase.CreateGraphDB.TABLENAME, "name=\""+name+"\" and children_height=\""+height+"\"", null) > 0;

    }

    public Cursor getAllBaby(){
        return DB.query(DataBase.CreateDB.TABLENAME, null, null, null, null, null, null);
    }

}
