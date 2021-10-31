package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Water_Potion_Gem extends Potion_Gem {
    @Override
    public GemType getGemType() {
        return GemType.WATER_POTION;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.WATER;
    }
}
