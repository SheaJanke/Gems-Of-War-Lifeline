package com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests.GemMatchingUtils.matchAndAssert;

public class WildTests {
    private final GemType FIRE = GemType.FIRE;
    private final GemType WATER = GemType.WATER;
    private final GemType WILD_X2 = GemType.WILD_X2;
    private final GemType WILD_X3 = GemType.WILD_X3;
    private final GemType WILD_X4 = GemType.WILD_X4;
    private final GemType UNKNOWN = GemType.UNKNOWN;
    private final GemType SKULL = GemType.SKULL;
    private final GemType SUPER_SKULL = GemType.SUPER_SKULL;

    @Test
    public void wild_match() {
        GemType[][] initFrag = {{FIRE, FIRE, WILD_X2}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 6);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void wild_extra_turn_match() {
        GemType[][] initFrag = {{FIRE, FIRE, WILD_X2, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 8);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, true);
    }

    @Test
    public void multiple_wilds_add() {
        GemType[][] initFrag = {{FIRE, WILD_X2, WILD_X3, WILD_X4}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 36);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, true);
    }

    @Test
    public void wild_only_match_fails() {
        GemType[][] initFrag = {{WILD_X2, WILD_X2, WILD_X3, WILD_X4}};
        GemType[][] expectedFinalFrag = {{WILD_X2, WILD_X2, WILD_X3, WILD_X4}};
        Map<GemType, Integer> expectedMatches = new HashMap();
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void wild_not_match_skulls() {
        GemType[][] initFrag = {{SKULL, SKULL, WILD_X2}};
        GemType[][] expectedFinalFrag = {{SKULL, SKULL, WILD_X2}};
        Map<GemType, Integer> expectedMatches = new HashMap();
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void wild_matches_two_colors() {
        GemType[][] initFrag = {{WATER, WATER, WILD_X2, FIRE, FIRE}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(FIRE, 6);
            put(WATER, 6);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void wild_exploded_does_nothing() {
        GemType[][] initFrag = {
                {SKULL, SUPER_SKULL, SKULL},
                {WILD_X2, WILD_X3, WILD_X4}
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN, UNKNOWN},
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(SUPER_SKULL, 1);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }
}
