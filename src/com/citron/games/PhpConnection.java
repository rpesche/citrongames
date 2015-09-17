package com.citron.games;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.ArrayList;
import android.util.Log;

/*	Les méthodes de cette classe sont:
 *	INSERT
 *	DELETE
 *	SET
 *	GET
*/

public class PhpConnection {

	//Roles
	public static final boolean	MASTER = false;
	public static final boolean	SLAVE	= true;
	/*Constantes*/
	private final long latenceMax = 20;
	private final long latenceRefresh=latenceMax/2;
	public static final String SERVER = "http://projetcitron.fr/Game/";

	/*Attributs de la classe*/
	private String name_;

	public PhpConnection(){
	}

	public static String get(String url) throws IOException{
		String source = "";
		URL oracle = new URL(SERVER+url);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(
		new InputStreamReader(
		yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			source +=inputLine;
		in.close();
		return source;
	}

	//#################################################################
	//#	Les Méthodes suivantes sont destinées à la PlateForme Citron	#
	//#	avant le lancement d'un jeu:																	#
	//#	-Gestion des listes d'utilisateur/de demandes de jeu(friends)	#
	//#	-Choix d'un partenaire et lancement d'une partie							#
	//#################################################################



	/*				INSERT USER				*/
	public boolean insertUser(String name){
		name_ = name;
		String res="";
		long date = (new Date().getTime())/1000;
		try{
			res = get("insertUser.php?name="+name+"&date="+date);
		}
		catch( Exception e){System.out.println("Error Server Connection");}
		if (res.equals("0")){
			System.out.println("Ce pseudo existe déja");
			return false;
		}
		return true;
	}


	/*				INSERT FRIEND				*/
	/* ajoute son propre nom dans la liste "friend" d'un friend */
	public void insertFriend(String other){
		try{
			get("insertFriend.php?name="+other+"&friend="+name_);
		}
		catch(Exception e){}
	}

	/*				INSERT GAME				*/
	/* créé une nouvelle partie du jeu "game"*/
	public int insertGame(String game){
		String sId=null;
		try{
			sId = get("insertGame.php?game="+game);
		}
		catch (Exception e) {}
		return Integer.parseInt(sId);
	}



	/*				GET USERSLIST					*/
	public ArrayList<String>	getUsersList(){
		String nameCopain;
		long date;
		String s="", token[];
		ArrayList<String> usersList = new ArrayList<String>();
		date = (new Date().getTime())/1000;
		try{
			s = get("getUsersList.php?name="+name_+"&date="+date+"&latenceMax="+latenceMax);
		}
		catch (Exception e) {}
		String[] tokens = s.split("[ ]");
		for (int i = 1; i < tokens.length; i++){
			if( !tokens[i].equals(name_) )
				usersList.add(tokens[i]);
		}
		return usersList;
	}


	/*				GET	FRIENDSLIST				*/
		public ArrayList<String> getFriendsList(){
			ArrayList<String> friendsList = new ArrayList<String>();
			String s = "";
			try{
				s = get("getFriendsList.php?name="+name_);
			}
			catch (Exception e) {}

			if(s.equals(""))
				return friendsList;
			s = s.replaceAll(" ","");
			String[] tokens = s.split("[,]+");
			for (int i = 0; i < tokens.length; i++)
				friendsList.add( tokens[i] );
			return friendsList;
	}



	/*				DELETE FRIEND				*/
	/* retire un nom de la liste "friend" d'un utilisateur*/
	public void deleteFriend(String user, String friend){
		ArrayList<String> friendsList = getFriendsList();
		String chain = "";
		for (int i=0; i<friendsList.size(); i++){
			if(friendsList.get(i).equals(friend)){
				friendsList.remove(i);
				i--;
			}
			else{
				chain+=friendsList.get(i);
				if(i<friendsList.size()-1)
					chain+=",";
			}
		}
		try{
			get("deleteFriend.php?name="+user+"&friend="+friend);
		}
		catch(Exception e){}
	}


	/*				SET GAME				*/
	/*	donne à son propre champ game le nom d'un jeu*/
	public void setGame(String game){
		try{
			get("setGame.php?name="+name_+"&game="+game);
		}
		catch(Exception e){}
	}

	/*				GET GAME				*/
	/*	Renvoi le champs game du friend		*/
	public String getGame(String friend){
		String s="";
		try{
			s = get("getGame.php?friend="+friend);
		}
		catch(Exception e){}
		return s;
	}

	/*				SET ID				*/
	/*	donne au champ id du friend l'id d'une partie*/
	public void setId(String friend, int id){
		try{
			get("setId.php?friend="+friend+"&id="+id);
		}
		catch(Exception e){}
	}


	/*				GET ID				*/
	/*	Renvoi son propre champ id		*/
	public int getId(String friend){
		String s="";
		try{
			s = get("getId.php?name="+friend);
		}
		catch(Exception e){}
		return Integer.parseInt(s);
	}

	/*				DELETE USER				*/
	/* supprime un joueur de la table "users*/
	public void deleteUser(String name){
		try{
			get("deleteUser.php?name="+name);
		}
		catch (Exception e) {}
	}


	/*				DELETE GAME				*/
	/* supprime une partie du jeu "game"*/
	public void deleteGame(String game, int id){
		try{
			get("deleteGame.php?game="+game+"&id="+id);
		}
		catch (Exception e) {}
	}

	//#################################################################
	//#	Les Méthodes suivantes sont destinées aux jeux en cours:			#
	//#	-Requete set d'un champ donné d'une partie donnée							#
	//#	-Requete get d'un champ donné d'une partie donnée							#
	//#################################################################


	/*				SET FIELD				*/
	/*	donne la valeur 'value' au champ 'field' de la partie 'id' du jeu 'game'*/
	public void setField(String game, int id, String field, int value){
		try{
			get("setField.php?game="+game+"&id="+id+"&field="+field+"&value="+value);
		}
		catch(Exception e){}
	}

	public void setField(String game, int id, String field, boolean value){
		try{
			get("setField.php?game="+game+"&id="+id+"&field="+field+"&value="+value);
		}
		catch(Exception e){}
	}

	/*				GET FIELD				*/
	/*	Renvoi la valeur du champ 'field' de la partie 'id' du jeu 'game'		*/
	public String getField(String game, int id, String field){
		String s="";
		try{
			s = get("getField.php?game="+game+"&id="+id+"&field="+field);
		}
		catch(Exception e){}
		return s;
	}









}
