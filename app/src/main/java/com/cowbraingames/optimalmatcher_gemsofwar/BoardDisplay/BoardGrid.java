package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay;

import android.content.Context;
import android.widget.GridView;

import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;

public class BoardGrid {

    private final Context context;
    private final GridView board;
    private int[][] boardOrbs;

    public BoardGrid(Context context, GridView board) {
        this.context = context;
        this.board = board;
    }

    public void setBoardOrbs(int[][] boardOrbs) {
        this.boardOrbs = boardOrbs;
        setNoResultSelected();
    }

    private void setNoResultSelected() {
        updateAdapter(new boolean[8][8]);
    }

    public void setSelectedResult(Result result) {
        boolean[][] selected = new boolean[8][8];
        selected[result.r1][result.c1] = true;
        selected[result.r2][result.c2] = true;
        updateAdapter(selected);
    }

    private void updateAdapter(boolean[][] selected) {
        board.setAdapter(new BoardGridAdapter(context, boardOrbs, selected, board.getColumnWidth()));
        board.invalidateViews();
    }
}
