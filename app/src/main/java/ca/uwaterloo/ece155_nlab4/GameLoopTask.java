package ca.uwaterloo.ece155_nlab4;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.TimerTask;

import lab4_204_01.uwaterloo.ca.lab4_204_01.R;

import static ca.uwaterloo.ece155_nlab4.GameBlock.SCALED_IMAGE_WIDTH;
import static ca.uwaterloo.ece155_nlab4.GameBlock.SCALING_ADJUST;



public class GameLoopTask extends TimerTask {

    public enum DirectionType {
        UP, DOWN, LEFT, RIGHT, NO_MOVEMENT
    }

    private DirectionType currentDir = DirectionType.NO_MOVEMENT;
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myRL;
    private TextView winLossTV;
    private LinkedList<GameBlock> gameBlocks = new LinkedList<>();
    private LinkedList<GameBlock> blocksToDelete = new LinkedList<>();

    static private int GAMEBOARD_DIMENSION = 1000;
    private boolean canInput = true;
    private boolean prevCanInput = true;

    final static int TOP_BOUNDARY = 0 - SCALING_ADJUST;
    final static int BOTTOM_BOUNDARY = GAMEBOARD_DIMENSION - SCALED_IMAGE_WIDTH - SCALING_ADJUST;
    final static int LEFT_BOUNDARY = 0 - SCALING_ADJUST;
    final static int RIGHT_BOUNDARY = GAMEBOARD_DIMENSION - SCALED_IMAGE_WIDTH - SCALING_ADJUST;

    public GameLoopTask (Activity _myActivity, Context _myContext, RelativeLayout _myRL, TextView _winLossTV) {
        myActivity = _myActivity;
        myContext = _myContext;
        myRL = _myRL;
        winLossTV = _winLossTV;
        gameBlocks.add(createBlock());
        gameBlocks.add(createBlock());
    }

    public void run() {
        myActivity.runOnUiThread(
            new Runnable(){
                public void run() {
//                    do game timer stuff here
                    for (GameBlock gameBlock: gameBlocks) {
                        gameBlock.move();
                    }
//                    Check if any blocks are moving
                    for (GameBlock gameBlock : gameBlocks) {
                        if (gameBlock.isMoving()) {
                            canInput = false;
                            break;
                        }
                        canInput = true;
                    }
//                    Check for win or lose conditions
                    checkWin();
                    if (!prevCanInput && canInput) {
                        onCanInputRisingEdge();
                    }
                    prevCanInput = canInput;
                }
            }
        );
    }

    private void onCanInputRisingEdge() {
        canInput = true;
        checkLose();
        for (GameBlock gameBlock : blocksToDelete) {
            myRL.removeView(gameBlock.getValueText());
            myRL.removeView(gameBlock);
            gameBlocks.remove(gameBlock);
        }
        if (gameBlocks.size() < 16) {
            gameBlocks.add(createBlock());
        }
    }

    private void checkLose() {
        if (!canBlocksMerge() && gameBlocks.size() > 15) {
            winLossTV.setText("LOSE");
            winLossTV.bringToFront();
            return;
        }
    }

    private void checkWin() {
        for (GameBlock gameBlock : gameBlocks) {
            if (gameBlock.getValue() > 128) {
                winLossTV.setText("WIN");
                winLossTV.bringToFront();
                return;
            }
        }
    }

    private int[] getNewBlockCoords() {
        int column = 1;
        int row = 1;
        int[] result = new int[2];
        boolean overlap = true;
//        Try new row and column numbers until we don't overlap with another block
        while (overlap) {
            overlap = false;
            column = (int)(Math.random() * (4));
            row = (int)(Math.random() * (4));
            for (GameBlock gameBlock : gameBlocks) {
                if (getRowFromCoord(gameBlock.getMiddleY()) == row && getColumnFromCoord(gameBlock.getMiddleX()) == column) {
                    overlap = true;
                }
            }
        }
        result[0] = getCoordFromColumn(column);
        result[1] = getCoordFromRow(row);
        return result;
    }

    private GameBlock createBlock() {
        int[] newCoords = getNewBlockCoords();
//        Create a textview to display the block's value
        TextView blockValueTV = new TextView(myContext);
        GameBlock newBlock = new GameBlock(myContext, blockValueTV, newCoords[0], newCoords[1]); //Or any (x,y) of your choice
        myRL.addView(newBlock);
        myRL.addView(blockValueTV);
        return newBlock;
    }

