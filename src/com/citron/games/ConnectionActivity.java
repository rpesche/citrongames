package com.citron.games;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.os.Bundle;

/*Cette activité est appellée au lancement de l'appli
 * Elle correspond à l'écran de connection*/

public class ConnectionActivity extends Activity implements TextWatcher, OnClickListener{
	private EditText editText;
	private Button updateBtn;
	private TextView textCount;

	/*PhpConnection_ offre des méthodes pour communiquer avec le serveur*/
	private PhpConnection connection_;
	private CitronApplication citronApp_;

	/*motif_ signal les caractères autorisés dans le pseudo*/
	final private String motif_= "[a-zA-Z0-9éè]*";

  /** Called when the activity is first created. */
  @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);
		editText = (EditText) findViewById(R.id.editPseudo);
		editText.requestFocus();
		editText.addTextChangedListener(this);
		updateBtn = (Button) findViewById(R.id.okBtn);
		updateBtn.setOnClickListener(this);

		citronApp_ = ((CitronApplication) this.getApplication());
		connection_ = citronApp_.getConnection();
	}

	/*@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN &&	event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		}
	}*/

  @Override
  public void onClick(View v){
		String name = editText.getText().toString();
		if( name.equals("") )
			Toast.makeText(citronApp_,"Choisissez un pseudo" , Toast.LENGTH_SHORT).show();
		else if( !name.matches(motif_) ){
			Toast.makeText(citronApp_,"Pseudo invalide" , Toast.LENGTH_SHORT).show();
		}
		else{
			if(connection_.insertUser(name) ){
				citronApp_.setName(name);

				Intent usersListActivity = new Intent(ConnectionActivity.this, UsersListActivity.class);
				startActivity(usersListActivity);
			}
			else{
				Toast.makeText(citronApp_, name+" est déja connecté" , Toast.LENGTH_SHORT).show();
				editText.setText("");
			}
		}
	}


	public void afterTextChanged(Editable statusText){
  }

	public void beforeTextChanged(CharSequence charSeq, int a, int b, int c){
	}

	public void onTextChanged(CharSequence charSeq, int a, int b, int c){
	}

	@Override //appelé quand l'utilisateur appuie sur le bouton menu
	public boolean onCreateOptionsMenu(Menu menu){
	//	MenuInflater inflater = getMenuInflater();
	//	inflater.inflate(R.menu.menu, menu);
    return true;
  }
}
