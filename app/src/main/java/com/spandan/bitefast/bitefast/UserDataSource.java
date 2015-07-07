package com.spandan.bitefast.bitefast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by rubbernecker on 7/7/15.
 */
public class UserDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper2 dbHelper;
    private String[] allColumns = { MySQLiteHelper2.COLUMN_ID,
            MySQLiteHelper2.COLUMN_PHN,MySQLiteHelper2.COLUMN_NAME,MySQLiteHelper2.COLUMN_EMAIL, MySQLiteHelper2.COLUMN_ADDR1, MySQLiteHelper2.COLUMN_ADDR2, MySQLiteHelper2.COLUMN_CITY };

    public UserDataSource(Context context) {
        dbHelper = new MySQLiteHelper2(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createChat(DeviceLoginBean deviceUserBean) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper2.COLUMN_ID, deviceUserBean.androidId);
        values.put(MySQLiteHelper2.COLUMN_PHN, deviceUserBean.phone);
        values.put(MySQLiteHelper2.COLUMN_NAME, deviceUserBean.name);
        values.put(MySQLiteHelper2.COLUMN_EMAIL,deviceUserBean.email);
        values.put(MySQLiteHelper2.COLUMN_ADDR1,deviceUserBean.addr1);
        values.put(MySQLiteHelper2.COLUMN_ADDR1,deviceUserBean.addr2);
        values.put(MySQLiteHelper2.COLUMN_CITY,deviceUserBean.city);
        database.insert(MySQLiteHelper2.TABLE_USER, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper2.TABLE_USER,
                allColumns, MySQLiteHelper2.COLUMN_ID + " = " + deviceUserBean.androidId, null,
                null, null, null);
        cursor.moveToFirst();
        deviceUserBean = cursorToComment(cursor);
        cursor.close();
        if(deviceUserBean!=null)
            return true;
        return false;
    }


    public List<DeviceLoginBean> getAllChats(String id) {
        List<DeviceLoginBean> chats = new ArrayList<DeviceLoginBean>();

        Cursor cursor = database.query(MySQLiteHelper2.TABLE_USER,
                allColumns, MySQLiteHelper2.COLUMN_ID + " = \'" + id + "\'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DeviceLoginBean deviceUserBean = cursorToComment(cursor);
            chats.add(deviceUserBean);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return chats;
    }

    public DeviceLoginBean getSortedChatMessages(String phn){
        DeviceLoginBean bean= null;
        List<DeviceLoginBean> beans=getAllChats(phn);
        Iterator<DeviceLoginBean> itr=beans.iterator();
        boolean left=false;
        while(itr.hasNext()){
            bean=itr.next();
            return bean;
        }
        return bean;
    }

    private DeviceLoginBean cursorToComment(Cursor cursor) {
        DeviceLoginBean deviceUserBean = new DeviceLoginBean(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
        return deviceUserBean;
    }
}