    private int getCoordFromRow (int _row) {
        int rowHeight = GAMEBOARD_DIMENSION / 4;
        return LEFT_BOUNDARY + (_row * rowHeight);
    }

    private int getCoordFromColumn (int _column) {
        int columnWidth = GAMEBOARD_DIMENSION / 4;
        return TOP_BOUNDARY + (_column * columnWidth);
    }

    private int getRowFromCoord (int _coordY) {
        int rowHeight = GAMEBOARD_DIMENSION / 4;
        for (int i = 0; i < 4; i++) {
            if (_coordY > i * rowHeight && _coordY < (i + 1) * rowHeight) {
                return i;
            }
        }
//        Error, coordinate not in a valid row
        return -1;
    }

    private int getColumnFromCoord (int _coordX) {
        int columnWidth = GAMEBOARD_DIMENSION / 4;
        for (int i = 0; i < 4; i++) {
            if (_coordX > i * columnWidth && _coordX < (i + 1) * columnWidth) {
                return i;
            }
        }
//        Error, coordinate not in a valid column
        return -1;
    }

    private int calculateTargX (GameBlock _blockToMove, DirectionType _dir) {
        int numBlocksInFront = _blockToMove.willBeDeleted() ? -1 : 0;
        int coordX = _blockToMove.getMiddleX();
        int dirFactor = _dir == DirectionType.RIGHT ? 1 : -1;
        int row = getRowFromCoord(_blockToMove.getMiddleY());
        int gameBlockX;

//        Make sure we are moving horizontally
        if (!(_dir == DirectionType.RIGHT || _dir == DirectionType.LEFT)) {
            return _blockToMove.getCurX();
        }
//        Calculate the number of blocks in front of the given coordinate
        for (GameBlock gameBlock : gameBlocks) {
            gameBlockX = gameBlock.getMiddleX();
            if (gameBlockX != coordX) {
                if (row == getRowFromCoord(gameBlock.getMiddleY())) {
                    if (!gameBlock.willBeDeleted() && dirFactor * gameBlockX > dirFactor * coordX) {
                        numBlocksInFront++;
                    }
                }
            }
        }
        if (_dir == DirectionType.RIGHT) {
            return getCoordFromColumn(3 - numBlocksInFront);
        }
        else {
            return getCoordFromColumn(numBlocksInFront);
        }
    }

    private int calculateTargY (GameBlock _blockToMove, DirectionType _dir) {
        int numBlocksInFront = _blockToMove.willBeDeleted() ? -1 : 0;
        int coordY = _blockToMove.getMiddleY();
        int dirFactor = _dir == DirectionType.DOWN ? 1 : -1;
        int column = getColumnFromCoord(_blockToMove.getMiddleX());
        int gameBlockY;

//        Make sure we are moving vertically
        if (!(_dir == DirectionType.UP || _dir == DirectionType.DOWN)) {
            return _blockToMove.getCurY();
        }
//        Calculate the number of blocks in front of the given coordinate
        for (GameBlock gameBlock : gameBlocks) {
            gameBlockY = gameBlock.getMiddleY();
            if (gameBlockY != coordY) {
                if (column == getColumnFromCoord(gameBlock.getMiddleX())) {
                    if (!gameBlock.willBeDeleted() && dirFactor * gameBlockY > dirFactor * coordY) {
                        numBlocksInFront++;
                    }
                }
            }
        }
        if (_dir == DirectionType.DOWN) {
            return getCoordFromRow(3 - numBlocksInFront);
        } else {
            return getCoordFromRow(numBlocksInFront);
        }
    }

