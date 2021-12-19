package com.cowbraingames.optimalmatcher_gemsofwar.Activities;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board.FinalActivityBoard;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGrid;
import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.ResultsList;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import java.util.Objects;

public class FinalBoardActivity extends AppCompatActivity {

    public static String FINAL_BOARD_KEY = "FINAL_BOARD";
    public static String FINAL_BOARD_VALID_KEY = "FINAL_BOARD_VALID";
    private TextView description;
    private GridView boardGridView;
    private BoardGrid boardGrid;
    private ResultsList resultsList;
    private RecyclerView resultsListView;
    private GemType[][] finalBoard;
    private boolean validFinalBoard;

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
        setContentView(R.layout.activity_main);

        getFinalBoardFromBundle();
        setupToolbar();

        description = findViewById(R.id.layout_description);
        boardGridView = findViewById(R.id.board);
        boardGrid = new BoardGrid(this, boardGridView);
        resultsListView = findViewById(R.id.results_list);
        resultsList = new ResultsList(this, resultsListView, boardGrid);

        setVisibility();

        // Required to make sure the boardGrid is ready to show the orbs.
        MessageQueue.IdleHandler handler = () -> {
            fillBoardAndResults();
            return false;
        };
        Looper.myQueue().addIdleHandler(handler);
    }

    void getFinalBoardFromBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        finalBoard = (GemType[][]) bundle.getSerializable(FINAL_BOARD_KEY);
        validFinalBoard = bundle.getBoolean(FINAL_BOARD_VALID_KEY);
    }

    void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    void setVisibility() {
        findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        if(validFinalBoard){
            boardGridView.setVisibility(View.VISIBLE);
            resultsListView.setVisibility(View.VISIBLE);
            description.setVisibility(View.INVISIBLE);
        }else{
            boardGridView.setVisibility(View.INVISIBLE);
            resultsListView.setVisibility(View.INVISIBLE);
            description.setVisibility(View.VISIBLE);
            description.setText(R.string.invalid_final_board_msg);
        }
    }

    private void fillBoardAndResults() {
        FinalActivityBoard board = new FinalActivityBoard(finalBoard);
        boardGrid.setBoard(board);
        resultsList.setResults(BoardUtils.getSortedResults(finalBoard));
    }
}
