package com.bitefast.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper2 extends SQLiteOpenHelper {

    public static final String TABLE_USER = "UserList";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_TIMESTAMP= "timestamp";
    public static final String COLUMN_REGPHN="regPhn";

    private static final String DATABASE_NAME = "msgs.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, "+ COLUMN_READ
            + " text not null, "+ COLUMN_TIMESTAMP
            + " text not null, "+ COLUMN_REGPHN
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