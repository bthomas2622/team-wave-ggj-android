package com.mygdx.ggjteamwave;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by zacha on 1/21/2017.
 */

public class MobPath {
    // Strings that represent preset path types (Custom being reserved for when the user completely
    // specifies a path
    public static final String PATH_VERTICAL = "VERTICAL";
    public static final String PATH_HORIZONTAL = "HORIZONTAL";
    public static final String PATH_CUSTOM = "CUSTOM";
    public static final String PATH_RECTANGLE = "RECTANGLE";
    public static final String PATH_DESTINATION = "DESTINATION";
    // This defers from destionation by allowing the user to specify a start position
    public static final String PATH_DESTINATION_FROM_START = "DESTINATION_FROM_START";

    // Note this array doesn't include custom because it is designed for random generation
    public static final String[] PATH_TYPES_ARRAY = {PATH_VERTICAL, PATH_HORIZONTAL, PATH_RECTANGLE, PATH_DESTINATION};
    // this doesn'tt include PATH_DESTINATION_WITH_START because that isn't designed for random

    // The path style that the mob is currently on
    private String currentPath = null;

    // The list of nodes that the mob will travel along
    private ArrayList<Node>  nodePath = new ArrayList<Node>();
    // The spot in the list that the mob is currently at
    // This integer will "rebound" meaning it will go 0, 1, 2, 3, 2, 1, 0, 1, 2 etc...
    private int pathIndex = 0;

    // marks whether we are travelling up or down the path
    private boolean ascending = true;

    // Constructor methods
    public MobPath(gameScreen screen, String pathType) {
        nodePath = createNodePath(screen.map, pathType);
        currentPath = pathType;

    }

    public MobPath(gameScreen screen, ArrayList<Node> customPath) {
        setCustomNodePath(customPath);
    }

    // Returns a random path type  from the array of path types
    public static String getRandomPathType(){
        return PATH_TYPES_ARRAY[MathUtils.random(0, PATH_TYPES_ARRAY.length - 1)];
    }

    // gets the next node in the mobs current sequence
    public Node nextNode(){
        // Rectangle paths are a special case because they loop unlike other paths
        if (currentPath == PATH_RECTANGLE)
        {
            ascending = true;
            // Note: ascending is always true for rectangle paths
            if (pathIndex >= nodePath.size() - 1)
                pathIndex = 0;
        }
        // All other paths should traverse up and down the path
        else {
            if (pathIndex >= nodePath.size() - 1) {
                pathIndex = nodePath.size() - 1;
                ascending = false;
            } else if (pathIndex <= 0) {
                pathIndex = 0;
                ascending = true;
            }
        }

        if (ascending)
            pathIndex += 1;
        else
            pathIndex -= 1;

        //System.out.println("Path index: " + pathIndex);
        return nodePath.get(pathIndex);
    }

    public Node getCurrentNode() {
        return nodePath.get((pathIndex));
    }

    // This is how you make a custom path
    public void setCustomNodePath(ArrayList<Node> newNodePath) {
        currentPath = PATH_CUSTOM;
        nodePath = newNodePath;
    }

