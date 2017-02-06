package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;

public interface Collideable {
	public Body getBody();
	
	public void onCollide(Collideable object);
}
