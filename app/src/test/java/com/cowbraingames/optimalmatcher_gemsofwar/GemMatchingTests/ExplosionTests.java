package com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests.GemMatchingUtils.matchAndAssert;

public class ExplosionTests {
    private final GemType FIRE = GemType.FIRE;
    private final GemType WATER = GemType.WATER;
    private final GemType UNKNOWN = GemType.UNKNOWN;
    private final GemType SKULL = GemType.SKULL;
    private final GemType SUPER_SKULL = GemType.SUPER_SKULL;
    private final GemType UBER_DOOM_SKULL = GemType.UBER_DOOM_SKULL;
    private final GemType NEXUS_STAR = GemType.NEXUS_STAR;
    private final GemType BLOCK = GemType.BLOCK;

    @Test
    public void super_skull_full_explosion() {
        GemType[][] initFrag = {
                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
                {BLOCK, FIRE, WATER, FIRE, BLOCK},
                {BLOCK, SKULL, SUPER_SKULL, SKULL, BLOCK},
                {BLOCK, FIRE, WATER, FIRE, BLOCK},
                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
        };
        GemType[][] expectedFinalFrag = {
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN},
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(SUPER_SKULL, 1);
            put(FIRE, 4);
            put(WATER, 2);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void uber_doom_skull_full_explosion() {
        GemType[][] initFrag = {
                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
                {BLOCK, BLOCK, WATER, FIRE, WATER, BLOCK, BLOCK},
                {BLOCK, WATER, FIRE, WATER, FIRE, WATER, BLOCK},
                {BLOCK, FIRE, SKULL, UBER_DOOM_SKULL, SKULL, FIRE, BLOCK},
                {BLOCK, WATER, FIRE, WATER, FIRE, WATER, BLOCK},
                {BLOCK, BLOCK, WATER, FIRE, WATER, BLOCK, BLOCK},
        };
        GemType[][] expectedFinalFrag = {
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, BLOCK, UNKNOWN, UNKNOWN, UNKNOWN, BLOCK},
                {BLOCK, BLOCK, UNKNOWN, UNKNOWN, UNKNOWN, BLOCK},

        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(UBER_DOOM_SKULL, 1);
            put(FIRE, 8);
            put(WATER, 10);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void nexus_star_full_explosion() {
        GemType[][] initFrag = {
                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, SKULL},
                {SKULL, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, SKULL, BLOCK},
                {BLOCK, SKULL, BLOCK, BLOCK, BLOCK, SKULL, BLOCK, BLOCK},
                {BLOCK, BLOCK, SKULL, BLOCK, SKULL, BLOCK, BLOCK, BLOCK},
                {BLOCK, BLOCK, WATER, NEXUS_STAR, WATER, BLOCK, BLOCK, BLOCK},
                {BLOCK, BLOCK, SKULL, BLOCK, SKULL, BLOCK, BLOCK, BLOCK},
                {BLOCK, SKULL, BLOCK, BLOCK, BLOCK, SKULL, BLOCK, BLOCK},
                {SKULL, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, SKULL, BLOCK},
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN, UNKNOWN, BLOCK, UNKNOWN, UNKNOWN, UNKNOWN, BLOCK},
                {BLOCK, BLOCK, UNKNOWN, BLOCK, UNKNOWN, BLOCK, BLOCK, BLOCK},

        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(WATER, 3);
            put(GemType.EARTH, 1);
            put(FIRE, 1);
            put(GemType.GROUND, 1);
            put(SKULL, 13);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void partial_explosion() {
        GemType[][] initFrag = {
                {SKULL, SUPER_SKULL, SKULL},
                {FIRE, WATER, FIRE}
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN, UNKNOWN},
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(SUPER_SKULL, 1);
            put(FIRE, 2);
            put(WATER, 1);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void explosion_causes_explosion() {
        GemType[][] initFrag = {
                {SKULL, SUPER_SKULL, SKULL},
                {BLOCK, BLOCK, SUPER_SKULL},
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {BLOCK, UNKNOWN, UNKNOWN, UNKNOWN},
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(SUPER_SKULL, 2);
            put(BLOCK, 7);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void explosion_after_match() {
        GemType[][] initFrag = {
                {FIRE, FIRE, FIRE},
                {SUPER_SKULL, SKULL, SKULL},
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN},
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(SUPER_SKULL, 1);
            put(BLOCK, 2);
            put(FIRE, 3);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
    }

    @Test
    public void explosion_keeps_extra_turn() {
        GemType[][] initFrag = {
                {FIRE, FIRE, FIRE, FIRE},
                {SUPER_SKULL, SKULL, SKULL},
        };
        GemType[][] expectedFinalFrag = {
                {UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN, UNKNOWN},
                {UNKNOWN, UNKNOWN},
        };
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(SKULL, 2);
            put(SUPER_SKULL, 1);
            put(BLOCK, 2);
            put(FIRE, 4);
        }};
        matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, true);
    }
}