    // In order to keep this method static, it must take in the current map
    public static ArrayList<Node> createNodePath(Map map, String pathType) {
        ArrayList<Node> path = new ArrayList<Node>();
        if (pathType == PATH_VERTICAL) {
            // Generating a random column to run up and down along the map
            // We could potentially add some way to create a path along a specific vertical later
            int column = MathUtils.random(0, map.WIDTH - 1);
            for (int i = 0; i < map.HEIGHT; i++)
                path.add(map.nodes[column][i]);
        }
        else if (pathType == PATH_HORIZONTAL) {
            // Generating a random row to run up across
            int row = MathUtils.random(0, map.HEIGHT - 1);
            for (int i = 0; i < map.WIDTH; i++)
                path.add(map.nodes[i][row]);
        }
        else if (pathType == PATH_RECTANGLE) {
            // first, we generate a starting column from 0 -> WIDTH - 2
            int startingColumn = MathUtils.random(0, map.WIDTH - 2);
            // then, we generate an ending column from  start to WIDTH - 1
            int endingColumn = MathUtils.random(startingColumn +1, map.WIDTH - 1);
            // next, we do the same thing for rows
            int startingRow = MathUtils.random(0, map.HEIGHT - 2);
            int endingRow = MathUtils.random(startingRow +1, map.HEIGHT - 1);

            //Then, we add the nodes from startingColumn, startingRow to endingColumn, Starting row
            for (int i = startingColumn; i <= endingColumn; i++)
                //path.add(map.nodes[startingRow][i]);
                path.add(map.nodes[i][startingRow]);

            for (int i = startingRow; i <= endingRow; i++)
                //path.add(map.nodes[i][endingColumn]);
                path.add(map.nodes[endingColumn][i]);

            for (int i = endingColumn; i >= startingColumn; i--)
                //path.add(map.nodes[endingRow][i]);
                path.add(map.nodes[i][endingRow]);

            for (int i = endingRow; i >= startingRow; i--)
                //path.add(map.nodes[i][startingColumn]);
                path.add(map.nodes[startingColumn][i]);

        }
        else if (pathType == PATH_DESTINATION) {
            int psX = MathUtils.random(0, map.WIDTH - 1);
            int psY = MathUtils.random(0, map.HEIGHT - 1);
            int peX = MathUtils.random(0, map.WIDTH  - 1);
            int peY = MathUtils.random(0, map.HEIGHT - 1);

            // Hacky code to ensure that the paths have some decent level of distance
            // note, this code does not allow for fully horizontal or vertical paths
            // (I wish I could say that it was by choice, but i'm too lazy / it actually improves
            // path distinction ;) )

            while (psX == peX) {
                psX = MathUtils.random(0, Map.WIDTH - 1);}
            while (psY == peY) {
                psY = MathUtils.random(0, Map.HEIGHT - 1);
            }
            // Because the closest path to a destination is the diagonal, we take turns adding
            // up / down and left / right movements  to the path until we are at the destination

            while (psX != peX || psY != peY){
                if (psX > peX){
                    psX -= 1;
                    path.add(map.nodes[psX][psY]);
                }
                else if (psX < peX) {
                    psX += 1;
                    path.add(map.nodes[psX][psY]);
                }
                if (psY > peY){
                    psY -= 1;
                    path.add(map.nodes[psX][psY]);
                }
                else if (psY < peY) {
                    psY += 1;
                    path.add(map.nodes[psX][psY]);
                }
            }
        }
        else if (pathType == PATH_CUSTOM) {
            // Does nothing if the path is custom
            // (because a randomly generated custom path is essentially just a destination path)
        }
        return path;
    }


    public void changeNodePath(Map map, int xStart, int yStart) {
        pathIndex = 0;
        currentPath = getRandomPathType();
        nodePath = createNodePath(map, currentPath, xStart, yStart);
    }

