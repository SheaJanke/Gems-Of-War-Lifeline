package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board;

import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public abstract class Board {
    public abstract GemType[][] getGemTypes();
    public abstract void reportOrb(int row, int col);
}
