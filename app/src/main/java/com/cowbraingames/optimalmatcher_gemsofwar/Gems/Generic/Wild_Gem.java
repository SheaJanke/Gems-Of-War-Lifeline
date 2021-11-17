package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

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
    public ArrayList<GemType> getResultGemTypes(MatchType matchType){
        assert matches(matchType);
        GemType resultGemType = getDisplayGemType();
        switch (matchType) {
            case FIRE:
                resultGemType = GemType.FIRE;
                break;
            case WATER:
                resultGemType = GemType.WATER;
                break;
            case EARTH:
                resultGemType = GemType.EARTH;
                break;
            case LIGHT:
                resultGemType = GemType.LIGHT;
                break;
            case DARK:
                resultGemType = GemType.DARK;
                break;
            case GROUND:
                resultGemType = GemType.GROUND;
                break;
        }
        GemType finalResultGemType = resultGemType;
        return new ArrayList<GemType>(){{
            add(finalResultGemType);
        }};
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
