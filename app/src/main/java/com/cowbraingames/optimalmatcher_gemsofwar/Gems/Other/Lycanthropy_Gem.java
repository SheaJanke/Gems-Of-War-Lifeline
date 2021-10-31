package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Lycanthropy_Gem extends Gem {
    @Override
    public GemType getDisplayGemType() {
        return GemType.LYCANTHROPY;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.DARK;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(GemType.LYCANTHROPY, 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
