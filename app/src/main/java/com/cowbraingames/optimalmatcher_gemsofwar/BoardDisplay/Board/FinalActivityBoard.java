package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public class FinalActivityBoard extends Board {
    private final GemType[][] orbTypes;

    public FinalActivityBoard(GemType[][] orbTypes){
        this.orbTypes = orbTypes;
    }

    @Override
    public GemType[][] getGemTypes() {
        return orbTypes;
    }

    @Override
    public void reportOrb(int row, int col) {
        // Do nothing. Can't report on the final board.
    }
}
