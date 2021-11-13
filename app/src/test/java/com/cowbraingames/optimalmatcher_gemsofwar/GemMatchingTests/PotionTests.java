package com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests;

import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.cowbraingames.optimalmatcher_gemsofwar.GemMatchingTests.GemMatchingUtils.matchAndAssert;

public class PotionTests {
    private final GemType FIRE = GemType.FIRE;
    private final GemType FIRE_POTION = GemType.FIRE_POTION;
    private final GemType BURNING = GemType.BURNING;
    private final GemType WATER = GemType.WATER;
    private final GemType WATER_POTION = GemType.WATER_POTION;
    private final GemType EARTH = GemType.EARTH;
    private final GemType EARTH_POTION = GemType.EARTH_POTION;
    private final GemType LIGHT = GemType.LIGHT;
    private final GemType LIGHT_POTION = GemType.LIGHT_POTION;
    private final GemType DARK = GemType.DARK;
    private final GemType DARK_POTION = GemType.DARK_POTION;
    private final GemType GROUND = GemType.GROUND;
    private final GemType GROUND_POTION = GemType.GROUND_POTION;
    private final GemType SKULL = GemType.SKULL;
    private final GemType UNKNOWN = GemType.UNKNOWN;

    @Test
    public void fire_potion_matches() {
        test_valid_potion_matches(FIRE_POTION, new GemType[]{FIRE, BURNING});
        test_invalid_potion_matches(FIRE_POTION, new GemType[]{WATER, EARTH, LIGHT, DARK, GROUND, SKULL});
    }

    @Test
    public void water_potion_match() {
        test_valid_potion_matches(WATER_POTION, new GemType[]{WATER});
        test_invalid_potion_matches(WATER_POTION, new GemType[]{FIRE, EARTH, LIGHT, DARK, GROUND, SKULL});
    }

    @Test
    public void earth_potion_match() {
        test_valid_potion_matches(EARTH_POTION, new GemType[]{EARTH});
        test_invalid_potion_matches(EARTH_POTION, new GemType[]{FIRE, WATER, LIGHT, DARK, GROUND, SKULL});
    }

    @Test
    public void light_potion_matches() {
        test_valid_potion_matches(LIGHT_POTION, new GemType[]{LIGHT});
        test_invalid_potion_matches(LIGHT_POTION, new GemType[]{WATER, EARTH, FIRE, DARK, GROUND, SKULL});
    }
    @Test
    public void dark_potion_matches() {
        test_valid_potion_matches(DARK_POTION, new GemType[]{DARK, GemType.LYCANTHROPY});
        test_invalid_potion_matches(DARK_POTION, new GemType[]{WATER, EARTH, LIGHT, FIRE, GROUND, SKULL});
    }
    @Test
    public void ground_potion_matches() {
        test_valid_potion_matches(GROUND_POTION, new GemType[]{GROUND});
        test_invalid_potion_matches(GROUND_POTION, new GemType[]{WATER, EARTH, LIGHT, DARK, FIRE, SKULL});
    }

    public void test_valid_potion_matches(GemType potionType, GemType[] otherTypes) {
        for (GemType otherType : otherTypes) {
            test_valid_potion_match(potionType, otherType);
        }
    }

    public void test_valid_potion_match(GemType potionType, GemType otherType) {
        GemType[][] initFrag = {{potionType, otherType, otherType}};
        GemType[][] expectedFinalFrag = {{UNKNOWN, UNKNOWN, UNKNOWN}};
        Map<GemType, Integer> expectedMatches = new HashMap<GemType, Integer>() {{
            put(otherType, 2);
            put(potionType, 1);
        }};
        Result result = matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
        assert result.getInvalidFinalBoard();
    }


    public void test_invalid_potion_matches(GemType potionType, GemType[] otherTypes) {
        for (GemType otherType : otherTypes) {
            test_invalid_potion_match(potionType, otherType);
        }
    }

    public void test_invalid_potion_match(GemType potionType, GemType otherType) {
        GemType[][] initFrag = {{potionType, otherType, otherType}};
        GemType[][] expectedFinalFrag = {{potionType, otherType, otherType}};
        Map<GemType, Integer> expectedMatches = new HashMap<>();
        Result result = matchAndAssert(initFrag, expectedFinalFrag, expectedMatches, false);
        assert !result.getInvalidFinalBoard();
    }

}
