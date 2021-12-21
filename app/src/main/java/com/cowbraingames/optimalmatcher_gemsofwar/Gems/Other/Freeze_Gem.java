package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Freeze_Gem extends Gem {
    @Override
    public GemType getDisplayGemType() {
        return GemType.FREEZE;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.WATER;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(GemType.FREEZE, 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
