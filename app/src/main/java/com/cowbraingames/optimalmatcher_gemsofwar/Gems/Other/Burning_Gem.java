package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Burning_Gem extends Gem {

    @Override
    public GemType getDisplayGemType() {
        return GemType.BURNING;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.FIRE;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(GemType.BURNING, 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
