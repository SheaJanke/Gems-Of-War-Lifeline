package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private final Context ct;
    private final ArrayList<ResultPair> displayResults;

    public ResultAdapter(Context ct, Result result){
        this.ct = ct;
        this.displayResults = result.getDisplayResults();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.result_item, parent, false);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked3");
            }
        });
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultPair pair = displayResults.get(position);
        holder.orbType.setImageResource(ImageAdapter.orbID[pair.orbType]);
        holder.numOrbs.setText(String.valueOf(pair.numOrbs));
    }

    @Override
    public int getItemCount() {
        return displayResults.size();
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