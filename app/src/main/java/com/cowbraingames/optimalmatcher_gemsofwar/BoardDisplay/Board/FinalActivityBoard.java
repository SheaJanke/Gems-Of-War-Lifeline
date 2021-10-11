package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board;

public class FinalActivityBoard extends Board {
    private final int[][] orbTypes;

    public FinalActivityBoard(int[][] orbTypes){
        this.orbTypes = orbTypes;
    }

    @Override
    public int[][] getOrbTypes() {
        return orbTypes;
    }

    @Override
    public void reportOrb(int row, int col) {
        // Do nothing. Can't report on the final board.
    }
}
