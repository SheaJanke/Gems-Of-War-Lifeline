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
}
