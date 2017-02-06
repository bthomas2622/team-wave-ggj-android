package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by zacha on 1/21/2017.
 */

// Class designed to add transparent color trails to the game where people who are waving go
public class ColorTrail {
    public static Color default_trail_color = new Color(1, 0, 1, (float)(.1));
    public Color trailColor;
    private int xPos, yPos;
    private int radius = 10;
    private static final int MAX_RADIUS = 100;
    private static final int RADIUS_GROWTH_SPEED = 2;

    public ColorTrail(int xPosition, int yPosition, Color color) {
        xPos = xPosition;
        yPos = yPosition;
        trailColor = color;
    }
    public ColorTrail(int xPosition, int yPosition) {
        xPos = xPosition;
        yPos = yPosition;
        trailColor = getRandomColor();
    }
    public void drawTrail(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(trailColor);
        shapeRenderer.circle(xPos, yPos, radius);
        if (radius <= MAX_RADIUS )
            radius += RADIUS_GROWTH_SPEED;
    }

    public Color getRandomColor(){
        return new Color(MathUtils.random(76, 256), MathUtils.random(76, 256), MathUtils.random(76, 256), (float).1);
    }

/*
    // Experimental Code
    public void renderColorTrails(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < colorTrails.size(); i++)
            colorTrails.get(i).drawTrail(shapeRenderer);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // Arraylist to contain all the color trails on screen
    ArrayList<ColorTrail> colorTrails = new ArrayList<ColorTrail>();

    // rendering color trails
    if (colorTrails.size() > 0)
    renderColorTrails();

    */

}
