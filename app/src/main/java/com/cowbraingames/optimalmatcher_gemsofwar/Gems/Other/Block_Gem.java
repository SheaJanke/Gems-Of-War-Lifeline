package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Block_Gem extends Gem {
    @Override
    public GemType getDisplayGemType() {
        return GemType.BLOCK;
    }

    @Override
    public boolean matches(MatchType type) {
        return false;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
        addResult.put(GemType.BLOCK, 1);
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }

    @Override
    public boolean canMove() {
        return false;
    }
}
