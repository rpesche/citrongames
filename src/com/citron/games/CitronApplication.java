package com.citron.games;

import android.os.Bundle;
import android.app.Application;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.String;

public class CitronApplication extends Application implements OnSharedPreferenceChangeListener {
	private String name_;
	private PhpConnection connection_;
	private ListsManager listsManager_;

    private SharedPreferences prefs;

    @Override
    public void onCreate(){
        super.onCreate();
			connection_ = new PhpConnection();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.prefs.registerOnSharedPreferenceChangeListener(this);
    }


	public void setName(String name){
		name_ = name;
	}

	public String getName(){
		return name_;
	}




	public void setListsManager(ListsManager listsManager){
		listsManager_ = listsManager;
	}

	public ListsManager getListsManager(){
		return listsManager_;
	}

    public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        //this.twitter = null;
    }


	public int getIntField(String game, int id, String field){
		return Integer.parseInt( connection_.getField(game, id, field));
	}

	public void setIntField(String game, int id, String field, int val) {
		connection_.setField(game, id, field, val);
	}
}
