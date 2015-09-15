package com.citron.games;

import android.app.Activity;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout; 
import android.util.Log;

import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;


public class UsersListActivity extends Activity implements OnClickListener{
	private Button usersBtn,gamesBtn,friendsBtn;

	private String name_;
	private PhpConnection connection_;
	private CitronApplication citronApp_;
	private ListView list_;
	private UserAdapter userAdapter_;
	private FriendAdapter friendAdapter_;	
	private int page_;// 0=users 1=games 2=friends
	private ListsManager listsManager_;

	private TimerTask timerTask;
	final Handler handler = new Handler();

	@Override	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("TAG1", "user OnCreate" );
		setContentView(R.layout.userslist);

		usersBtn = (Button) findViewById(R.id.usersListBtn);
		gamesBtn = (Button) findViewById(R.id.gamesListBtn);
		friendsBtn = (Button) findViewById(R.id.friendsListBtn);
		
		usersBtn.setOnClickListener(this);
		gamesBtn.setOnClickListener(this);
		friendsBtn.setOnClickListener(this);
		page_ = 0;


		citronApp_ = ((CitronApplication) this.getApplication());
		connection_ = citronApp_.getConnection();

		name_ = citronApp_.getName();

		TextView nameView = (TextView)findViewById(R.id.name);
		nameView.setText(name_);
		
		
		listsManager_ = new ListsManager(connection_, citronApp_);
		citronApp_.setListsManager(listsManager_);
		listsManager_.start();

		userAdapter_ = new UserAdapter(this, listsManager_);
		friendAdapter_ = new FriendAdapter(this, listsManager_);
        
		list_ = (ListView)findViewById(R.id.ListView01);

		list_.setAdapter(userAdapter_);
		list_.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
				selection(position);
			}
        });
	}


	private void selection(int position){
		String friend;
		int invit;
		if(page_ == 0){
			friend = listsManager_.getUsersList().get(position).name_;
			invit = listsManager_.getUsersList().get(position).friend_;
		}	
		else{
			friend = listsManager_.getFriendsList().get(position);
			invit = 1;
		}

		if(invit == 0){
			//Activité "inviter" avec param friend
			Intent toInvit = new Intent(UsersListActivity.this, ToInvitActivity.class);
			toInvit.putExtra("string", friend);
			startActivity(toInvit);
		}

		else{
			//Activité "voir invitation" avec param friend
			Intent beInvited = new Intent(UsersListActivity.this, BeInvitedActivity.class);
			beInvited.putExtra("string", friend);
			startActivity(beInvited);
		}
	}

	@Override	
    protected void onStart(){
		super.onStart();
		Log.d("TAG1", "user OnStart" );
	}

	@Override	
    protected void onRestart(){
		listsManager_.restart();
		super.onRestart();
		Log.d("TAG1", "user OnRestart" );
	}

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("listsManager");
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

	@Override	
    protected void onStop(){
		listsManager_.pause();
		super.onStop();
		Log.d("TAG1", "user OnStop" );
	}

	@Override	
    protected void onDestroy(){
	//	listsManager_.stop();
		super.onDestroy();
		Log.d("TAG1", "user OnDestoy" );
		connection_.deleteUser(name_);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			String event = (String) intent.getSerializableExtra("event");
			if(event.equals("refresh")){
				if(page_ == 0)
					userAdapter_.refresh();
				else if(page_ == 2)
					friendAdapter_.refresh();
			}
		}
	};

	 @Override
	public void onClick(View v){

		if (v.getId()==R.id.usersListBtn){
			page_ = 0;
			list_.setAdapter(userAdapter_);
			usersBtn.setBackgroundResource(R.drawable.pressed_button);
			gamesBtn.setBackgroundResource(R.drawable.button_background);
			friendsBtn.setBackgroundResource(R.drawable.button_background);
		}
		if (v.getId()==R.id.gamesListBtn){
			page_ = 1;
			list_.setAdapter(null);
			usersBtn.setBackgroundResource(R.drawable.button_background);
			gamesBtn.setBackgroundResource(R.drawable.pressed_button);
			friendsBtn.setBackgroundResource(R.drawable.button_background);
		}
		if (v.getId()==R.id.friendsListBtn){
			page_ = 2;
			list_.setAdapter(friendAdapter_);
			usersBtn.setBackgroundResource(R.drawable.button_background);
			gamesBtn.setBackgroundResource(R.drawable.button_background);
			friendsBtn.setBackgroundResource(R.drawable.pressed_button);
		}
	}

	@Override //appelé quand l'utilisateur appuie sur le bouton menu
	public boolean onCreateOptionsMenu(Menu menu){
/*		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		*/
	return true;
  }
}

