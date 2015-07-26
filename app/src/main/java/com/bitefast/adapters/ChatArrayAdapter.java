package com.bitefast.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitefast.beans.ChatMessage;
import com.bitefast.R;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

	private TextView chatText;
	private ImageView imageView;
	private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
	private LinearLayout singleMessageContainer;
	private TextView timeView;
	private LinearLayout.LayoutParams params;
	private ChatMessage chatMessageObj;
	private LinearLayout status_Time;

	@Override
	public void add(ChatMessage object) {
		Logger.getLogger(this.getClass().getName() + ":ADDING TO ADAPTER:").log(Level.INFO, object.toString());
		chatMessageList.add(object);
		super.add(object);
	}


	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return this.chatMessageList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		chatMessageObj = getItem(position);
		LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = vi.inflate(R.layout.activity_chat_singlemessage, null);
			holder = createViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		boolean myMsg = chatMessageObj.isLeft() ;//Just a dummy check to simulate whether it me or other sender
		setAlignment(holder, myMsg);
		holder.singleMessage.setText(chatMessageObj.getMessage());
		holder.time.setText(chatMessageObj.gettS());
		if(position-1>0&&getItem(position-1).isLeft()==myMsg){
			holder.time.setVisibility(View.GONE);
		}

		return convertView;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}


	private void setAlignment(ViewHolder holder, boolean isMe) {
		if (!isMe) {
			holder.contentWithBG.setBackgroundResource(R.drawable.in_bg);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
			layoutParams.gravity = Gravity.RIGHT;
			holder.contentWithBG.setLayoutParams(layoutParams);

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.content.setLayoutParams(lp);
			layoutParams = (LinearLayout.LayoutParams) holder.singleMessage.getLayoutParams();
			layoutParams.gravity = Gravity.RIGHT;
			holder.singleMessage.setLayoutParams(layoutParams);

			layoutParams = (LinearLayout.LayoutParams) holder.time.getLayoutParams();
			layoutParams.gravity = Gravity.RIGHT;
			holder.time.setLayoutParams(layoutParams);
		} else {
			holder.contentWithBG.setBackgroundResource(R.drawable.out_bg);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.contentWithBG.setLayoutParams(layoutParams);

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.content.setLayoutParams(lp);
			layoutParams = (LinearLayout.LayoutParams) holder.singleMessage.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.singleMessage.setLayoutParams(layoutParams);

			layoutParams = (LinearLayout.LayoutParams) holder.time.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.time.setLayoutParams(layoutParams);
		}
	}

	private ViewHolder createViewHolder(View v) {
		ViewHolder holder = new ViewHolder();
		holder.singleMessage = (TextView) v.findViewById(R.id.singleMessage);
		holder.content = (LinearLayout) v.findViewById(R.id.content);
		holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
		holder.time = (TextView) v.findViewById(R.id.time);
		return holder;
	}


	private static class ViewHolder {
		public TextView singleMessage;
		public TextView time;
		public LinearLayout content;
		public LinearLayout contentWithBG;
	}

}