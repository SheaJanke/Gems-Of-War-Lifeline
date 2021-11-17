package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

import java.util.ArrayList;
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
    public ArrayList<GemType> getResultGemTypes(MatchType matchType){
        return new ArrayList<GemType>(){{
            add(getDisplayGemType());
        }};
    }
    public abstract boolean matches(MatchType type);
    public abstract void explode(Gem[][] board, int r, int c);
    public abstract boolean canBeMultiplied();

    public void match(MatchType matchType, int multiplier) {
        if(!matches(matchType)){
            return;
        }
        isMatched = true;
        ArrayList<GemType> resultGemTypes = getResultGemTypes(matchType);
        for(GemType gemType : resultGemTypes) {
            if(canBeMultiplied()){
                addResult.put(gemType, multiplier);
            }else{
                addResult.put(gemType, 1);
            }
        }
    }

    public int getMultiplier() {
        return 0;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public boolean canMove() {
        return true;
    }

    public boolean createsInvalidFinalBoard() {
        return false;
    }

    public boolean createsValidMatch() { return true; }
}
