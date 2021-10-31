package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay;

import android.content.Context;
import android.widget.GridView;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board.Board;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;

public class BoardGrid {

    private final Context context;
    private final GridView grid;
    private Board board;

    public BoardGrid(Context context, GridView grid) {
        this.context = context;
        this.grid = grid;
        addClickListener();
    }

    public void setBoard(Board board) {
        this.board = board;
        setNoResultSelected();
    }

    private void setNoResultSelected() {
        updateAdapter(new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE]);
    }

    public void setSelectedResult(Result result) {
        boolean[][] selected = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        selected[result.r1][result.c1] = true;
        selected[result.r2][result.c2] = true;
        updateAdapter(selected);
    }

    private void updateAdapter(boolean[][] selected) {
        grid.setAdapter(new BoardGridAdapter(context, board.getGemTypes(), selected, grid.getColumnWidth()));
        grid.invalidateViews();
    }

    private void addClickListener() {
        grid.setOnItemLongClickListener((adapterView, view, position, l) -> {
            int row = position / Constants.BOARD_SIZE;
            int col = position % Constants.BOARD_SIZE;
            board.reportOrb(row, col);
            return false;
        });
    }
}
