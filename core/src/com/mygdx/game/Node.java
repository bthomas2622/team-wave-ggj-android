package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;

public class Node
{
	private gameScreen game;
	private int xPos, yPos;
	// Flagged true for end nodes, so that "exiting" people know to be removed when they reach it
	private boolean endNode;


	public Node(gameScreen gScreen, int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.game = gScreen;
	}

	// Methods that allow some math between Nodes
	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public int getXPixelPos() {
		return game.map.getNodePixelPosX(this);
	}

	public int getYPixelPos() {
		return game.map.getNodePixelPosY(this);
	}


	public ArrayList<Node> getNeighborNodes(){
		ArrayList<Node> neighborNodes = new ArrayList<Node>();

		// Ghetto hacking in a method to get the neighboring nodes... look for a better way if time
		if (xPos == 0)
			neighborNodes.add(game.map.nodes[xPos + 1][yPos]);
		else if (xPos == Map.WIDTH -1)
			neighborNodes.add(game.map.nodes[xPos - 1][yPos]);
		else {
			neighborNodes.add(game.map.nodes[xPos + 1][yPos]);
			neighborNodes.add(game.map.nodes[xPos - 1][yPos]);
		}

		if (yPos == 0)
			neighborNodes.add(game.map.nodes[xPos][yPos + 1]);
		else if (yPos == Map.HEIGHT - 1)
			neighborNodes.add(game.map.nodes[xPos][yPos - 1]);
		else {
			neighborNodes.add(game.map.nodes[xPos][yPos + 1]);
			neighborNodes.add(game.map.nodes[xPos][yPos - 1]);
		}
		return neighborNodes;
	}

	public Node getRandomNeighborNode(){
		ArrayList<Node> neighborNodes = getNeighborNodes();
		return neighborNodes.get(MathUtils.random(0, neighborNodes.size() - 1));
	}

}
