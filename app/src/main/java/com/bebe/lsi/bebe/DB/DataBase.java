package com.bebe.lsi.bebe.DB;

import android.provider.BaseColumns;

/**
 * Created by JYW on 2015-04-27.
 */
public class DataBase {
    public static final class CreateDB implements BaseColumns {
        public static final String _ID = "_id";
        public static final String NICKNAME = "nickname";
        public static final String NAME = "name";
        public static final String BIRTH = "birth";
        public static final String IMAGE = "image";
        public static final String GENDER = "gender";
        public static final String NUMBER = "number";
        public static final String TABLENAME = "bebe";
        public static final String CREATE = "create table "+TABLENAME+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +NICKNAME+" text not null,"
                +NAME+" text not null,"
                +GENDER+" text not null,"
                +IMAGE+" text not null,"
                +NUMBER+" text not null,"
                +BIRTH+" text not null );";
    }

    public static final class CreateGraphDB implements BaseColumns {
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String DAY = "day";
        public static final String NICKNAME = "nickname";
        public static final String MONTH = "month";
        public static final String HEIGHT = "height";
        public static final String TABLENAME = "graphDB";
        public static final String CREATE = "create table "+TABLENAME+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +NAME+" text not null,"
                +NICKNAME+" text not null,"
                +DAY+" text not null,"
                +MONTH+" text not null,"
                +HEIGHT+" text not null );";
    }

    public static final class CreateDiary implements BaseColumns {
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String IMAGE = "image";
        public static final String DATE = "date";
        public static final String TABLENAME = "diary";
        public static final String CREATE = "create table "+TABLENAME+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +TITLE+" text not null,"
                +IMAGE+" text,"
                +CONTENT+" text not null,"
                +DATE+" text not null);";
    }
}
