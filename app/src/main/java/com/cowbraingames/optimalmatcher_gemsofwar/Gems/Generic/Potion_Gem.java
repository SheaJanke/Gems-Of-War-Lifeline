package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public abstract class Potion_Gem extends Gem {

    public abstract GemType getGemType();

    @Override
    public GemType getDisplayGemType() {
        return getGemType();
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(getGemType(), 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
