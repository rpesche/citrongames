package com.citron.games;

public class User {

	/*Attributs de la classe*/
	public String name_;
	public int	friend_;

	public User(String name, int friend){
		name_ = name;
		friend_ = friend;
	}

	public User(String name){
		name_  = name;
		friend_ = 0;
	}

}
