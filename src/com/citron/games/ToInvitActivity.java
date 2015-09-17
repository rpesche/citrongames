package com.citron.games;

//import android.app.ListActivity;
import android.app.Activity;
import android.content.Intent;
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


public class ToInvitActivity extends Activity implements OnClickListener{
	private final int ABORT = -2;
	private final int WAITANSWER = -1;
	private final int MASTER = 0;

	private Button abortBtn;

	private String name_, friendName_, game_;
	private PhpConnection connection_;
	private CitronApplication citronApp_;
	private ListView list_;
	private GameAdapter gameAdapter_;
	private int page_;// 0=users 1=games 2=friends
	private ListsManager listsManager_;
	private int sentFlag_ = 0;

	private Timer timer_;
	private TimerTask timerTask_;
	final Handler handler = new Handler();
	private boolean run_= true;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toinvit);
		findViewById(R.id.waiting).setVisibility(View.INVISIBLE);

		friendName_ = (String) getIntent().getSerializableExtra("string");


		citronApp_ = ((CitronApplication) this.getApplication());
		connection_ = citronApp_.getConnection();

		name_ = citronApp_.getName();

		TextView friendView = (TextView)findViewById(R.id.friend);
		friendView.setText("Proposez une partie\nà\n"+friendName_);

		abortBtn = (Button) findViewById(R.id.abortBtn);
		abortBtn.setOnClickListener(this);

		listsManager_ = citronApp_.getListsManager();
		gameAdapter_ = new GameAdapter(this, listsManager_);
		list_ = (ListView)findViewById(R.id.ListView01);
		list_.setAdapter(gameAdapter_);

		list_.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selection(position);
			}
        });
	}

	private void selection(int position){
		sentFlag_ = 1;
		game_ = listsManager_.getGamesList().get(position);
		findViewById(R.id.waiting).setVisibility(View.VISIBLE);

		connection_.insertFriend( friendName_);
		connection_.setGame(game_);
		connection_.setId( name_, WAITANSWER ); //WAITANSWER

		timer_ = new Timer();
        initializeTimerTask();
        timer_.schedule(timerTask_, 0, 500);

	}

	public void initializeTimerTask() {
        timerTask_ = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						int id;
						id = connection_.getId(name_);
						if(id == ABORT ){// ABORTED
							timer_.cancel();
							timer_.purge();
							Toast.makeText(citronApp_,"Game Refused" , Toast.LENGTH_LONG).show();
							ToInvitActivity.this.finish();
						}
						else if(id != WAITANSWER && run_){
							run_= false;
							timer_.cancel();
							timer_.purge();
							//if( game_.equals("revenge") )
								Intent inGame = new Intent(ToInvitActivity.this, RevengeActivity.class);
							inGame.putExtra("string", friendName_);
							inGame.putExtra("string1", MASTER);
							inGame.putExtra("string2", id);
							inGame.putExtra("string3", game_);
							startActivity(inGame);
							ToInvitActivity.this.finish();
						}
					}
				});
			}
		};
	}

	 @Override
	public void onClick(View v){
		if (v.getId() == R.id.abortBtn){//Abort
			if(sentFlag_ == 0){
				ToInvitActivity.this.finish();
			}
			else if (sentFlag_ == 1){
				connection_.setId(name_, ABORT );
				connection_.deleteFriend(friendName_, name_);
				timer_.cancel();
				timer_.purge();
				ToInvitActivity.this.finish();
			}
		}
	}

	@Override //appelé quand l'utilisateur appuie sur le bouton menu
	public boolean onCreateOptionsMenu(Menu menu){
	return true;
  }
}
