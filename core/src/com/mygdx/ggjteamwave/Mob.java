package com.mygdx.ggjteamwave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

public class Mob implements Collideable {

	static final int BODY_WIDTH = 30;
	// Variable that determines how close a mob needs to get to its target to be considered "at" it
	private static final float TARGET_COLLISION_TOLERANCE = 200 / gameScreen.PIXELS_TO_METERS;
	// Amount moved by the mob per tick (in pixels)

	private static final float MOVE_SPEED = 2; //2

	private static final float RETARGET_TIME = 0.5f; //.5
	public static Texture mobImage = new Texture(Gdx.files.internal("pencilNeutralPedestrian.png"));
	public static Texture mobImageReadyBlue = new Texture(Gdx.files.internal("pencilReadyPedestrianBlue.png"));
	public static Texture mobImageReadyRed = new Texture(Gdx.files.internal("pencilReadyPedestrianRed.png"));
	public static Texture mobImageReadyGreen = new Texture(Gdx.files.internal("pencilReadyPedestrianGreen.png"));
	public static Texture mobImageReadyYellow = new Texture(Gdx.files.internal("pencilReadyPedestrianYellow.png"));
	public static Texture mobImageSpentBlue = new Texture(Gdx.files.internal("pencilSpentPedestrianBlue.png"));
	public static Texture mobImageSpentRed = new Texture(Gdx.files.internal("pencilSpentPedestrianRed.png"));
	public static Texture mobImageSpentGreen = new Texture(Gdx.files.internal("pencilSpentPedestrianGreen.png"));
	public static Texture mobImageSpentYellow = new Texture(Gdx.files.internal("pencilSpentPedestrianYellow.png"));
	gameScreen game;
	WaveObject wave;
    Sprite mobSprite;
    float MobDice;
    boolean startingPlayer;

	boolean controlled;		// Has been waved at
	boolean waved;			// Has performed a wave
    Node target;            // Node with the position the mob wants to move to
    Body body;                // Mob body
	float retargetTimer;
	
	int team;

    // Variable to store the mobs path
    private MobPath path;

	// Variables to handle switching mob paths
	// number of ticks before switching paths (1 second = 60 ticks
	public static final int PATH_SWITCH_TIMER_LENGTH = 1200;

	// local tick counter
	private int pathSwitchTimer = 0;




    // Base constructor now takes in a mobpath
    public Mob(gameScreen game, Body body, MobPath mobPath, boolean isStartingPlayer, int team) {
        this.game = game;
        this.body = body;
        this.team = team;
        this.startingPlayer = isStartingPlayer;
        //this.target = target;
        MobDice = MathUtils.random();
        mobSprite = new Sprite(mobImage);

        mobSprite.setPosition(body.getPosition().x * game.PIXELS_TO_METERS - (BODY_WIDTH / 2f), body.getPosition().y * game.PIXELS_TO_METERS - (BODY_WIDTH / 2f));
        mobSprite.setOriginCenter();
        mobSprite.setRotation(0f);
        body.setUserData(this);

        // setting the mob's path to the passed in mobpath instance
        path = mobPath;
		setTarget(path.getCurrentNode());
    }

    // Alternate constructor creates a random path for the  mob
    public Mob(gameScreen game, Body body, boolean isStartingPlayer, int team) {
        this(game, body, new MobPath(game, MobPath.getRandomPathType()), isStartingPlayer, team);
    }

    // Method that gets called whenever the game is "updating"
	public void tick(float delta) {
		if (wave != null) {
			wave.tick(delta);
		}
		retargetTimer += delta;
		if (retargetTimer >= RETARGET_TIME) {
			moveTowardTarget();
			retargetTimer = 0;
		}

        if (!game.menuScreen) {
            if ((Gdx.input.justTouched() && team == game.teamTurn)) {
                if (controlled && !waved) {
                    wave();
                }
                game.updateTeamTurn = true;
            }
        }

		if (atTarget()) {
			if (pathSwitchTimer >= PATH_SWITCH_TIMER_LENGTH)
			{
				pathSwitchTimer = 0;
				path.changeNodePath(game.map, target.getXPos(), target.getYPos());
				setTarget(path.getCurrentNode());
			}
			else {
				setTarget(path.nextNode());


				retargetTimer = RETARGET_TIME;
			}
		}

		if (pathSwitchTimer < PATH_SWITCH_TIMER_LENGTH)
			pathSwitchTimer += 1;
	}

	public void render(Batch batch) {
		if (waved) {
			if (team == 1) {
				mobSprite.setTexture(mobImageSpentBlue);
			}
			else if (team == 2) {
				mobSprite.setTexture(mobImageSpentRed);
			}
			else if (team == 3) {
				mobSprite.setTexture(mobImageSpentGreen);
			}
			else {
				mobSprite.setTexture(mobImageSpentYellow);
			}
        } else if (controlled) {
        	if (team == 1) {
				mobSprite.setTexture(mobImageReadyBlue);
			}
        	else if (team == 2) {
				mobSprite.setTexture(mobImageReadyRed);
			}
			else if (team == 3) {
				mobSprite.setTexture(mobImageReadyGreen);
			}
			else {
				mobSprite.setTexture(mobImageReadyYellow);
			}
        } else {
            mobSprite.setTexture(mobImage);
        }
		
		mobSprite.setPosition(body.getPosition().x * game.PIXELS_TO_METERS - (BODY_WIDTH / 2f), body.getPosition().y * game.PIXELS_TO_METERS - (BODY_WIDTH / 2f));
		batch.draw(mobSprite, mobSprite.getX(), mobSprite.getY(), mobSprite.getOriginX(), mobSprite.getOriginY(), mobSprite.getWidth() / 2, mobSprite.getHeight() / 2, mobSprite.getScaleX(), mobSprite.getScaleY(), mobSprite.getRotation());
		if (waved) {
			wave.drawWave(batch);
		}
	}

