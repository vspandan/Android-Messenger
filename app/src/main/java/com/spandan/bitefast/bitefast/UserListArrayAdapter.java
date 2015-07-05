package com.spandan.bitefast.bitefast;

import java.util.ArrayList;
import java.util.List;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserListArrayAdapter extends ArrayAdapter<String> {

	public UserListArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public UserListArrayAdapter(Context context, int resource, List<String> items) {
		super(context, resource, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		return row;
	}

}