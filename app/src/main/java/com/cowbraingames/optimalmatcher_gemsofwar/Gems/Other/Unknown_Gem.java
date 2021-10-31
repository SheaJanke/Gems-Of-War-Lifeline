package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Unknown_Gem extends Gem {
    @Override
    public GemType getDisplayGemType() {
        return GemType.UNKNOWN;
    }

    @Override
    public boolean matches(MatchType type) {
        return false;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {

    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