    // This is a super hacky, alternate behavior createNodePath that is designed for creating a
    // new path that starts at the current location
    // This makes it easy for mobs to switch paths
    public ArrayList<Node> createNodePath(Map map, String pathType, int xStart, int yStart) {
        pathIndex = 0;
        ArrayList<Node> path = new ArrayList<Node>();
        if (pathType == PATH_VERTICAL) {
            // Generating a random column to run up and down along the map
            // We could potentially add some way to create a path along a specific vertical later
            int column = xStart;
            for (int i = 0; i < map.HEIGHT; i++)
                path.add(map.nodes[column][i]);
        }
        else if (pathType == PATH_HORIZONTAL) {
            // Generating a random row to run up across
            int row = yStart;
            for (int i = 0; i < map.WIDTH; i++)
                path.add(map.nodes[i][row]);
        }
        else if (pathType == PATH_RECTANGLE) {
            // first, we generate a starting column from 0 -> WIDTH - 2
            int startingColumn = xStart;

            // next, we do the same thing for rows
            int startingRow = yStart;

            // then, we generate an ending column from  start to WIDTH - 1
            int endingColumn, endingRow;

            if (startingColumn == map.WIDTH - 1 && startingRow == map.HEIGHT -  1) {

                // I'm too lazy to rewrite my path code to accept " reverse" rectagles, so if you
                // get stuck in the corner, you will switch paths to a horizontal line to get
                // you out of the corner
                currentPath = PATH_HORIZONTAL;
                return createNodePath(map, PATH_HORIZONTAL, xStart, yStart);
                // note, this does mean that there will be a bug where the path type string does not
                // match the actual path type, but honestly that is never used outside of here
                // and we have to submit in 7 minutes

            }
            else if (startingColumn == map.WIDTH - 1) {
                endingColumn = startingColumn;
                endingRow = MathUtils.random(startingRow + 1, map.HEIGHT - 1);

            }
            else if (startingRow == Map.HEIGHT - 1){
                endingColumn = MathUtils.random(startingColumn + 1, map.WIDTH - 1);
                endingRow = startingRow;
            }
            else {
                // then, we generate an ending column from  start to WIDTH - 1
                endingColumn = MathUtils.random(startingColumn + 1, map.WIDTH - 1);
                endingRow = MathUtils.random(startingRow + 1, map.HEIGHT - 1);
            }

            //Then, we add the nodes from startingColumn, startingRow to endingColumn, Starting row
            for (int i = startingColumn; i <= endingColumn; i++)
                //path.add(map.nodes[startingRow][i]);
                path.add(map.nodes[i][startingRow]);

            for (int i = startingRow; i <= endingRow; i++)
                //path.add(map.nodes[i][endingColumn]);
                path.add(map.nodes[endingColumn][i]);

            for (int i = endingColumn; i >= startingColumn; i--)
                //path.add(map.nodes[endingRow][i]);
                path.add(map.nodes[i][endingRow]);

            for (int i = endingRow; i >= startingRow; i--)
                //path.add(map.nodes[i][startingColumn]);
                path.add(map.nodes[startingColumn][i]);

        }
        else if (pathType == PATH_DESTINATION) {
            int psX = xStart;
            int psY = yStart;
            int peX = MathUtils.random(0, map.WIDTH  - 1);
            int peY = MathUtils.random(0, map.HEIGHT - 1);

            // Hacky code to ensure that the paths have some decent level of distance
            // note, this code does not allow for fully horizontal or vertical paths
            // (I wish I could say that it was by choice, but i'm too lazy / it actually improves
            // path distinction ;) )

            while (psX == peX) {
                psX = MathUtils.random(0, Map.WIDTH - 1);}
            while (psY == peY) {
                psY = MathUtils.random(0, Map.HEIGHT - 1);
            }
            // Because the closest path to a destination is the diagonal, we take turns adding
            // up / down and left / right movements  to the path until we are at the destination

            while (psX != peX || psY != peY){
                if (psX > peX){
                    psX -= 1;
                    path.add(map.nodes[psX][psY]);
                }
                else if (psX < peX) {
                    psX += 1;
                    path.add(map.nodes[psX][psY]);
                }
                if (psY > peY){
                    psY -= 1;
                    path.add(map.nodes[psX][psY]);
                }
                else if (psY < peY) {
                    psY += 1;
                    path.add(map.nodes[psX][psY]);
                }
            }
        }
        else if (pathType == PATH_CUSTOM) {
            // Does nothing if the path is custom
            // (because a randomly generated custom path is essentially just a destination path)
        }
        return path;
    }
    public String getCurrentPathType() {
        return currentPath;
    }

    public ArrayList<Node> getNodePath(){
        return nodePath;
    }
}
