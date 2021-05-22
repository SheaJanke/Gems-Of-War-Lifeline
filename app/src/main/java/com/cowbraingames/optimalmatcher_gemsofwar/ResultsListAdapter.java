package com.cowbraingames.optimalmatcher_gemsofwar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
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
import java.util.Calendar;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ResultListViewHolder> {

    private final Context ct;
    private final ArrayList<Result> results;
    private final Board board;
    private final GridView gridView;
    private final Color[] colors = {
            Color.valueOf(105,105,105,255),
            Color.valueOf(64,64,64,255)
    };
    private final ArrayList<Pair<RecyclerView, Integer>> rows;

    public ResultsListAdapter(Context ct, ArrayList<Result> results, Board board, GridView gridView){
        rows = new ArrayList<>();
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ResultListViewHolder holder, final int position) {
        rows.add(Pair.create(holder.resultRow, position));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ct);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if(position%2 == 0){
            holder.resultRow.setBackgroundColor(0xFF404040);
        }else{
            holder.resultRow.setBackgroundColor(0xFF696969);
        }
        holder.resultRow.setLayoutManager(linearLayoutManager);
        ResultAdapter resultAdapter = new ResultAdapter(ct, results.get(position));
        holder.resultRow.setAdapter(resultAdapter);
        holder.resultRow.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            //click event has occurred
                            updateHighlighted(position);
                            for(int i = 0; i < rows.size(); i++){
                                if(rows.get(i).second%2 == 0){
                                    rows.get(i).first.setBackgroundResource(R.color.boardDark);
                                }else{
                                    rows.get(i).first.setBackgroundResource(R.color.boardLight);
                                }
                            }
                            if(position%2 == 0) {
                                holder.resultRow.setBackgroundResource(R.drawable.dark_border);
                            }else{
                                holder.resultRow.setBackgroundResource(R.drawable.light_border);
                            }
                        }
                    }
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
