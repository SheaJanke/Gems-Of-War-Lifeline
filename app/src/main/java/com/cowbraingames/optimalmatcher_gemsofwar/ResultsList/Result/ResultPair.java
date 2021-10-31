package com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public class ResultPair {
    public final int numGems;
    public final GemType gemType;

    public ResultPair(int numGems, GemType gemType){
        this.numGems = numGems;
        this.gemType = gemType;
    }
}
