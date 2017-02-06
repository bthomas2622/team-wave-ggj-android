package com.mygdx.ggjteamwave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bthom on 1/20/2017.
 */

public class gameScreen implements Screen {
    final static float PIXELS_TO_METERS = 100f;
    final TeamWave game;
    Map map;
    Array<Mob> mobs;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    Array<Sprite> buildings;
    boolean menuScreen;
    static Texture pressSpace = new Texture(Gdx.files.internal("pressSpaceSplash.png"));
    Sprite pressSpaceSprite;
    static Texture playerAmount = new Texture(Gdx.files.internal("playerCount.png"));
    Sprite playerAmountSprite;
    static Texture bluePlayer = new Texture(Gdx.files.internal("blueOne.png"));
    static Texture redPlayer = new Texture(Gdx.files.internal("redTwo.png"));
    static Texture greenPlayer = new Texture(Gdx.files.internal("greenThree.png"));
    static Texture yellowPlayer = new Texture(Gdx.files.internal("yellowFour.png"));
    Sprite playerTurn;

    int TEAMS = 2;
    int[] teamScores;
    int[] teamRemaining;
    int teamTurn;
    boolean updateTeamTurn;
    boolean shouldGameOver;
    
    AssetManager assetManager;
    Music backgroundMusic;
    Sound waveSound;
    //Sound menuChange;
    boolean loaded = false;
    Viewport viewport;
    float timer;

    Vector3 touchPos = new Vector3();

    public gameScreen(final TeamWave gam, boolean isMainMenu, int playerCount) {
        game = gam;
        map = new Map(this);
        mobs = new Array<Mob>();
        buildings = new Array<Sprite>();
        menuScreen = isMainMenu;
        TEAMS = playerCount;
        playerTurn = new Sprite(bluePlayer);

        map.generate();
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        if (!menuScreen){
            camera.zoom = 5;
        } else {
            pressSpaceSprite = new Sprite(pressSpace);
            pressSpaceSprite.setPosition(0f, 0f);
            playerAmountSprite = new Sprite(playerAmount);
            playerAmountSprite.setPosition(0f, 0f);
            camera.zoom = 1;
        }
        teamScores = new int[TEAMS];
        teamRemaining = new int[TEAMS];
        teamTurn = 1;

        assetManager = new AssetManager();
        assetManager.load("backgroundMusic.mp3", Music.class);
        assetManager.load("wave.mp3", Sound.class);
        //assetManager.load("menuChange.mp3", Sound.class);
        assetManager.finishLoading();
        
        viewport = new FitViewport(1920, 1080, camera);
        shouldGameOver = false;
    }
    
    public void useTeamTurn() {
    	teamTurn++;
    	
    	if (teamTurn > TEAMS) {
    		teamTurn = 1;
    	}
    	int count = 0;
    	while (teamRemaining[teamTurn-1] <= 0) {
    		teamTurn++;
    		if (teamTurn > TEAMS) {
        		teamTurn = 1;
        	}
    		
    		if (count > TEAMS) {
    			shouldGameOver = true;
    			break;
    		}
    		count++;
    	}
    	updateTeamTurn = false; 
    }
    
    public void updateTeamScores() {
    	for (int x = 0; x < TEAMS; x++) {
    		teamScores[x] = 0;
    	}
    	for (Mob mob : mobs) {
    		if (mob.controlled) {
    			teamScores[mob.team-1]++;
    		}
    	}
    }
    
    public void countTeamRemaining() {
    	boolean oneAlive = false;
    	for (int x = 0; x < TEAMS; x++) {
    		teamRemaining[x] = 0;
    	}
    	for (Mob mob : mobs) {
    		if (mob.controlled && !mob.waved) {
    			teamRemaining[mob.team-1]++;
    			oneAlive = true;
    		}
    	}
    	if (teamRemaining[teamTurn-1] <= 0) {
			updateTeamTurn = true;
    	}
    	
    	if (!oneAlive) {
    		shouldGameOver = true;
    	} else {
    		shouldGameOver = false;
    	}
    }

    //@Override
    public void create() {
        /*
        The code to override the ApplicationAdapter goes here
         */
    }

