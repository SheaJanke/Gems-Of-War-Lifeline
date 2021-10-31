package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Skull;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Skull_Gem extends Gem {

    @Override
    public GemType getDisplayGemType() {
        return GemType.SKULL;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.SKULL;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(getDisplayGemType(), 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
