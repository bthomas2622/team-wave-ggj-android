package com.mygdx.ggjteamwave;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TeamWave extends Game {
	public SpriteBatch batch;
	//public BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		//font = new BitmapFont(); //temp till create new FreeTypeFontGenerator
		this.setScreen(new gameScreen(this, true, 2));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		//font.dispose();
	}
}
