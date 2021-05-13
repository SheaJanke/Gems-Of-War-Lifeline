package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ResultListViewHolder> {

    private final Context ct;
    private final ArrayList<Result> results;
    private final Board board;
    private final GridView gridView;

    public ResultsListAdapter(Context ct, ArrayList<Result> results, Board board, GridView gridView){
        this.ct = ct;
        this.results = results;
        this.board = board;
        this.gridView = gridView;
    }

    @NonNull
    @Override
    public ResultListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.result_row, parent, false);
        return new ResultListViewHolder(view);
    }

    private void updateHighlighted(int position){
        boolean[][] selected = new boolean[8][8];
        Result result = results.get(position);
        selected[result.r1][result.c1] = true;
        selected[result.r2][result.c2] = true;
        gridView.setAdapter(new ImageAdapter(ct, board.getGrid(), selected));
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultListViewHolder holder, final int position) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ct);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.resultRow.setLayoutManager(linearLayoutManager);
        ResultAdapter resultAdapter = new ResultAdapter(ct, results.get(position));
        holder.resultRow.setAdapter(resultAdapter);
        holder.resultRow.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN){
                    updateHighlighted(position);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ResultListViewHolder extends RecyclerView.ViewHolder{
        RecyclerView resultRow;

        public ResultListViewHolder(@NonNull View itemView) {
            super(itemView);
            resultRow = itemView.findViewById(R.id.result_row);
        }
    }
}
