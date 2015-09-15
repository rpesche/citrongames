package com.citron.games;

import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class UserAdapter extends BaseAdapter {
	private ArrayList<User> usersList_;
	private ListsManager listsManager_;
	private Context context_;

	public UserAdapter(Context context, ListsManager listsManager) {
		context_ = context;
		listsManager_ = listsManager;	
		usersList_ = listsManager.getUsersList();
	}

	public int getCount() {
		return usersList_.size();
	}

	public Object getItem(int position) {
		return usersList_.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
   
	public void refresh(){
		usersList_ = listsManager_.getUsersList(); 
		this.notifyDataSetChanged();
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		usersList_ = listsManager_.getUsersList();

		if(convertView == null){

			LayoutInflater inflater = LayoutInflater.from(context_);
			convertView = inflater.inflate(R.layout.user, null);
		}
		/*Name*/
		TextView name = (TextView)convertView.findViewById(R.id.name);
		name.setText( usersList_.get(position).name_ );

		/*Game*/
		//TextView game = (TextView)convertView.findViewById(R.id.game);
		//game.setText( friendsList_.get(position).game_ );

		/*Icon*/
		ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.grey);
		if ( usersList_.get(position).friend_ == 1)
			icon.setImageResource(R.drawable.blue);

		return(convertView);
	}
}
