package com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGridAdapter;
import com.cowbraingames.optimalmatcher_gemsofwar.R;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private final Context ct;
    private final ArrayList<ResultPair> displayResults;
    private final boolean hasExtraTurn;

    public ResultAdapter(Context ct, Result result){
        this.ct = ct;
        this.displayResults = result.getDisplayResults();
        this.hasExtraTurn = result.getExtraTurn();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.result_item, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        if(hasExtraTurn){
            if(position == 0){
                // Draw the extra turn
                holder.orbType.setImageResource(R.drawable.extra_turn);
                return;
            }
            position--;
        }
        ResultPair pair = displayResults.get(position);
        holder.orbType.setImageResource(BoardGridAdapter.orbID[pair.gemType]);
        holder.numOrbs.setText(String.valueOf(pair.numGems));
    }

    @Override
    public int getItemCount() {
        return displayResults.size() + (hasExtraTurn ? 1 : 0);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView numOrbs;
        ImageView orbType;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            numOrbs = itemView.findViewById(R.id.numOrbs);
            orbType = itemView.findViewById(R.id.orbType);
        }
    }
}
