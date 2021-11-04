package com.cowbraingames.optimalmatcher_gemsofwar;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Ground_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other.Block_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.ResultPair;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class BoardUtilsTests {
    private final int BOARD_SIZE = Constants.BOARD_SIZE;
    private final GemType FIRE = GemType.FIRE;
    private final GemType WATER = GemType.WATER;
    private final GemType WILD_X2 = GemType.WILD_X2;
    private final GemType WILD_X3 = GemType.WILD_X3;
    private final GemType WILD_X4 = GemType.WILD_X4;
    private final GemType UNKNOWN = GemType.UNKNOWN;
    private final GemType SKULL = GemType.SKULL;

    @Test
    public void valid_type_to_gem_conversion() {
        // Make sure all the gem types have a mapping to a gem without error.
        GemType[] types = GemType.values();
        for(int i = 0; i < types.length; i++){
            Gem gem = BoardUtils.getGemFromType(types[i]);
            assert gem.getDisplayGemType() == types[i];
        }
    }

    @Test
    public void simple_match() {
        GemType[][] initFrag = {{FIRE, FIRE, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN}};
        matchAndAssert(initFrag, expectedFinalFrag, 3, false);
    }

    @Test
    public void no_match() {
        GemType[][] initFrag = {{FIRE, FIRE, WATER, FIRE}};
        GemType[][] expectedFinalFrag = {{FIRE, FIRE, WATER, FIRE}};
        matchAndAssert(initFrag, expectedFinalFrag, 0, false);
    }

    @Test
    public void extra_turn_match() {
        GemType[][] initFrag = {{FIRE, FIRE, FIRE, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        matchAndAssert(initFrag, expectedFinalFrag, 4, true);
    }

    @Test
    public void wild_match() {
        GemType[][] initFrag = {{FIRE, FIRE, WILD_X2}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN}};
        Result result = matchAndAssert(initFrag, expectedFinalFrag, 6, false);
        ArrayList<ResultPair> resultPairs = result.getDisplayResults();
        assertEquals(resultPairs.size(), 1);
        assertEquals(resultPairs.get(0).numGems, 6);
        assertEquals(resultPairs.get(0).gemType, FIRE);
    }

    @Test
    public void wild_extra_turn_match() {
        GemType[][] initFrag = {{FIRE, FIRE, WILD_X2, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        matchAndAssert(initFrag, expectedFinalFrag, 8, true);
    }

    @Test
    public void multiple_wilds_add() {
        GemType[][] initFrag = {{FIRE, WILD_X2, WILD_X3, WILD_X4}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        matchAndAssert(initFrag, expectedFinalFrag, 36, true);
    }

    @Test
    public void wild_only_match_fails() {
        GemType[][] initFrag = {{WILD_X2, WILD_X2, WILD_X3, WILD_X4}};
        GemType[][] expectedFinalFrag = {{WILD_X2, WILD_X2, WILD_X3, WILD_X4}};
        matchAndAssert(initFrag, expectedFinalFrag, 0, false);
    }

    @Test
    public void wild_not_match_skulls() {
        GemType[][] initFrag = {{SKULL, SKULL, WILD_X2}};
        GemType[][] expectedFinalFrag = {{SKULL, SKULL, WILD_X2}};
        matchAndAssert(initFrag, expectedFinalFrag, 0, false);
    }

    @Test
    public void wild_matches_two_colors() {
        GemType[][] initFrag = {{WATER, WATER, WILD_X2, FIRE, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        Result result = matchAndAssert(initFrag, expectedFinalFrag, 12, false);
        ArrayList<ResultPair> resultPairs = result.getDisplayResults();
        assertEquals(2, resultPairs.size());
        assertEquals(6, resultPairs.get(0).numGems);
        assertEquals(FIRE, resultPairs.get(0).gemType);
        assertEquals(6, resultPairs.get(1).numGems);
        assertEquals(WATER, resultPairs.get(1).gemType);
    }



    public Result matchAndAssert(GemType[][] initFrag, GemType[][] expectedFinalFrag, int expectedMatched, boolean expectedExtraTurn) {
        GemType[][] initialBoard = TestUtils.generateFullBoardFromFragment(initFrag);
        Result result = BoardUtils.getResult(initialBoard, 0, 0, 0, 1);
        assertEquals(expectedMatched, result.totalMatched());
        assertEquals(expectedExtraTurn, result.getExtraTurn());
        assertTrue(TestUtils.validateGemBoardFromFragment(expectedFinalFrag, result.getFinalBoard()));
        return result;
    }

}