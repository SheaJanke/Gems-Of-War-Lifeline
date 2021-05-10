package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ResultListViewHolder> {

    private final Context ct;
    private final ArrayList<Result> results;
    private OnResultListener onResultListener;

    public ResultsListAdapter(Context ct, ArrayList<Result> results, OnResultListener onResultListener){
        this.ct = ct;
        this.results = results;
        this.onResultListener = onResultListener;
    }

    @NonNull
    @Override
    public ResultListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.result_row, parent, false);
        return new ResultListViewHolder(view, onResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultListViewHolder holder, final int position) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ct);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.resultRow.setLayoutManager(linearLayoutManager);
        ResultAdapter resultAdapter = new ResultAdapter(ct, results.get(position));
        holder.resultRow.setAdapter(resultAdapter);
        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                onResultListener.onResultClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ResultListViewHolder extends RecyclerView.ViewHolder{
        RecyclerView resultRow;
        ConstraintLayout rowLayout;
        OnResultListener onResultListener;

        public ResultListViewHolder(@NonNull View itemView, OnResultListener onResultListener) {
            super(itemView);
            resultRow = itemView.findViewById(R.id.result_row);
            rowLayout = itemView.findViewById(R.id.row_layout);

            this.onResultListener = onResultListener;
        }
    }

    public interface OnResultListener{
        void onResultClick(int position);
    }
}
