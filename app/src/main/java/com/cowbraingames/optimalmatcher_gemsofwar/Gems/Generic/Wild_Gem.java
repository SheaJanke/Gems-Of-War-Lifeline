package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public abstract class Wild_Gem extends Gem{

    @Override
    public boolean matches(MatchType type) {
        return type != MatchType.SKULL;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        isMatched = true;
    }

    @Override
    public GemType getResultGemType(MatchType matchType){
        assert matches(matchType);
        switch (matchType) {
            case FIRE:
                return GemType.FIRE;
            case WATER:
                return GemType.WATER;
            case EARTH:
                return GemType.EARTH;
            case LIGHT:
                return GemType.LIGHT;
            case DARK:
                return GemType.DARK;
            case GROUND:
                return GemType.GROUND;
        }
        return getDisplayGemType();
    }

    @Override
    public boolean canBeMultiplied() {
        return true;
    }

    @Override
    public boolean createsValidMatch() {
        return false;
    }
}