    @Override
    public void render (float delta) {
        //make sure music is loaded
        if (loaded == false){
            loaded = startMusic();
        }
        if (!menuScreen){
            if (camera.zoom > 1) {
                camera.zoom -= 0.08;
                camera.position.x = camera.viewportWidth/2;
                camera.position.y = camera.viewportHeight/2;
            }
            else {
                camera.zoom = 1;
            }
        }
    	
    	if (camera.zoom > 1) {
    		camera.zoom -= 0.1;
    		camera.position.x = camera.viewportWidth/2;
    		camera.position.y = camera.viewportHeight/2;
    	}
    	else {
    		camera.zoom = 1;
    	}
    	map.world.step(delta, 6, 2);
        camera.update();

    	//map.tick();
    	for (Mob mob:mobs) {
            mob.tick(delta);
        }

        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);
        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        game.batch.begin();
        map.render(game.batch);
        for (Mob mob:mobs) {
            mob.render(game.batch);
        }
        for (Sprite building : buildings) {
            game.batch.draw(building, building.getX(), building.getY(), building.getOriginX(), building.getOriginY(), building.getWidth(), building.getHeight(), building.getScaleX(), building.getScaleY(), building.getRotation());
        }
        if (menuScreen){
            game.batch.draw(pressSpaceSprite, pressSpaceSprite.getX(), pressSpaceSprite.getY(), pressSpaceSprite.getOriginX(), pressSpaceSprite.getOriginY(), pressSpaceSprite.getWidth(), pressSpaceSprite.getHeight(), pressSpaceSprite.getScaleX(), pressSpaceSprite.getScaleY(), pressSpaceSprite.getRotation());
            game.batch.draw(playerAmountSprite, playerAmountSprite.getX(), playerAmountSprite.getY(), playerAmountSprite.getOriginX(), playerAmountSprite.getOriginY(), playerAmountSprite.getWidth(), playerAmountSprite.getHeight(), playerAmountSprite.getScaleX(), playerAmountSprite.getScaleY(), playerAmountSprite.getRotation());
        } else{
            if (TEAMS > 1){
                if (teamTurn == 1){
                    playerTurn.setTexture(bluePlayer);
                    playerTurn.setPosition(0f, 0f);
                } else if (teamTurn == 2){
                    playerTurn.setTexture(redPlayer);
                    playerTurn.setPosition(0f, 0f);
                } else if (teamTurn == 3) {
                    playerTurn.setTexture(greenPlayer);
                    playerTurn.setPosition(0f, 0f);
                } else if (teamTurn == 4) {
                    playerTurn.setTexture(yellowPlayer);
                    playerTurn.setPosition(0f, 0f);
                }
                if (!shouldGameOver) {
                    game.batch.draw(playerTurn, playerTurn.getX(), playerTurn.getY(), playerTurn.getOriginX(), playerTurn.getOriginY(), playerTurn.getWidth(), playerTurn.getHeight(), playerTurn.getScaleX(), playerTurn.getScaleY(), playerTurn.getRotation());
                }
            }
        }
        game.batch.end();
        //debugRenderer.render(map.world, debugMatrix);

        if (menuScreen){
            if (Gdx.input.justTouched()){
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                if (touchPos.x > Gdx.graphics.getWidth()/2f){
                    if (touchPos.y > Gdx.graphics.getHeight()/2f){
                        menuScreen = false;
                    }
                    else {
                        game.setScreen(new gameScreen(game, false, 4));
                        dispose();
                    }
                }
                if (touchPos.x < Gdx.graphics.getWidth()/2f){
                    if (touchPos.y > Gdx.graphics.getHeight()/2f){
                        game.setScreen(new gameScreen(game, false, 1));
                        dispose();
                    }
                    else {
                        game.setScreen(new gameScreen(game, false, 3));
                        dispose();
                    }
                }
            }
        }

//        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
//            game.setScreen(new gameScreen(game, false, TEAMS));
//            dispose();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
//        	game.setScreen(new gameOverScreen(game, teamScores, map.MOB_NUMBERS, TEAMS));
//            dispose();
//        }
        
    	countTeamRemaining();

        if (shouldGameOver) {
        	timer += delta;
        	if (Gdx.input.justTouched() && timer >= 1f) {
        		game.setScreen(new gameOverScreen(game, teamScores, map.MOB_NUMBERS, TEAMS));
                dispose();
            	shouldGameOver = false;
            	timer = 0;
        	}
        }
        else {
        	timer = 0;
            if (updateTeamTurn) {
                useTeamTurn();
            }
        }
    }

    public boolean startMusic() {
        if(assetManager.isLoaded("backgroundMusic.mp3")) {
            backgroundMusic = assetManager.get("backgroundMusic.mp3", Music.class);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();
            waveSound = assetManager.get("wave.mp3", Sound.class);
            //menuChange = assetManager.get("menuChange.mp3", Sound.class);
            return true;
        }else {
            //System.out.println("not loaded yet");
            return false;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        map.dispose();
        assetManager.dispose();
        backgroundMusic.dispose();
//        try{
//            pressSpace.dispose();
//        }
//        catch (Exception e){
//        }

    }


}
