package com.bitefast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rubbernecker on 7/7/15.
 */
public class UserDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper2 dbHelper;
    private String[] allColumns = { MySQLiteHelper2.COLUMN_ID,
            MySQLiteHelper2.COLUMN_NAME,MySQLiteHelper2.COLUMN_READ};

    public UserDataSource(Context context) {
        dbHelper = new MySQLiteHelper2(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createChat(UserListBean deviceUserBean) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper2.COLUMN_NAME, deviceUserBean.name);
        values.put(MySQLiteHelper2.COLUMN_READ, deviceUserBean.read);
        values.put(MySQLiteHelper2.COLUMN_READ, deviceUserBean.read);
        long id=database.insert(MySQLiteHelper2.TABLE_USER, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper2.TABLE_USER,
                allColumns, MySQLiteHelper2.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        deviceUserBean = cursorToComment(cursor);
        cursor.close();
        if(deviceUserBean!=null)
            return true;
        return false;
    }

    public boolean deleteChat(String  name) {
        long id=database.delete(MySQLiteHelper2.TABLE_USER, MySQLiteHelper2.COLUMN_NAME + " = " + name,
                null);
        if(id>0)
            return true;
        return false;
    }


    public List<UserListBean> getAllChats() {
        List<UserListBean> chats = new ArrayList<UserListBean>();

        Cursor cursor = database.query(MySQLiteHelper2.TABLE_USER,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserListBean deviceUserBean = cursorToComment(cursor);
            chats.add(deviceUserBean);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return chats;
    }

    public List<UserListBean> getSortedChatMessages(){
        List<UserListBean> beanList= new ArrayList<UserListBean>();
        List<UserListBean> beans=getAllChats();
        Iterator<UserListBean> itr=beans.iterator();
        boolean left=false;
        while(itr.hasNext()){
            beanList.add(itr.next());
        }
        return beanList;
    }

    private UserListBean cursorToComment(Cursor cursor) {
        UserListBean deviceUserBean = new UserListBean(cursor.getLong(0),cursor.getString(1),cursor.getString(2));
        return deviceUserBean;
    }
}
