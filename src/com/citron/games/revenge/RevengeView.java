package com.citron.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;
import android.content.Intent;

public class RevengeView extends View {

	private final int MASTER = 0;
	private final int SLAVE = 1;
	private final int EMPTY = -1;


	private final int INIT = -1;
	private final int CANT_PLAY = -2;
	private final int ABORT = -3;
	private	final int mapSize_ = 8;
	int countdown_ = mapSize_*mapSize_ -4;


	private final String game_ = "revenge";
	private int role_, foe_;
	private int id_;
	private int token_;
	private Paint paint;
	private int screenSize_;
	private int cellSize_;
	private Bitmap	me_, friend_, empty_, chance_;
	private int map_[][] ;
	private ArrayList<Position> chances_ = new ArrayList<Position>();
	private CitronApplication citronApp_;
	private PhpConnection connection_;
	private Position position_;
	private boolean chanceFlag = false;
	private boolean waiting_=true;
	private boolean run_ = true;


	private int score_;
	private int friendScore_;
	private Intent intent = new Intent("revenge");

   // Constructor
   public RevengeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		me_	= BitmapFactory.decodeResource(getResources(), R.drawable.greenplayer);
		friend_	= BitmapFactory.decodeResource(getResources(), R.drawable.blueplayer);
		empty_	= BitmapFactory.decodeResource(getResources(), R.drawable.empty);
		chance_	= BitmapFactory.decodeResource(getResources(), R.drawable.chance);
		map_ = newMap();
   }

	public void init(int screenSize, int role, CitronApplication citronApp, int id ){
		screenSize_ = screenSize;
		cellSize_ = screenSize/mapSize_;
		role_ = role;
		citronApp_=citronApp;
		connection_ = citronApp_.getConnection();
		id_ = id;
		if( role_ == MASTER ){
			double ran=Math.random();
			if(ran<0.5)
				token_= SLAVE;
			else
				token_= MASTER;
			foe_ = SLAVE;
		}
		else
			foe_ = MASTER;
	}

   @Override
	protected void onDraw(Canvas canvas) {
		if(!run_)
			return;
		checkToken();
		drawMap(canvas);
		drawChances(canvas);
		invalidate();
   }


	private void gameEnd(String info){
		intent.putExtra("event", "gameEnd");
		intent.putExtra("info", info);
		citronApp_.sendBroadcast(intent);
	}

	private void giveToken(int x, int y){
		token_ = foe_;
		setPosition(x, y);	////////////
		setToken();			//Mutualiser
		intent.putExtra("event", "leaveToken");
		citronApp_.sendBroadcast(intent);
	}

	public void abort(){
		token_ = ABORT;
		setToken();
		run_ = false;
	}

	private void takeToken(){
		intent.putExtra("event","getToken");
		citronApp_.sendBroadcast(intent);
	}

	private void checkToken(){
		if(countdown_ == 0){
			gameEnd("finished");
		}
		token_ = getToken();

		if( token_ == ABORT ){
			gameEnd("aborted");
			return;
		}

		//waiting for token
		if(waiting_){
			//I just get the token
			if(token_ == role_){
				Intent intent;
				position_ = getPosition();

				//friend can't play
				if(position_.x_ == CANT_PLAY ){
					//I couldn't play
					if( chances_.size() == 0){
						giveToken( CANT_PLAY, CANT_PLAY);
						gameEnd("blocked");// END OF GAME
					}
				}

				//I'm not the first to play
				else if (position_.x_ != INIT ){
					apply( position_.x_,position_.y_, foe_);
				}


				updateScore();
				chancesCheck(role_);

				//I can't play
				if( chances_.size() == 0){
					giveToken( CANT_PLAY, CANT_PLAY);
				}
				//I can play
				else{
					takeToken();
					waiting_=false;
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if( waiting_ )
			return false;

		int x = (int)event.getX()/cellSize_;
		int y = (int)event.getY()/cellSize_;
		if(apply(x, y, role_)){
			setChance(false);
			giveToken(x, y);
			updateScore();
			waiting_= true;
		}
		return false;
	}


	private int[][] newMap(){
		int map[][] = new int[mapSize_][mapSize_];
		for (int y = 0; y < mapSize_; y++)
			for (int x = 0; x < mapSize_; x++)
				map[x][y] = EMPTY;
		map[mapSize_/2-1][mapSize_/2-1] = MASTER;
		map[mapSize_/2][mapSize_/2] = MASTER;
		map[mapSize_/2][mapSize_/2-1] = SLAVE;
		map[mapSize_/2-1][mapSize_/2] = SLAVE;
		return map;
	}

	private void drawMap(Canvas canvas){
		RectF rect = new RectF();
		for (int y = 0; y < mapSize_; y++){
			for (int x = 0; x < mapSize_; x++){
				rect.set( x*cellSize_ , y*cellSize_ , x*cellSize_ + cellSize_, y*cellSize_ +cellSize_);
				canvas.drawBitmap(empty_, null, rect, paint);
				if(map_[x][y] == role_)
					canvas.drawBitmap(me_, null, rect, paint);
				else if (map_[x][y] == foe_)
					canvas.drawBitmap(friend_, null, rect, paint);
			}
		}
	}

	private void updateScore(){
		score_=0;
		friendScore_=0;
		for (int y = 0; y < mapSize_; y++){
			for (int x = 0; x < mapSize_; x++){
				if(map_[x][y] == role_)
					score_++;
				else if( map_[x][y] == foe_)
					friendScore_++;
			}
		}
	}

	private void drawChances(Canvas canvas){
		if(!chanceFlag)
			return;

		RectF rect = new RectF();
		for(int i=0; i<chances_.size();i++){
			rect.set( chances_.get(i).x_*cellSize_ , chances_.get(i).y_*cellSize_ ,
					chances_.get(i).x_*cellSize_ + cellSize_, chances_.get(i).y_*cellSize_ +cellSize_);
			canvas.drawBitmap(chance_, null, rect, paint);
		}
	}

	public int getScore(){
		return score_;
	}

	public int getFriendScore(){
		return friendScore_;
	}


	public void setChance(boolean bool){
		if( token_ == role_)
			chanceFlag = bool;
	}




	private Position getPosition(){
		int x = citronApp_.getIntField(game_, id_, "x");
		int y = citronApp_.getIntField(game_, id_, "y");
		return new Position(x, y);
	}

	private void setPosition(int x, int y){
		citronApp_.setIntField(game_, id_, "x", x);
		citronApp_.setIntField(game_, id_, "y", y);
	}

	private int getToken(){
		return citronApp_.getIntField(game_, id_, "token");
		}

	private void setToken(){
		citronApp_.setIntField(game_, id_, "token", token_);
	}


/*
   @Override
   public void onSizeChanged(int w, int h, int oldW, int oldH) {
   }
*/

   /* Chances Check */
   	private int chancesCheck(int player){
		boolean hit;
		int count = 0;
		int foe;
		if(player == MASTER)
			foe=SLAVE;
		else
			foe=MASTER;
		chances_.clear();
		for(int x=0; x<mapSize_; x++){
			for(int y=0; y<mapSize_; y++){

				if(map_[x][y] == EMPTY){
				hit = false;

				//Right
					if( x < mapSize_-1 && map_[x+1][y] == foe ){
						for(int i=x; i<=mapSize_-1; i++){
							if(map_[i][y] == player){
								hit = true;
								break;
							}
							else if(map_[i][y] == EMPTY && i!=x)
								break;
						}
					}
					//Left
					if( x > 0 && map_[x-1][y] == foe){
						for(int i=x; i>=0; i--){
							if(map_[i][y] == player){
								hit = true;
								break;
							}
							else if(map_[i][y] == EMPTY && i!=x)
								break;

						}
					}
					//Down
					if( y < mapSize_-1 && map_[x][y+1] == foe){
						for(int j=y; j<=mapSize_-1; j++){
							if(map_[x][j] == player){
								hit = true;
								break;
							}
							else if(map_[x][j] == EMPTY && j!=y)
								break;

						}
					}
					//Top
					if( y > 0 && map_[x][y-1] == foe){
						for(int j=y; j>=0; j--){
							if(map_[x][j] == player){
								hit = true;
								break;
							}
							else if(map_[x][j] == EMPTY &&j!=y)
								break;
						}
					}
					//Right Top
					if( x < mapSize_-1 && y > 0 && map_[x+1][y-1] == foe){
						for(int i=x, j=y; i<=mapSize_-1 && j>=0 ; i++,j--){
							if(map_[i][j] == player){
								hit = true;
								break;
							}
							else if(map_[i][j] == EMPTY && i!=x)
								break;

						}
					}
					//Right Down
					if( x < mapSize_-1 && y <mapSize_-1 && map_[x+1][y+1] == foe){
						for(int i=x, j=y; i<=mapSize_-1 && j<=mapSize_-1 ; i++,j++){
							if(map_[i][j] == player){
								hit = true;
								break;
							}
							else if(map_[i][j] == EMPTY && i!=x)
								break;

						}
					}
					//Left Top
					if( x > 0 && y > 0 && map_[x-1][y-1] == foe){
						for(int i=x, j=y; i>=0 && j>=0 ; i--,j--){
							if(map_[i][j] == player){
								hit = true;
								break;
							}
							else if(map_[i][j] == EMPTY && i!=x)
								break;

						}
					}
					//Left Down
					if( x > 0 && y < mapSize_-1 && map_[x-1][y+1] == foe){
						for(int i=x, j=y; i>=0 && j<=mapSize_-1 ; i--,j++){
							if(map_[i][j] == player){
								hit = true;
								break;
							}
							else if(map_[i][j] == EMPTY &&i!=x)
								break;
						}
					}
					if(hit){
						count++;
						chances_.add(new Position(x,y));
					}
				}
			}
		}
		return count;
	}



	/* Apply the choice */
	private boolean apply(int x, int y, int player){
		boolean hit = false;
		int foe;
		if(player == MASTER)
			foe=SLAVE;
		else
			foe=MASTER;

		if(map_[x][y] != EMPTY)
			return false;
		//Right
		if( x < mapSize_-1 && map_[x+1][y] == foe ){
			for(int i=x; i<=mapSize_-1; i++){
				if(map_[i][y] == player){
					for(;i>x;i--)
						map_[i][y]=player;
					hit = true;
					break;
				}
				else if(map_[i][y] == EMPTY && i!=x)
					break;
			}
		}
		//Left
		if( x > 0 && map_[x-1][y] == foe){
			for(int i=x; i>=0; i--){
				if(map_[i][y] == player){
					for(;i<x;i++)
						map_[i][y]=player;
					hit = true;
					break;
				}
				else if(map_[i][y] == EMPTY && i!=x)
					break;
			}
		}

		//Down
		if( y < mapSize_-1 && map_[x][y+1] == foe){
			for(int j=y; j<=mapSize_-1; j++){
				if(map_[x][j] == player){
					for(;j>y;j--)
						map_[x][j]=player;
					hit = true;
					break;
				}
				else if(map_[x][j] == EMPTY && j!=y)
					break;
			}
		}
		//Top
		if( y > 0 && map_[x][y-1] == foe){
			for(int j=y; j>=0; j--){
				if(map_[x][j] == player){
					for(;j<y;j++)
						map_[x][j]=player;
					hit = true;
					break;
				}
				else if(map_[x][j] == EMPTY && j!=y)
					break;
			}
		}
		//Right Top
		if( x < mapSize_-1 && y > 0 && map_[x+1][y-1] == foe){
			for(int i=x, j=y; i<=mapSize_-1 && j>=0 ; i++,j--){
				if(map_[i][j] == player){
					for(;i>x ;i--,j++)
						map_[i][j]=player;
					hit = true;
					break;
				}
				else if(map_[i][j] == EMPTY && i!=x)
					break;
			}
		}
		//Right Down
		if( x < mapSize_-1 && y <mapSize_-1 && map_[x+1][y+1] == foe){
			for(int i=x, j=y; i<=mapSize_-1 && j<=mapSize_-1 ; i++,j++){
				if(map_[i][j] == player){
					for(;i>x ;i--,j--)
						map_[i][j]=player;
					hit = true;
					break;
				}
				else if(map_[i][j] == EMPTY && i!=x)
					break;
			}
		}
		//Left Top
		if( x > 0 && y > 0 && map_[x-1][y-1] == foe){
			for(int i=x, j=y; i>=0 && j>=0 ; i--,j--){
				if(map_[i][j] == player){
					for(;i<x ;i++,j++)
						map_[i][j]=player;
					hit = true;
					break;
				}
				else if(map_[i][j] == EMPTY && i!=x)
					break;
			}
		}
		//Left Down
		if( x > 0 && y < mapSize_-1 && map_[x-1][y+1] == foe){
			for(int i=x, j=y; i>=0 && j<=mapSize_-1 ; i--,j++){
				if(map_[i][j] == player){
					for(;i<x ;i++,j--)
						map_[i][j]=player;
					hit = true;
					break;
				}
				else if(map_[i][j] == EMPTY && i!=x)
					break;
			}
		}
		if( hit ){
			map_[x][y] = player;
			countdown_ --;
		}
		return hit;
	}


}
