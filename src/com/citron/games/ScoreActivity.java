package com.citron.games;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;


public class ScoreActivity extends Activity{
	private CitronApplication citronApp_;
	private String name_, friendName_, info_;
	private int score_, friendScore_;

	@Override	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		name_ = (String) getIntent().getSerializableExtra("name");
		friendName_ = (String) getIntent().getSerializableExtra("friendName");
		score_ = (Integer) getIntent().getSerializableExtra("score");
		friendScore_ = (Integer) getIntent().getSerializableExtra("friendScore");
		info_ = (String) getIntent().getSerializableExtra("info");
		setContentView(R.layout.score);
		
		TextView scoreText = (TextView)findViewById(R.id.scoreText);
		String text;

		if( info_.equals("aborted"))
			text = "YOU WIN\n\n"+friendName_+" aborted the game.\nHe was affraid.";
		else if ( info_.equals("abort"))
			text = "YOU LOOSE\n\nYou aborted the game.\nSuch a bad boy.";

		else if (score_ > friendScore_)
			text = "YOU WIN!\n\nYou get "+score_+" points\n"+friendName_+" get "+friendScore_+" points\n\nYou're really da best =D";
		else if (friendScore_ > score_)	
			text = "YOU LOOSE\n\nYou get "+score_+" points\n"+friendName_+" get "+friendScore_+" points\n\nI don't know what to say :/";
		else
			text = "EX AEQUO!\n\nYou both get "+score_+" points\n\nWhat a battle! =o";

		scoreText.setText(text);
	}

	@Override //appelé quand l'utilisateur appuie sur le bouton menu
	public boolean onCreateOptionsMenu(Menu menu){
	return true;
  }
}

