package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Skull;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

public class Super_Skull_Gem extends Gem {
    private boolean isExploded;

    public Super_Skull_Gem() {
        super();
        isExploded = false;
    }

    @Override
    public GemType getDisplayGemType() {
        return GemType.SUPER_SKULL;
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
        addResult.put(GemType.SUPER_SKULL, 1);
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                int row = r + i;
                int col = c + j;
                if(row >= 0 && row < Constants.BOARD_SIZE && col >= 0 && col < Constants.BOARD_SIZE){
                    board[row][col].explode(board, row, col);
                }
            }
        }
    }

    @Override
    public boolean canBeMultiplied() {
        return false;
    }
}
