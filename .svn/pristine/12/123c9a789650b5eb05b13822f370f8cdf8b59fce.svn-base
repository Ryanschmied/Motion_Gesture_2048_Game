package ca.uwaterloo.ece155_nlab4;

import android.widget.ImageView;
import android.content.Context;

public abstract class GameBlockTemplate extends ImageView{

    protected int curX;

    protected int curY;

    protected int targX = 0;

    protected int targY = 0;

    protected int velX = 0;

    protected int velY = 0;
    //    Randomly initialize the value to 2 or 4
    protected int blockValue = (int)(Math.round(Math.random()) + 1) * 2;

    protected double accX = 2;

    protected double accY = 2;
    //    Used in merging algorithm to flag blocks
    protected boolean toDelete = false;
    protected boolean toMerge = false;

    public GameBlockTemplate(Context _myContext, int _coordX, int _coordY) {
        super(_myContext);
        curX = _coordX;
        curY = _coordY;
        this.setX(curX);
        this.setY(curY);
        targX = curX;
        targY = curY;
    }

    //    Should be in abstract class
    public void move () {

//        Calculate new velocities based on the current velocities and the acceleration
        calculateVelocity();
//        Adjust current coordinates based on velocities
        if (curX != targX) {
            curX += velX;
        }
        if (curY != targY) {
            curY += velY;
        }
//        Make sure we stay within the game screen bounds
        stayWithinBounds();
        this.setX(curX);
        this.setY(curY);
    }


    protected void calculateVelocity() {
        if (curX < targX) {
//            move to the right
            velX = velX > 0 ? velX : 5;
            velX += accX;
        }
        else if (curX > targX) {
//            move to the left
            velX = velX < 0 ? velX : -5;
            velX -= accX;
        }

        if (curY < targY) {
//            move downwards
            velY = velY > 0 ? velY : 5;
            velY += accY;
        }
        else if (curY > targY) {
//            move upwards
            velY = velY < 0 ? velY : -5;
            velY -= accY;
        }
    }

    protected void stayWithinBounds() {
//        If we go past the target coordinates, set the current coords to the target coords
        if (velX > 0 && curX > targX) {
            curX = targX;
        }
        if (velX < 0 && curX < targX) {
            curX = targX;
        }
        if (velY > 0 && curY > targY) {
            curY = targY;
        }
        if (velY < 0 && curY < targY) {
            curY = targY;
        }
    }

    public void setTargCoords(int _targX, int _targY) {
        targX = _targX;
        targY = _targY;
    }

    public int getValue() {
        return blockValue;
    }

    public boolean isMoving() {
        return (curX != targX) || (curY != targY);
    }

    public int getCurX() {
        return curX;
    }

    public int getCurY() {
        return curY;
    }

    public int getTargX() {
        return targX;
    }

    public int getTargY() {
        return targY;
    }

}