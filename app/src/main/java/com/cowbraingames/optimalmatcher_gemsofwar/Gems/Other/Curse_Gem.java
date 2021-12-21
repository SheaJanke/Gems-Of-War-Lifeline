package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Curse_Gem extends Gem {
    @Override
    public GemType getDisplayGemType() {
        return GemType.CURSE;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.GROUND;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(GemType.CURSE, 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
