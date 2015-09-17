package com.citron.games;

import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GameAdapter extends BaseAdapter {
	private ArrayList<String> gamesList_;
	private ListsManager listsManager_;
	private Context context_;

	public GameAdapter(Context context, ListsManager listsManager) {
		context_ = context;
		listsManager_ = listsManager;
		gamesList_ = listsManager.getGamesList();
	}

	public int getCount() {
		return gamesList_.size();
	}

	public Object getItem(int position) {
		return gamesList_.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(context_);
			convertView = inflater.inflate(R.layout.game, null);
		}
		/*Name*/
		TextView name = (TextView)convertView.findViewById(R.id.name);
		name.setText( gamesList_.get(position));

		/*Game*/
		//TextView game = (TextView)convertView.findViewById(R.id.game);
		//game.setText( friendsList_.get(position).game_ );

		/*Icon*/
		//ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
		//icon.setImageResource(R.drawable.blue);
		return(convertView);
	}
}
