package com.spandan.bitefast.bitefast;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper2 extends SQLiteOpenHelper {

    public static final String TABLE_USER = "UserLogin";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ANDRDOI_ID = "androidId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ADDR1 = "addr1";
    public static final String COLUMN_ADDR2 = "addr2";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHN = "phonenum";


    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER + "(" + COLUMN_ID
            + " text primary key autoincrement, " + COLUMN_ANDRDOI_ID
            + " text not null, "+ COLUMN_PHN
            + " text not null, "+ COLUMN_NAME
            + " text not null, "+ COLUMN_EMAIL
            + " text not null, "+ COLUMN_ADDR1
            + " text not null, "+ COLUMN_ADDR2
            + " text not null, "+ COLUMN_CITY
            + " text not null );";

    public MySQLiteHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper2.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

}