package com.cowbraingames.optimalmatcher_gemsofwar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class FinalBoardActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<Result> results;
    private int[][] grid;
    private RecyclerView resultsList;
    private ImageView testImg;
    private Context context;
    private ProgressBar spinner;

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        gridView = findViewById(R.id.board);
        resultsList = findViewById(R.id.results_list);
        testImg = findViewById(R.id.testImg);
        spinner = findViewById(R.id.spinner);
        findViewById(R.id.fab).setVisibility(View.INVISIBLE);
    }
}
