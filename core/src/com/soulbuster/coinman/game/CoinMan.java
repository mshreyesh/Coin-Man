package com.soulbuster.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;
	Texture[] caracter;

	int charstate=0;
	int pause =0;
	float gravity =0.2f;
	float velocity =0;
	int caracterY = 0;
	Rectangle carShape;
	Texture coin;
	ArrayList<Integer> coinxaxis = new ArrayList<Integer>();
	ArrayList<Integer> coinyaxis = new ArrayList<Integer>();
	Random random;
	int coinCount=0;

	Texture bomb;
	ArrayList<Integer> bombxaxis = new ArrayList<Integer>();
	ArrayList<Integer> bombyaxis = new ArrayList<Integer>();
	int bombCount=0;

	int score = 0;
	BitmapFont font;
	int gamestate=0;

	Texture gameend;

	ArrayList<Rectangle> coinShape = new ArrayList<Rectangle>();
	ArrayList<Rectangle> bombShape = new ArrayList<Rectangle>();

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		caracter = new Texture[4];
		caracter[0] = new Texture("frame-1.png");
		caracter[1] = new Texture("frame-2.png");
		caracter[2] = new Texture("frame-3.png");
		caracter[3] = new Texture("frame-4.png");
		caracterY = Gdx.graphics.getHeight() / 2;

		gameend = new Texture("dizzy-1.png");

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.FIREBRICK);
		font.getData().scale(11);
	}

	public void generateCoins() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinyaxis.add((int) height);
		coinxaxis.add(Gdx.graphics.getWidth());
	}

	public void generateBombs() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombyaxis.add((int) height);
		bombxaxis.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gamestate == 0) {
			if(Gdx.input.justTouched())
				gamestate=1;
		} else if(gamestate == 1) {
			//bomb
			if(bombCount<250)
				bombCount++;
			else {
				bombCount = 0;
				generateBombs();
			}

			bombShape.clear();
			for(int i = 0; i < bombxaxis.size(); i++) {
				batch.draw(bomb,bombxaxis.get(i),bombyaxis.get(i));
				bombxaxis.set(i,bombxaxis.get(i)-8);
				bombShape.add(new Rectangle(bombxaxis.get(i),bombyaxis.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//coin
			if(coinCount<100)
				coinCount++;
			else {
				coinCount = 0;
				generateCoins();
			}

			coinShape.clear();
			for(int i = 0; i < coinxaxis.size(); i++) {
				batch.draw(coin,coinxaxis.get(i),coinyaxis.get(i));
				coinxaxis.set(i,coinxaxis.get(i)-4);
				coinShape.add(new Rectangle(coinxaxis.get(i),coinyaxis.get(i),coin.getWidth(),coin.getHeight()));
			}

			if(Gdx.input.justTouched())
				velocity = -15;

			if(pause<8)
				pause++;
			else{
				pause=0;
				if(charstate<3)
					charstate++;
				else
					charstate=0;
			}

			velocity += gravity;
			caracterY -= velocity;

			if(caracterY<5)
				caracterY=5;
			if(caracterY>Gdx.graphics.getHeight()-400)
				caracterY=Gdx.graphics.getHeight()-400;


		} else if(gamestate == 2) {

			if(Gdx.input.justTouched())
				gamestate = 1;
			caracterY = Gdx.graphics.getHeight() / 2;
			score = 0;
			velocity = 0;
			coinxaxis.clear();
			coinyaxis.clear();
			coinShape.clear();
			coinCount = 0;
			bombxaxis.clear();
			bombyaxis.clear();
			bombShape.clear();
			bombCount = 0;

		}

		if(gamestate == 2)
			batch.draw(gameend,Gdx.graphics.getWidth()/2 - caracter[charstate].getWidth()/2,caracterY);
		else
			batch.draw(caracter[charstate],Gdx.graphics.getWidth()/2 - caracter[charstate].getWidth()/2,caracterY);
		carShape = new Rectangle(Gdx.graphics.getWidth()/2 - caracter[charstate].getWidth()/2,caracterY,caracter[charstate].getWidth(),caracter[charstate].getHeight());
		//coin collision
		for(int i = 0; i < coinShape.size();i++) {
			if(Intersector.overlaps(carShape,coinShape.get(i))) {
				score++;
				coinShape.remove(i);
				coinxaxis.remove(i);
				coinyaxis.remove(i);
				break;
			}
		}
		//bomb collision
		for(int i = 0; i < bombShape.size();i++) {
			if(Intersector.overlaps(carShape,bombShape.get(i))) {

				gamestate =2;
			}
		}

		font.draw(batch, String.valueOf(score),100,200);

		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
