package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

import java.util.ArrayList;

public class Nexus_Star_Gem extends Gem {
    private boolean isExploded;

    public Nexus_Star_Gem() {
        super();
        isExploded = false;
    }

    @Override
    public GemType getDisplayGemType() {
        return GemType.NEXUS_STAR;
    }

    @Override
    public ArrayList<GemType> getResultGemTypes(MatchType matchType){
        assert matches(matchType);
        return new ArrayList<GemType>(){{
            add(GemType.FIRE);
            add(GemType.WATER);
            add(GemType.EARTH);
            add(GemType.GROUND);
        }};
    }


    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.GROUND || type == MatchType.EARTH || type == MatchType.FIRE || type == MatchType.WATER;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        if(isExploded){
            return;
        }
        isMatched = true;
        isExploded = true;

        addResult.put(GemType.GROUND, 1);
        addResult.put(GemType.EARTH, 1);
        addResult.put(GemType.WATER, 1);
        addResult.put(GemType.FIRE, 1);

        for(int i = 1; i < Constants.BOARD_SIZE; i++){
            BoardUtils.explodeIfInBoard(board, r-i, c-i);
            BoardUtils.explodeIfInBoard(board, r+i, c-i);
            BoardUtils.explodeIfInBoard(board, r-i, c+i);
            BoardUtils.explodeIfInBoard(board, r+i, c+i);
        }
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }

    @Override
    public boolean createsValidMatch() {
        return false;
    }
}
