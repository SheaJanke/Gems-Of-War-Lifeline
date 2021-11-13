package com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests;

import android.util.Pair;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.ResultPair;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GemMatchingUtils {
    private static final int BOARD_SIZE = Constants.BOARD_SIZE;

    public static boolean validateGemBoardFromFragment(GemType[][] validateFragment, GemType[][] testFragment){
        boolean valid = true;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(i < validateFragment.length && j < validateFragment[i].length){
                    if(validateFragment[i][j] != testFragment[i][j]){
                        System.out.println("Expected: " + validateFragment[i][j] + " Actual: " + testFragment[i][j]);
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
    public static boolean validateDisplayResults(Result result, Map<GemType, Integer> expectedGemsMatched) {
        ArrayList<ResultPair> matchedGems = result.getDisplayResults();
        boolean isValid = true;
        for(int i = 0; i < matchedGems.size(); i++){
            GemType gemType = matchedGems.get(i).gemType;
            int numMatched = matchedGems.get(i).numGems;
            if(expectedGemsMatched.getOrDefault(gemType, 0) != numMatched){
                isValid = false;
            }
        }
        int expectedTotalMatched = 0;
        for(Integer matched : expectedGemsMatched.values()){
            expectedTotalMatched += matched;
        }
        if(expectedTotalMatched != result.totalMatched()){
            isValid = false;
        }
        return isValid;
    }

    public static Result matchAndAssert(GemType[][] initFrag, GemType[][] expectedFinalFrag,Map<GemType, Integer> expectedGemsMatched, boolean expectedExtraTurn) {
        GemType[][] initialBoard = GemMatchingUtils.generateFullBoardFromFragment(initFrag);
        Result result = BoardUtils.getResult(initialBoard, 0, 0, 0, 0);
        assertTrue(validateDisplayResults(result, expectedGemsMatched));
        assertEquals(expectedExtraTurn, result.getExtraTurn());
        assertTrue(GemMatchingUtils.validateGemBoardFromFragment(expectedFinalFrag, result.getFinalBoard()));
        return result;
    }
}
