package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Skull;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Uber_Doom_Skull_Gem extends Gem {
    private boolean isExploded;

    public Uber_Doom_Skull_Gem() {
        super();
        isExploded = false;
    }

    @Override
    public GemType getDisplayGemType() {
        return GemType.UBER_DOOM_SKULL;
    }

    @Override
    public boolean matches(MatchType type) {
        return type == MatchType.SKULL;
    }

    @Override
    public void explode(Gem[][] board, int r, int c) {
        if(isExploded){
            return;
        }
        isMatched = true;
        isExploded = true;
        addResult.put(GemType.UBER_DOOM_SKULL, 1);
        for(int i = -2; i <= 2; i++){
            for(int j = -2; j <= 2; j++){
                if((Math.abs(i) == Math.abs(j)) && Math.abs(i) == 2){
                    continue;
                }
                int newR = r + i;
                int newC = c + j;
                if(newR >= 0 && newR< Constants.BOARD_SIZE && newC >= 0 && newC < Constants.BOARD_SIZE){
                    board[newR][newC].explode(board, newR, newC);
                }
            }
        }
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