    private GameBlock getBlockInFront (GameBlock _blockToCheck, DirectionType _dir) {
        int dirFactor;
        int coord;
        int gameBlockCoord;
//        row or column number of the block to check
        int rcNum;
        GameBlock blockInFront = null;

        if (_dir == DirectionType.RIGHT || _dir == DirectionType.LEFT) {
//            horizontal checking
            dirFactor = _dir == DirectionType.RIGHT ? 1 : -1;
            coord = _blockToCheck.getMiddleX();
            rcNum = getRowFromCoord(_blockToCheck.getMiddleY());
            for (GameBlock gameBlock : gameBlocks) {
                gameBlockCoord = gameBlock.getMiddleX();
//                make sure the block we're checking is not the same block
                if (gameBlockCoord != coord) {
//                    check that block is in the same row
                    if (rcNum == getRowFromCoord(gameBlock.getMiddleY())) {
//                        check that block is in front of block passed in
                        if (dirFactor * gameBlockCoord > dirFactor * coord) {
//                            check that block is in front of currently frontmost block
                            if (blockInFront == null || dirFactor * gameBlockCoord < dirFactor * blockInFront.getMiddleX()) {
                                blockInFront = gameBlock;
                            }
                        }
                    }
                }
            }
        } else if (_dir == DirectionType.UP || _dir == DirectionType.DOWN) {
//            vertical checking
            dirFactor = _dir == DirectionType.DOWN ? 1 : -1;
            coord = _blockToCheck.getMiddleY();
            rcNum = getColumnFromCoord(_blockToCheck.getMiddleX());
            for (GameBlock gameBlock : gameBlocks) {
                gameBlockCoord = gameBlock.getMiddleY();
//                make sure the block we're checking is not the same block
                if (gameBlockCoord != coord) {
//                    check that block is in the same row
                    if (rcNum == getColumnFromCoord(gameBlock.getMiddleX())) {
//                        check that block is in front of block passed in
                        if (dirFactor * gameBlockCoord > dirFactor * coord) {
//                            check that block is in front of currently frontmost block
                            if (blockInFront == null || dirFactor * gameBlockCoord < dirFactor * blockInFront.getMiddleY()) {
                                blockInFront = gameBlock;
                            }
                        }
                    }
                }
            }
        }
        return blockInFront;
    }

    private int mergeBlocks(DirectionType _dir) {
        int blocksMerged = 0;
        GameBlock blockInFront;
        for (GameBlock gameBlock : gameBlocks) {
//            get the block in front of the current block
            blockInFront = getBlockInFront(gameBlock, _dir);
            if (blockInFront != null) {
                if (blockInFront.getValue() == gameBlock.getValue() &&
                    !blockInFront.willBeDeleted() && !blockInFront.willBeMerged() &&
                    !gameBlock.willBeDeleted() && !gameBlock.willBeMerged()) {

                    gameBlock.flagForDeletion();
                    blockInFront.setValue(blockInFront.getValue() * 2);
                    blockInFront.bringToFront();
                    blockInFront.getValueText().bringToFront();
                    blockInFront.setToMerge(true);
                    blocksMerged++;
                }
            }
        }
        for (GameBlock gameBlock : gameBlocks) {
            if (gameBlock.willBeMerged()) {
                gameBlock.setToMerge(false);
            }
            if (gameBlock.willBeDeleted()) {
                blocksToDelete.add(gameBlock);
            }
        }
        return blocksMerged;
    }

    private boolean canBlocksMerge() {
        int c1,c2,r1,r2;
        for (GameBlock gameBlock : gameBlocks) {
            c1 = getColumnFromCoord(gameBlock.getMiddleX());
            r1 = getRowFromCoord(gameBlock.getMiddleY());
            for (GameBlock blockToCheck : gameBlocks) {
                c2 = getColumnFromCoord(blockToCheck.getMiddleX());
                r2 = getRowFromCoord(blockToCheck.getMiddleY());
                if (gameBlock.getValue() == blockToCheck.getValue() && ((c1 != c2 && r1 == r2 && Math.abs(c1 - c2) < 2) || (r1 != r2 && c1 == c2 && Math.abs(r1 - r2) < 2))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean willBlocksMove(DirectionType _dir) {
        for (GameBlock gameBlock : gameBlocks) {
            if (calculateTargX(gameBlock, _dir) != gameBlock.getCurX() || calculateTargY(gameBlock, _dir) != gameBlock.getCurY()) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(DirectionType newDirection) {
//        Only redirect the blocks if we are not moving
        if (!canInput) {
            return;
        }

        if (mergeBlocks(newDirection) == 0 && !willBlocksMove(newDirection)) {
            return;
        }

        currentDir = newDirection;
//        Pass the new direction onto the game block
        for (GameBlock gameBlock : gameBlocks) {
            gameBlock.setTargCoords(calculateTargX(gameBlock, currentDir), calculateTargY(gameBlock, currentDir));
        }
        Log.d("direction setter log", "Direction setter called");
    }

    public DirectionType getDirection() {
        return currentDir;
    }

    public boolean canReceiveInputs() {
        return canInput;
    }
}
