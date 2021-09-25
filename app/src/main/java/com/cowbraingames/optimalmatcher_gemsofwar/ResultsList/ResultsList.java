package com.cowbraingames.optimalmatcher_gemsofwar.ResultsList;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cowbraingames.optimalmatcher_gemsofwar.FinalBoardActivity;
import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.RecyclerTouchListener;

import java.util.ArrayList;

public class ResultsList {
    private final Context context;
    private final RecyclerView resultsList;
    private ResultsListAdapter resultsListAdapter;
    private ArrayList<Result> results;

    public ResultsList(Context context, RecyclerView resultsList) {
        this.context = context;
        this.resultsList = resultsList;
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
                ArrayList<Pair<RecyclerView, Integer>> rows = resultsListAdapter.getRows();
                updateHighlighted(rows, position);
                System.out.println("onClick: " + position);
            }

            @Override
            public void onLongClick(View view, int position) {
                ArrayList<Pair<RecyclerView, Integer>> rows = resultsListAdapter.getRows();
                updateHighlighted(rows, position);
                Intent intent = new Intent(context, FinalBoardActivity.class);
                String flatFinalBoard = "";
                int[][] finalBoard = results.get(position).getFinalBoard();
                System.out.println("Position pressed: " + position);
                //System.out.println("Final board:");
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        //System.out.print(finalBoard[i][j] == -1 ? 1 : 0);
                        flatFinalBoard += finalBoard[i][j] + " ";
                    }
                    //System.out.println();
                }
                //System.out.println(flatFinalBoard);
                intent.putExtra("FLAT_FINAL_BOARD", flatFinalBoard);
                context.startActivity(intent);
            }
        }));
    }

    private void updateHighlighted(ArrayList<Pair<RecyclerView, Integer>> rows, int position){
        boolean[][] selected = new boolean[8][8];
        Result result = results.get(position);
        selected[result.r1][result.c1] = true;
        selected[result.r2][result.c2] = true;
        //gridView.setAdapter(new ImageAdapter(context, grid, selected, gridView.getColumnWidth()));
        for(int i = 0; i < rows.size(); i++){
            if(rows.get(i).second%2 == 0){
                rows.get(i).first.setBackgroundResource(R.color.boardDark);
            }else{
                rows.get(i).first.setBackgroundResource(R.color.boardLight);
            }
        }
        RecyclerView resultRow = rows.get(position).first;
        if(position%2 == 0) {
            resultRow.setBackgroundResource(R.drawable.dark_border);
        }else{
            resultRow.setBackgroundResource(R.drawable.light_border);
        }
    }
}
