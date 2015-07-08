package com.bitefast.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bitefast.beans.UserListItem;

import java.util.ArrayList;
import java.util.List;

public class UserListArrayAdapter extends ArrayAdapter<UserListItem> {

    private TextView chatText;
    private List<UserListItem> userListItemList = new ArrayList<UserListItem>();


    @Override
    public void add(UserListItem object) {
        userListItemList.add(object);
        super.add(object);
    }

    @Override
    public void remove(UserListItem object) {
        userListItemList.remove(object);
        super.remove(object);
    }

    public UserListArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.userListItemList.size();
    }

    public UserListItem getItem(int index) {
        return this.userListItemList.get(getCount() - index - 1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        UserListItem itemValue = getItem(position);
        TextView item=(TextView) row.findViewById(android.R.id.text1);
        item.setText(itemValue.message);
        item.setBackgroundColor(itemValue.readStatus ? Color.LTGRAY : Color.GRAY);
        return row;
    }


    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}