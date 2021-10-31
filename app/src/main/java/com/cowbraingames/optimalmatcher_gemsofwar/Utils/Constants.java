package com.cowbraingames.optimalmatcher_gemsofwar.Utils;

import com.cowbraingames.optimalmatcher_gemsofwar.R;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int BOARD_SIZE = 8;

    private static final Map<GemType, Integer> gemResource = new HashMap<GemType, Integer>() {{
       put(GemType.FIRE, R.drawable.fire);
       put(GemType.FIRE_POTION, R.drawable.fire_potion);
       put(GemType.WATER, R.drawable.water);
       put(GemType.WATER_POTION, R.drawable.water_potion);
       put(GemType.EARTH, R.drawable.earth);
       put(GemType.EARTH_POTION, R.drawable.earth_potion);
       put(GemType.LIGHT, R.drawable.light);
       put(GemType.LIGHT_POTION, R.drawable.light_potion);
       put(GemType.DARK, R.drawable.dark);
       put(GemType.DARK_POTION, R.drawable.dark_potion);
       put(GemType.LYCANTHROPY, R.drawable.lycanthropy);
       put(GemType.GROUND, R.drawable.ground);
       put(GemType.SKULL, R.drawable.skull);
       put(GemType.SUPER_SKULL, R.drawable.super_skull);
       put(GemType.UBER_DOOM_SKULL, R.drawable.uber_doom_skull);
       put(GemType.BLOCK, R.drawable.block);
       put(GemType.UNKNOWN, R.drawable.unknown);
       put(GemType.WILD_X2, R.drawable.wild_2);
       put(GemType.WILD_X3, R.drawable.wild_3);
       put(GemType.WILD_X4, R.drawable.wild_4);
    }};

    public static int getResource(GemType gemType) {
        return gemResource.get(gemType);
    }
}
