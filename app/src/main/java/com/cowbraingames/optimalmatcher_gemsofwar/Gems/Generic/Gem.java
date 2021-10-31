package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

import java.util.HashMap;
import java.util.Map;

public abstract class Gem {
    protected final Map<GemType, Integer> addResult;
    protected boolean isMatched;

    public Gem() {
        addResult = new HashMap<>();
        isMatched = false;
    }

    public Map<GemType, Integer> getAddResult() {
        return addResult;
    }

    public abstract GemType getDisplayGemType();
    public GemType getResultGemType(MatchType matchType){
        return getDisplayGemType();
    }
    public abstract boolean matches(MatchType type);
    public abstract void explode(Gem[][] board, int r, int c);
    public abstract boolean canBeMultiplied();

    public void match(MatchType matchType, int multiplier) {
        if(!matches(matchType)){
            return;
        }
        isMatched = true;
        if(canBeMultiplied()){
            addResult.put(getResultGemType(matchType), multiplier);
        }else{
            addResult.put(getResultGemType(matchType), 1);
        }
    }

    public int getMultiplier() {
        return 0;
    }

    public boolean isMatched() {
        return isMatched;
    }
}
