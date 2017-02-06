package com.mygdx.ggjteamwave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by bthom on 1/21/2017.
 */

public class gameOverScreen implements Screen {
    final TeamWave game;
    OrthographicCamera camera;
    static Texture backgroundImageBlue = new Texture(Gdx.files.internal("gameoverimages/gameOverBlue.png"));
    static Texture backgroundImageGreen = new Texture(Gdx.files.internal("gameoverimages/gameOverGreen.png"));
    static Texture backgroundImageOrange = new Texture(Gdx.files.internal("gameoverimages/gameOverOrange.png"));
    static Texture backgroundImagePink = new Texture(Gdx.files.internal("gameoverimages/gameOverPink.png"));
    static Sprite backgroundBlue = new Sprite(backgroundImageBlue);
    static Sprite backgroundGreen = new Sprite(backgroundImageGreen);
    static Sprite backgroundOrange = new Sprite(backgroundImageOrange);
    static Sprite backgroundPink = new Sprite(backgroundImagePink);
    Sprite playerAmountSprite;
    Texture backgroundImage;
    Sprite background;
    float backgroundRoller;
    BitmapFont fontOne;
    int[] score;
    int remaining;
    int TEAMS;
    AssetManager assetManager;
    Sound menuChange;
    boolean loaded = false;
    boolean played = false;
    String playAgain = "Tap to wave once more! Top Left = 1 Player, TR = 2, BL = 3, BR = 4";
    Vector3 touchPos = new Vector3();

    public gameOverScreen(final TeamWave gam, int[] score, int remaining, int playerCount) {
        game = gam;
        camera = new OrthographicCamera();
        //camera.setToOrtho(false, 1280, 720);
        camera.setToOrtho(false, 1920, 1080);
        backgroundRoller = MathUtils.random();
        if (backgroundRoller <= 0.25f) {
            background = backgroundBlue;
        } else if (backgroundRoller > 0.25f & backgroundRoller <= 0.5f){
            background = backgroundGreen;
        } else if (backgroundRoller > 0.5f & backgroundRoller <= 0.75f){
            background = backgroundOrange;
        } else {
            background = backgroundPink;
        }
        this.score = score;
        this.remaining = remaining;
        fontOne = new BitmapFont();
        fontOne.setColor(Color.BLACK);
        fontOne.getData().setScale(3f);
        TEAMS = playerCount;
        assetManager = new AssetManager();
        assetManager.load("menuChange.mp3", Sound.class);
        assetManager.finishLoading();
    }

    @Override
    public void render(float delta) {
        if (loaded == false){
            loaded = startMusic();
        } else if (loaded == true & played == false){
            menuChange.play();
            played = true;
        }
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (score.length > 0) {
        	fontOne.setColor(0, 0, 1, 1);
            fontOne.draw(game.batch, "BLUE SCORE: " + this.score[0], Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/1.50f + 20);
        }
        if (score.length > 1) {
        	fontOne.setColor(1, 0, 0, 1);
            fontOne.draw(game.batch, "RED SCORE: " + this.score[1], Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/1.50f - 60);
        }
        if (score.length > 2) {
        	fontOne.setColor(0, 1, 0, 1);
            fontOne.draw(game.batch, "GREEN SCORE: " + this.score[2], Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/1.50f + 20);
        }
        if (score.length > 3) {
        	fontOne.setColor(1, 1, 0, 1);
            fontOne.draw(game.batch, "YELLOW SCORE: " + this.score[3], Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/1.50f - 60);
        }
        fontOne.setColor(1, 1, 1, 1);
        fontOne.draw(game.batch, playAgain, Gdx.graphics.getWidth()/5, Gdx.graphics.getHeight()/3.0f);
        game.batch.end();
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (touchPos.x > Gdx.graphics.getWidth() / 2f) {
                if (touchPos.y > Gdx.graphics.getHeight() / 2f) {
                    game.setScreen(new gameScreen(game, false, 2));
                    dispose();
                } else {
                    game.setScreen(new gameScreen(game, false, 4));
                    dispose();
                }
            }
            if (touchPos.x < Gdx.graphics.getWidth() / 2f) {
                if (touchPos.y > Gdx.graphics.getHeight() / 2f) {
                    game.setScreen(new gameScreen(game, false, 1));
                    dispose();
                } else {
                    game.setScreen(new gameScreen(game, false, 3));
                    dispose();
                }
            }
        }
    }

    public boolean startMusic() {
        if(assetManager.isLoaded("menuChange.mp3")) {
            menuChange = assetManager.get("menuChange.mp3", Sound.class);
            return true;
        }else {
            //System.out.println("not loaded yet");
            return false;
        }
    }

    @Override
    public void resize(int width, int height){
        background.setSize(width, height);
        if (height < 1000){
            playAgain = "Tap to wave once more! \n TopLeft=1 Player, TR=2, BL=3, BR=4";
        }
        camera.setToOrtho(false, width, height);
        //you can move it to whatever position you want here
        camera.update();
    }

    @Override
    public void show(){
    }

    @Override
    public void hide(){
    }

    @Override
    public void pause(){
    }

    @Override
    public void resume(){
    }

    @Override
    public void dispose(){
//        backgroundImage.dispose();
        fontOne.dispose();
        assetManager.dispose();
        menuChange.dispose();
    }
}
