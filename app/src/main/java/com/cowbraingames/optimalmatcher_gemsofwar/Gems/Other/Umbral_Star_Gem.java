package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

import java.util.ArrayList;

public class Umbral_Star_Gem extends Gem {
    private boolean isExploded;

    public Umbral_Star_Gem() {
        super();
        isExploded = false;
    }

    @Override
    public GemType getDisplayGemType() {
        return GemType.UMBRAL_STAR;
    }

    @Override
    public ArrayList<GemType> getResultGemTypes(MatchType matchType){
        assert matches(matchType);
        return new ArrayList<GemType>(){{
            add(GemType.LIGHT);
            add(GemType.DARK);
        }};
    }


    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.LIGHT || type == MatchType.DARK;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        if(isExploded){
            return;
        }
        isMatched = true;
        isExploded = true;

        addResult.put(GemType.LIGHT, 1);
        addResult.put(GemType.DARK, 1);

        for(int i = 1; i < Constants.BOARD_SIZE; i++){
            BoardUtils.explodeIfInBoard(board, r-i, c);
            BoardUtils.explodeIfInBoard(board, r+i, c);
        }
        for(int i = 1; i < Constants.BOARD_SIZE; i++){
            BoardUtils.explodeIfInBoard(board, r, c-i);
            BoardUtils.explodeIfInBoard(board, r, c+i);
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