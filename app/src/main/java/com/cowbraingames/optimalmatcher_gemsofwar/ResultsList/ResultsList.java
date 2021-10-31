package com.cowbraingames.optimalmatcher_gemsofwar.ResultsList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGrid;
import com.cowbraingames.optimalmatcher_gemsofwar.Activities.FinalBoardActivity;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.RecyclerTouchListener;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;

import java.util.ArrayList;

public class ResultsList {
    private final Context context;
    private final RecyclerView resultsList;
    private final BoardGrid boardGrid;
    private ResultsListAdapter resultsListAdapter;
    private ArrayList<Result> results;

    public ResultsList(Context context, RecyclerView resultsList, BoardGrid boardGrid) {
        this.context = context;
        this.resultsList = resultsList;
        this.boardGrid = boardGrid;
        setResults(new ArrayList<>());
        addClickListener();
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
        resultsList.setLayoutManager(new LinearLayoutManager(context));
        resultsListAdapter = new ResultsListAdapter(context, results);
        resultsList.setAdapter(resultsListAdapter);
    }


    private void addClickListener() {
        resultsList.addOnItemTouchListener(new RecyclerTouchListener(context, resultsList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                updateHighlightedRow(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                updateHighlightedRow(position);
                GemType[][] finalBoard = results.get(position).getFinalBoard();
                startFinalBoardActivity(finalBoard);
            }
        }));
    }

    private void startFinalBoardActivity(GemType[][] finalBoard) {
        Intent intent = new Intent(context, FinalBoardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FinalBoardActivity.FINAL_BOARD_KEY, finalBoard);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void updateHighlightedRow(int highlightedPosition){
        Result result = results.get(highlightedPosition);
        boardGrid.setSelectedResult(result);
        resultsListAdapter.updateHighlightedRow(highlightedPosition);
    }
}
