package com.citron.games;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.util.Log;

public class RevengeActivity extends Activity implements OnClickListener {
	private final int MASTER = 0;
	private final int SLAVE = 1;

	private CitronApplication citronApp_;
	private PhpConnection connection_;
	private String name_, friendName_, game_;
	private int id_;
	private int role_;
	private RevengeView revengeView_;
	private Button chancesBtn;
	private ImageView icon_,friendIcon_;
	private TextView score_,friendScore_;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.revenge);

		friendName_ = (String) getIntent().getSerializableExtra("string");
		role_ = (Integer) getIntent().getSerializableExtra("string1");
		id_ = (Integer) getIntent().getSerializableExtra("string2");
		game_ = (String) getIntent().getSerializableExtra("string3");

		citronApp_ = ((CitronApplication) this.getApplication());
		connection_ = citronApp_.getConnection();
		name_ = citronApp_.getName();

		revengeView_ = (RevengeView)findViewById(R.id.graphicView);
		orientationManager();

		TextView nameView= (TextView)findViewById(R.id.name);
		TextView friendNameView= (TextView)findViewById(R.id.friendname);
		nameView.setText(name_);
		friendNameView.setText(friendName_);

		icon_ = (ImageView)findViewById(R.id.icon);
		friendIcon_ = (ImageView)findViewById(R.id.friendIcon);


		if(role_ == MASTER){
			icon_.setImageResource(R.drawable.greenplayer);
			friendIcon_.setImageResource(R.drawable.blueplayer_off);
		}
		else{
			icon_.setImageResource(R.drawable.greenplayer_off);
			friendIcon_.setImageResource(R.drawable.blueplayer);
		}

		score_ = (TextView)findViewById(R.id.points);
		friendScore_ = (TextView)findViewById(R.id.friendPoints);
		score_.setText("2");
		friendScore_.setText("2");
		chancesBtn = (Button) findViewById(R.id.chancesBtn);
		chancesBtn.setOnClickListener(this);

	}

	public void orientationManager(){
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) revengeView_.getLayoutParams();
		DisplayMetrics DM = getResources().getDisplayMetrics();

		//Landscape
		if(DM.widthPixels>DM.heightPixels) {
			params.height = DM.heightPixels ;
			params.width = DM.heightPixels ;
			revengeView_.setLayoutParams(params);
			revengeView_.init(DM.heightPixels, role_, citronApp_, id_);
        }
		//Portrait
        else {
			params.height = DM.widthPixels;
			params.width = DM.widthPixels;
			revengeView_.setLayoutParams(params);
			revengeView_.init(DM.widthPixels, role_, citronApp_, id_);
        }
	}

	@Override
	public void onClick(View v){
		if (v.getId()==R.id.chancesBtn)
			revengeView_.setChance(true);
	}

	@Override //appelé quand l'utilisateur appuie sur le bouton menu
	public boolean onCreateOptionsMenu(Menu menu){
	return true;
  }

   private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			String event = (String) intent.getSerializableExtra("event");
			score_.setText(""+revengeView_.getScore());
			friendScore_.setText(""+revengeView_.getFriendScore());

			if(event.equals("getToken")){
				icon_.setImageResource(R.drawable.greenplayer);
				friendIcon_.setImageResource(R.drawable.blueplayer_off);
			}
			else if(event.equals("leaveToken")){
				icon_.setImageResource(R.drawable.greenplayer_off);
				friendIcon_.setImageResource(R.drawable.blueplayer);
			}
			else if(event.equals("gameEnd")){
				String info = (String) intent.getSerializableExtra("info");
				endGame(info);
			}
        }
    };

	private void endGame(String info){
		Intent scoreScreen = new Intent(RevengeActivity.this, ScoreActivity.class);
		scoreScreen.putExtra("name", name_);
		scoreScreen.putExtra("friendName", friendName_);
		scoreScreen.putExtra("score", revengeView_.getScore());
		scoreScreen.putExtra("friendScore", revengeView_.getFriendScore());
		scoreScreen.putExtra("info", info);
		startActivity(scoreScreen);
		RevengeActivity.this.finish();
	}

	@Override
    protected void onDestroy(){
		revengeView_.abort();
		endGame("abort");
		super.onDestroy();
		Log.d("TAG2", "OnDestoy" );
	}
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("revenge");
        registerReceiver(receiver, filter);
        super.onResume();
		Log.d("TAG2", "OnResume" );
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
		Log.d("TAG2", "OnPause" );
    }


}

