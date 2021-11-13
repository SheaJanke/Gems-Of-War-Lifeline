package com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests.GemMatchingUtils.matchAndAssert;

public class BasicTests {
    private final GemType FIRE = GemType.FIRE;
    private final GemType WATER = GemType.WATER;
    private final GemType UNKNOWN = GemType.UNKNOWN;
    private final GemType BLOCK = GemType.BLOCK;

    @Test
    public void simple_match() {
        GemType[][] initFrag = {{FIRE, FIRE, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 3);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void no_match() {
        GemType[][] initFrag = {{FIRE, FIRE, WATER, FIRE}};
        GemType[][] expectedFinalFrag = {{FIRE, FIRE, WATER, FIRE}};
        Map<GemType, Integer> expectedMatches = new HashMap();
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void extra_turn_match() {
        GemType[][] initFrag = {{FIRE, FIRE, FIRE, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 4);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, true);
    }

    @Test
    public void cross_match() {
        GemType[][] initFrag = {
                {BLOCK, FIRE, BLOCK},
                {FIRE, FIRE, FIRE},
                {BLOCK, FIRE, BLOCK}
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, UNKNOWN, BLOCK},
                {BLOCK, UNKNOWN, BLOCK}
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 5);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, true);
    }
}
