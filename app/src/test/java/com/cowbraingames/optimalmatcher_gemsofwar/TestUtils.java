package com.cowbraingames.optimalmatcher_gemsofwar;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public class TestUtils {
    private static final int BOARD_SIZE = Constants.BOARD_SIZE;

    public static boolean validateGemBoardFromFragment(GemType[][] validateFragment, GemType[][] testFragment){
        boolean valid = true;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(i < validateFragment.length && j < validateFragment[0].length){
                    if(validateFragment[i][j] != testFragment[i][j]){
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    public static GemType[][] generateFullBoardFromFragment(GemType[][] boardFragment) {
        GemType[][] fullBoard = new GemType[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(i < boardFragment.length && j < boardFragment[i].length){
                    fullBoard[i][j] = boardFragment[i][j];
                }else{
                    fullBoard[i][j] = GemType.BLOCK;
                }
            }
        }
        return fullBoard;
    }

}
