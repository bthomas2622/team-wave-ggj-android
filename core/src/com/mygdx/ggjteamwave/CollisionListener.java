package com.mygdx.ggjteamwave;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionListener implements ContactListener {
	
	@Override
	public void beginContact(Contact contact) {
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		
		if (body1.getUserData() != null && body1.getUserData() instanceof Collideable) {
			if (body2.getUserData() != null && body2.getUserData() instanceof Collideable) {
				((Collideable) body1.getUserData()).onCollide((Collideable) body2.getUserData());
				((Collideable) body2.getUserData()).onCollide((Collideable) body1.getUserData());
			}
		}
	}
	
	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
};
