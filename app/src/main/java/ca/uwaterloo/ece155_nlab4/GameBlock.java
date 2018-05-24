package ca.uwaterloo.ece155_nlab4;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import lab4_204_01.uwaterloo.ca.lab4_204_01.R;

public class GameBlock extends GameBlockTemplate {

//    Randomly initialize the value to 2 or 4
    private int blockValue = (int)(Math.round(Math.random()) + 1) * 2;
    //    Should be in abstract class
    private double accX = 2;
    //    Should be in abstract class
    private double accY = 2;
//    Used in merging algorithm to flag blocks
    private boolean toDelete = false;
    private boolean toMerge = false;

//    private GameLoopTask.DirectionType currentDir = GameLoopTask.DirectionType.NO_MOVEMENT;
    private TextView valueText;

    final static int GAMEBOARD_DIMENSION = 1000;
    final static float IMAGE_SCALE = 0.646f;
    final static int IMAGE_WIDTH = 393;
    final static int SCALED_IMAGE_WIDTH = (int)Math.ceil(IMAGE_WIDTH * IMAGE_SCALE);
    final static int SCALING_ADJUST = (IMAGE_WIDTH - SCALED_IMAGE_WIDTH)/2;

    public GameBlock(Context _myContext, TextView _blockValueTV, int _coordX, int _coordY) {
        super(_myContext, _coordX, _coordY);
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);

        valueText = _blockValueTV;
        valueText.setX(calculateTextX());
        valueText.setY(calculateTextY());
        valueText.setText(Integer.toString(blockValue));
        valueText.setTextSize(35);
        valueText.measure(0,0);
    }

    public void move () {
        super.move();
        valueText.setX(calculateTextX());
        valueText.setY(calculateTextY());
    }
    private int calculateTextX() {
        return (int)(curX + SCALING_ADJUST + (SCALED_IMAGE_WIDTH / 2) - (valueText.getMeasuredWidth()/2));
    }

    private int calculateTextY() {
        return (int)(curY + SCALING_ADJUST + (SCALED_IMAGE_WIDTH / 2) - (valueText.getMeasuredHeight()/2));
    }

    public void setValue(int _value) {
        blockValue = _value;
        valueText.setText(Integer.toString(blockValue));
        valueText.measure(0,0);
    }

    public int getValue() {
        return blockValue;
    }

    public int getMiddleX() {
        return curX + SCALING_ADJUST + (SCALED_IMAGE_WIDTH / 2);
    }

    public int getMiddleY() {
        return curY + SCALING_ADJUST + (SCALED_IMAGE_WIDTH / 2);
    }

    public int getMiddleTargX() {
        return targX + SCALING_ADJUST + (SCALED_IMAGE_WIDTH / 2);
    }

    public int getMiddleTargY() {
        return targY + SCALING_ADJUST + (SCALED_IMAGE_WIDTH / 2);
    }

    public void flagForDeletion() {
        toDelete = true;
    }

    public void cancelDeletion() {
        toDelete = false;
    }

    public boolean willBeDeleted() {
        return toDelete;
    }

    public void setToMerge(boolean _toMerge) {
        toMerge = _toMerge;
    }

    public boolean willBeMerged() {
        return toMerge;
    }

    public TextView getValueText() {
        return valueText;
    }
}