    /**
     * Code to generate wave
     * Place the projectiles around the person object in a circular fashion
     * Each projectile will be a 2x2 blue rectangle travelling for some 'd' distance
     * Number of projectiles = 360/18 = 20
	 * Need to calculate each projectiles center location (x,y) around the particular person object
	 * To be decided:
	 * 1. Velocity of the projectile
	 * 2. TTL = Time to live. The projectile will expire after 's' seconds
     */
    public void wave() {
		wave = new WaveObject(game, team);
		wave.positionDrops(mobSprite.getX() + 25, mobSprite.getY() + 40);
		waved = true;
		game.waveSound.play(.25f);
    }

    //  Move method to tie atTarget() and moveTowardTarget() together
    // Checks if the mob is at a target, switches if it is, then moves towards the target
    public void move() {
        if (atTarget())
            setTarget(path.nextNode());

        moveTowardTarget();
    }

	// returns true when the mob is at the target node
	private boolean atTarget() {
		// Note, we have to adjust the xPos because it is not a pixel position
		float dX = Math.abs(this.getXPixelPos() - target.getXPixelPos());
		float dY = Math.abs(this.getYPixelPos() - target.getYPixelPos());
		//System.out.println("Mob x: " + this.getXPixelPos() + " target X: " + target.getXPixelPos());
		//System.out.println("Mob y: " + this.getYPixelPos() + " target y: " + target.getYPixelPos());

		//System.out.println("DX: " + dX + ", DY " + dY);
		//System.out.println("atTarget: " + (dX <= 96 && dY <= 96));
		return (dX <= TARGET_COLLISION_TOLERANCE && dY <= TARGET_COLLISION_TOLERANCE);
	}

    // Moves the target one "MOVE_SPEED" towards the target Node
    public void moveTowardTarget() {
        // Note: this code is based on the assumption that there is a straight line between
        // the mob and its target node. This does come with the advantage that it allows for
        // diagonal movement paths

		// I'm leaving the debug printlns in in case we ever decide to hone in on the issue

		float deltaX = target.getXPixelPos() - this.getXPixelPos();
		float deltaY = target.getYPixelPos() - this.getYPixelPos();

		float theta = (float)Math.atan(deltaY / deltaX);

		float Vx = (float) Math.cos(theta) * MOVE_SPEED;
		float Vy = (float) Math.sin(theta) * MOVE_SPEED;

		// Super hacky method of making sure that our mobs don't run off the screen
		if ((deltaX > 0 && Vx < 0) || (deltaX < 0 && Vx > 0))
			Vx = -Vx;
		if ((deltaY > 0 && Vy < 0) || (deltaY < 0 && Vy > 0))
			Vy = -Vy;

		//System.out.println("Vx: " + Vx + ", Vy: " + Vy +", Theta: " + theta +", target X: " + target.getXPos() + ", target Y: " + target.getYPos());
		this.body.setLinearVelocity(Vx, Vy);
	}

	public float getPixelDistanceToTarget() {
		double dX2 = Math.pow((target.getXPixelPos() - this.getXPixelPos()), 2);
		double dY2 = Math.pow((target.getYPixelPos() - this.getYPixelPos()), 2);
		return (float) Math.sqrt(dX2 + dY2);
	}

	// Moving the position access calls into a helper to increase code readability
	// Note these are not pixel positions
	public float getXPos() {
		return body.getPosition().x;
	}

    public float getYPos() {
        return body.getPosition().y;
    }

    public void setPos(float newXPos, float newYPos) {
        body.getPosition().set(newXPos, newYPos);
    }

	// These methods are pixel positions
	public float getXPixelPos() {
		return body.getPosition().x * game.PIXELS_TO_METERS;
	}

	public float getYPixelPos() {
		return body.getPosition().y * game.PIXELS_TO_METERS;
	}

    // Helper functions for dealing with mob targets
    public Node getTarget() {
        return target;
    }

    public void setTarget(Node newTarget) {
        target = newTarget;
    }

	public void setMobPath(MobPath newPath) {
		path = newPath;
	}

    // Dispose Method
	public void dispose(){
//		mobImage.dispose();
	}

	@Override
	public void onCollide(Collideable object) {
		if (object instanceof WaveObject) {
			if (controlled == false || (((WaveObject)object).team != team && waved)) {
				controlled = true;
				team = ((WaveObject)object).team;
				game.updateTeamScores();
			}	
		}
		
		if (!body.getFixtureList().get(0).isSensor() && !(object instanceof WaveObject)) {
			body.setLinearVelocity(MathUtils.random(-1, 1) , MathUtils.random(-1, 1));
		}
	}

	public Body getBody() {
		return body;
	}
}
