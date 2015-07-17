package com.bitefast.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.widget.TextView;

import com.bitefast.beans.ChatMessage;
import com.bitefast.R;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

	private TextView chatText;
	private ImageView imageView;
	private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
	private LinearLayout singleMessageContainer;

	@Override
	public void add(ChatMessage object) {
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
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
		}

		singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
		imageView = (ImageView) row.findViewById(R.id.status);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)chatText.getLayoutParams();
        ChatMessage chatMessageObj = getItem(position);
        chatText.setText(Html.fromHtml(chatMessageObj.getMessage()+" <sub><small>"+chatMessageObj.gettS()+"</small></sub>"));
		if (chatMessageObj.isLeft()) {
            chatText.setPadding(25, 7, 15, 7);
            params.setMargins(0, 10, 0, 4);
			imageView.setVisibility(View.INVISIBLE);
        }
		else {
            chatText.setPadding(15, 7, 25, 7);
            params.setMargins(10, 10, 0, 4);
        }
		chatText.setBackgroundResource(chatMessageObj.isLeft() ? R.drawable.bubble_b : R.drawable.bubble_c);
		chatText.setTextColor(Color.BLACK);

		if(!chatMessageObj.isSent() && !chatMessageObj.isDelivered()){
			imageView.setImageResource(R.drawable.msg_pending);
		}
		if(chatMessageObj.isSent()){
			imageView.setImageResource(R.drawable.sent);
		}
		if(chatMessageObj.isDelivered()){
			imageView.setImageResource(R.drawable.delivered);
		}

        if(position>=1) {
            ChatMessage prev = getItem(position - 1);
            if (prev != null) {
                if (prev.isLeft() == chatMessageObj.isLeft()) {
                    params.setMargins(10, 0, 5, 0);
                    chatText.setPadding(15,7,15,7);
                    chatText.setBackgroundResource(chatMessageObj.isLeft() ? R.drawable.bubble_b1 : R.drawable.bubble_c1);
                }
            }
        }
		else{
			params.setMargins(0, 20, 0, 4);
		}
        chatText.setLayoutParams(params);
        singleMessageContainer.setGravity(chatMessageObj.isLeft() ? Gravity.LEFT : Gravity.RIGHT);

        return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}