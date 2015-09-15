package com.citron.games;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;


/*Cette activité est appellée quand on séléctionne une invitation. 
 * Elle permet d'accepter, de refuser ou d'ignorer (bouton retour) l'invitation
 */

public class BeInvitedActivity extends Activity implements OnClickListener{
	private final int ABORT = -2;
	private final int WAITANSWER = -1;
	private final int SLAVE = 1;

	private Button acceptBtn,refuseBtn;
	private PhpConnection connection_;
	private CitronApplication citronApp_;
	private String name_, friendName_, game_;

	@Override	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		friendName_ = (String) getIntent().getSerializableExtra("string");
		setContentView(R.layout.beinvited);

		citronApp_ = ((CitronApplication) this.getApplication());
		connection_ = citronApp_.getConnection();
		name_ = citronApp_.getName();
		game_ = connection_.getGame(friendName_);
		acceptBtn = (Button) findViewById(R.id.acceptBtn);
		refuseBtn = (Button) findViewById(R.id.refuseBtn);
		acceptBtn.setOnClickListener(this);
		refuseBtn.setOnClickListener(this);

		TextView friendView = (TextView)findViewById(R.id.friend);
		friendView.setText(friendName_+"\nvous propose de jouer à\n"+game_); 
        

	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.acceptBtn){//Accepter
			int id = connection_.getId(friendName_);
			if(id == ABORT){//aborteda
				Toast.makeText(citronApp_,"Game Aborted" , Toast.LENGTH_LONG).show();
				BeInvitedActivity.this.finish();
			}
			else{			
				id	= connection_.insertGame(game_);
				connection_.setId(friendName_,id);
				connection_.deleteFriend(name_,friendName_);
				//if( game_.equals("revenge") )
					Intent inGame = new Intent(BeInvitedActivity.this, RevengeActivity.class);

				inGame.putExtra("string", friendName_);
				inGame.putExtra("string1", SLAVE);
				inGame.putExtra("string2", id);
				inGame.putExtra("string3", game_);
				startActivity(inGame);

				BeInvitedActivity.this.finish();
			}
		}
		else if(v.getId() == R.id.refuseBtn){//Refuser
			connection_.setId(friendName_, ABORT);
			connection_.deleteFriend(name_,friendName_);
			BeInvitedActivity.this.finish();
		}
	}

	@Override //appelé quand l'utilisateur appuie sur le bouton menu
	public boolean onCreateOptionsMenu(Menu menu){
	return true;
  }
}

