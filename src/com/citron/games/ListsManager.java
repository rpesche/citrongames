package com.citron.games;
import java.util.ArrayList;
import android.util.Log;
import android.content.Intent;

/*ListManager garde à jour et met à disposition les listes d'invitations (friends) et d'utilisateurs (users)
 * Elle existe aujourd'hui sous forme de thread mais va probablement devenir un service */


public class ListsManager extends Thread {

		/*Constantes*/
	private final long latenceMax = 2;
	private final long latenceRefresh=latenceMax/2;

	/*Attributs de la classe*/
	private ArrayList<String> oldUsersList_ = new ArrayList<String>();
	private ArrayList<String> usersList_ = new ArrayList<String>();
	private ArrayList<String> oldFriendsList_ = new ArrayList<String>();
	private ArrayList<String> friendsList_ = new ArrayList<String>();
	private ArrayList<String> gamesList_ = new ArrayList<String>();
	private PhpConnection connection_;
	private CitronApplication citronApp_;
	private boolean run_;
	private Intent intent;


	public ListsManager(PhpConnection connection, CitronApplication citronApp){
		connection_ = connection;
 		citronApp_ = citronApp;

		intent = new Intent("listsManager");
		intent.putExtra("event", "refresh");
		initialiseGamesList();
		run_ = true;
	}

	public void run(){
		while(true){
			if(run_){
				Log.i("TAG1", "run");
				updateUsersList();
				updateFriendsList();
				if( !oldUsersList_.equals(usersList_) || !oldFriendsList_.equals(friendsList_)){
					citronApp_.sendBroadcast(intent);
				}
				try{
					this.sleep(latenceRefresh*1000);	
				}
				catch (Exception e) {}		
			}
		}
	}

	/*		INITIALISE GAMES LIST	*/
	private void initialiseGamesList(){
		gamesList_.add(0,"revenge");
	}

	/*		UPDATE USERS LIST		*/	
	private void updateUsersList(){
		synchronized(usersList_){
			oldUsersList_ = usersList_;
			usersList_ = connection_.getUsersList();
		}
	}

	/*		UPDATE FRIEND LIST		*/	
	private void updateFriendsList(){
		synchronized(friendsList_){
			oldFriendsList_ = friendsList_;
			friendsList_ = connection_.getFriendsList();
		}
	}

	/*		GET USERS LIST		*/
	public synchronized ArrayList<User> getUsersList(){
		ArrayList<User> newList = new ArrayList<User>();

		for(int j=0; j<usersList_.size(); j++){
			newList.add(j, new User(usersList_.get(j)));

			for(int i=0; i<friendsList_.size(); i++){
				if( friendsList_.get(i).equals(usersList_.get(j))){
					newList.set(j, new User(usersList_.get(j), 1));
					break;
				}
			}
		}
		return newList;
	}

	/*		GET FRIENDS LIST		*/
	public synchronized ArrayList<String> getFriendsList(){
		ArrayList<Friend> newList = new ArrayList<Friend>();

		synchronized(friendsList_){
			return friendsList_;
		}
	}

	public ArrayList<String> getGamesList(){
		return gamesList_;
	}

	public void pause(){
		Log.d("TAG1", "pause" );
		run_=false;
	}

	public void restart(){

		Log.d("TAG1", "restart" );
		run_=true;
	}




}
