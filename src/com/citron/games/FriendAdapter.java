package com.citron.games;

import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FriendAdapter extends BaseAdapter {
	private ArrayList<String> friendsList_;
	private ListsManager listsManager_;
	private Context context_;

	public FriendAdapter(Context context, ListsManager listsManager) {
		context_ = context;
		listsManager_ = listsManager;	
		friendsList_ = listsManager.getFriendsList();
	}

	public int getCount() {
		return friendsList_.size();
	}

	public Object getItem(int position) {
		return friendsList_.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
   
	public void refresh(){
		friendsList_ = listsManager_.getFriendsList(); 
		this.notifyDataSetChanged();
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		friendsList_ = listsManager_.getFriendsList();

		if(convertView == null){

			LayoutInflater inflater = LayoutInflater.from(context_);
			convertView = inflater.inflate(R.layout.user, null);
		}
		/*Name*/
		TextView name = (TextView)convertView.findViewById(R.id.name);
		name.setText( friendsList_.get(position));

		/*Game*/
		//TextView game = (TextView)convertView.findViewById(R.id.game);
		//game.setText( friendsList_.get(position).game_ );

		/*Icon*/
		ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.blue);
		
		return(convertView);
	}
}
