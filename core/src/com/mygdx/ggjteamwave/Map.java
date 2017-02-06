package com.mygdx.ggjteamwave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Map {

	// adding some static variables to represent the pixel offsets for nodes
	// Note: we are creating this as static finals now, but we could eventually add new ones
	// to allow us to scale our map
	public static final int INITIAL_NODE_PIXEL_OFFSET_X = 192;
	public static final int INITIAL_NODE_PIXEL_OFFSET_Y = 156;
	public static final int INITIAL_NODE_PIXEL_SIZE = 192;

	// Number of "blocks" aka Nodes on the map
	static final int WIDTH = 5;
	static final int HEIGHT = 3;
	static final int MOB_NUMBERS = 20;
	
	gameScreen game;

	World world;
	
	Node[][] nodes;
	
    static Texture backgroundImage = new Texture(Gdx.files.internal("grid.png"));;

    //building assetts
    public static Texture building1 = new Texture(Gdx.files.internal("buildings/blueBuilding.png"));
    public static Texture building2 = new Texture(Gdx.files.internal("buildings/blueBuilding2.png"));
    public static Texture building3 = new Texture(Gdx.files.internal("buildings/brownBuilding.png"));
    public static Texture building4 = new Texture(Gdx.files.internal("buildings/greenBuilding.png"));
    public static Texture building5 = new Texture(Gdx.files.internal("buildings/greyBuilding.png"));
    public static Texture building6 = new Texture(Gdx.files.internal("buildings/orangeBuilding.png"));
    public static Texture building7 = new Texture(Gdx.files.internal("buildings/orangeBuilding2.png"));
    public static Texture building8 = new Texture(Gdx.files.internal("buildings/pinkBuilding.png"));
    Sprite buildingSprite;
    float buildingRoller;

	public Map(gameScreen game) {
		this.game = game;
		world = new World(new Vector2(0, 0), false);
		world.setContactListener(new CollisionListener());
		nodes = new Node[WIDTH][HEIGHT];
        
	}
	
	public void tick() {
		
	}
	
	public void render(Batch batch) {
        batch.draw(backgroundImage, 0, 0, 1920, 1080);
	}

	public void generate() {
		generateNodes();
		generateBuildingBodies();

		for (int x = 1; x <= game.TEAMS; x++) {
			Mob startingPlayer = new Mob(game, createRoundBody(960, 540, Mob.BODY_WIDTH), true, x);//, nodes[2][1]);
			startingPlayer.controlled = true;
			game.mobs.add(startingPlayer);
		}

		for (int x = 0; x < MOB_NUMBERS; x++) {
			int startingX = MathUtils.random(WIDTH - 1);
			int startingY = MathUtils.random(HEIGHT - 1);
			int nodePosX = getNodePixelPosX(nodes[startingX][startingY]);
			int nodePosY = getNodePixelPosY(nodes[startingX][startingY]);
			Mob newMob = new Mob(game, createRoundBody(nodePosX, nodePosY, Mob.BODY_WIDTH), false, 0);//, nodes[startingX][startingY]);
			game.mobs.add(newMob);
		}
	}
	
	public void generateNodes() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				Node newNode = new Node(game, x, y);
				nodes[x][y] = newNode;
			}
		}
		/* Legacy code that generates nodes using xPos / yPos as PIXEL locations (rather than nodes
			array indices)
		for (int x = 1; x <= WIDTH; x++) {
			for (int y = 1; y <= HEIGHT; y++) {
				Node newNode = new Node(x * 192, y * 192);
				nodes[x - 1][y - 1] = newNode;
			}
		}
		*/
	}
	
	public void generateBuildingBodies() {
		for (int x = 0; x <= WIDTH; x++) {
			for (int y = 0; y <= HEIGHT; y++) {
				Body body = createStaticBody(-192 + INITIAL_NODE_PIXEL_OFFSET_X + x * INITIAL_NODE_PIXEL_SIZE * 2,
						-192 + INITIAL_NODE_PIXEL_OFFSET_Y + y * INITIAL_NODE_PIXEL_SIZE * 2, 192, 192);
                buildingRoller = MathUtils.random();
                buildingSprite = new Sprite(building1);
                if (buildingRoller <= 0.125) {
                    buildingSprite.setTexture(building1);
                } else if (buildingRoller > 0.125 & buildingRoller <= 0.125*2){
                	buildingSprite.setTexture(building2);
                } else if (buildingRoller > 0.125*2 & buildingRoller <= 0.125*3){
                	buildingSprite.setTexture(building3);
                } else if (buildingRoller > 0.125*3 & buildingRoller <= 0.125*4){
                	buildingSprite.setTexture(building4);
                } else if (buildingRoller > 0.125*4 & buildingRoller <= 0.125*5){
                	buildingSprite.setTexture(building5);
                } else if (buildingRoller > 0.125*5 & buildingRoller <= 0.125*6){
                	buildingSprite.setTexture(building6);
                } else if (buildingRoller > 0.125*6 & buildingRoller <= 0.125*7){
                	buildingSprite.setTexture(building7);
                } else {
                	buildingSprite.setTexture(building8);
                }
                buildingSprite.setPosition(body.getPosition().x * game.PIXELS_TO_METERS - (INITIAL_NODE_PIXEL_SIZE / 2f), body.getPosition().y * game.PIXELS_TO_METERS - (INITIAL_NODE_PIXEL_SIZE / 2f));
                buildingSprite.setOriginCenter();
                buildingSprite.setRotation(0f);
                game.buildings.add(buildingSprite);
			}
		}
	}

	// Returns the pixel location of a node based on its position in the node array nodes
	public int getNodePixelPosX(Node node) {
		// Refactor this code if we want to allow the map to zoom in and out
		int xPos = INITIAL_NODE_PIXEL_OFFSET_X + node.getXPos() * INITIAL_NODE_PIXEL_SIZE * 2;
		return xPos;
	}

	public int getNodePixelPosY(Node node) {
		int yPos = INITIAL_NODE_PIXEL_OFFSET_Y + node.getYPos() * INITIAL_NODE_PIXEL_SIZE * 2;
		return yPos;
	}

	public Body createBody(int x, int y, int width, int height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		// fixing rotation
		bodyDef.fixedRotation = true;

		bodyDef.position.set(x / game.PIXELS_TO_METERS, y / game.PIXELS_TO_METERS);
        //System.out.println(bodyDef.position);
		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();

		shape.setAsBox(width / 2f / game.PIXELS_TO_METERS, height / 2f / game.PIXELS_TO_METERS);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;

		// NOTE: REMOVE THIS LINE IF WE WANT COLLISIONS
		fixtureDef.isSensor = true;
		// THIS IS FALSE FOR TESTING

		body.createFixture(fixtureDef);

		shape.dispose();

		return body;
	}
	
	public Body createRoundBody(int x, int y, int radius) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		// fixing rotation
		bodyDef.fixedRotation = true;

		bodyDef.position.set(x / game.PIXELS_TO_METERS, y / game.PIXELS_TO_METERS);
		Body body = world.createBody(bodyDef);

		CircleShape shape = new CircleShape();

		shape.setRadius(radius / 2f / game.PIXELS_TO_METERS); 
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.restitution = 1f;

		body.createFixture(fixtureDef);

		shape.dispose();

		return body;
	}
	
	public Body createStaticBody(int x, int y, int width, int height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;

		bodyDef.position.set(x / game.PIXELS_TO_METERS, y / game.PIXELS_TO_METERS);
        //System.out.println(bodyDef.position);
		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();

		shape.setAsBox(width / 2f / game.PIXELS_TO_METERS, height / 2f / game.PIXELS_TO_METERS);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;

		body.createFixture(fixtureDef);

		shape.dispose();

		return body;
	}

	public Node[][] getNodes(){
		return nodes;
	}

    public void dispose(){
        world.dispose();
    }
}
