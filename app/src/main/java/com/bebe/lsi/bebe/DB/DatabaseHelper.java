package com.bebe.lsi.bebe.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JYW on 2015-04-27.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBase.CreateDB.CREATE);
        db.execSQL(DataBase.CreateGraphDB.CREATE);
        db.execSQL(DataBase.CreateDiary.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ DataBase.CreateDB.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DataBase.CreateGraphDB.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DataBase.CreateDiary.TABLENAME);
        onCreate(db);
    }
}
