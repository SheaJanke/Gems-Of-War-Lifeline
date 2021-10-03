package com.cowbraingames.optimalmatcher_gemsofwar.ResultsList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.ResultAdapter;

import java.util.ArrayList;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ResultListViewHolder> {

    private final Context ct;
    private final ArrayList<Result> results;
    private final ArrayList<Pair<RecyclerView, Integer>> rows;

    public ResultsListAdapter(Context ct, ArrayList<Result> results){
        rows = new ArrayList<>();
        this.ct = ct;
        this.results = results;
    }

    @NonNull
    @Override
    public ResultListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.result_row, parent, false);
        return new ResultListViewHolder(view);
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
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public ArrayList<Pair<RecyclerView, Integer>> getRows(){
        return rows;
    }

    public class ResultListViewHolder extends RecyclerView.ViewHolder {
        RecyclerView resultRow;

        public ResultListViewHolder(@NonNull View itemView) {
            super(itemView);
            resultRow = itemView.findViewById(R.id.result_row);
        }
    }
}
