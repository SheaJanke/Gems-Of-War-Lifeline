package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Normal_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Fire_Gem extends Normal_Gem {

    @Override
    public GemType getGemType() {
        return GemType.FIRE;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.FIRE;
    }
}
