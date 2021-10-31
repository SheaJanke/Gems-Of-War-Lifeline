package com.cowbraingames.optimalmatcher_gemsofwar.Activities;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board.FinalActivityBoard;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGrid;
import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.ResultsList;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import java.util.Objects;

public class FinalBoardActivity extends AppCompatActivity {

    public static String FINAL_BOARD_KEY = "FINAL_BOARD";
    private BoardGrid boardGrid;
    private ResultsList resultsList;
    private GemType[][] finalBoard;

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

        boardGrid = new BoardGrid(this, findViewById(R.id.board));
        resultsList = new ResultsList(this, findViewById(R.id.results_list), boardGrid);
        findViewById(R.id.fab).setVisibility(View.INVISIBLE);

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
    }

    void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void fillBoardAndResults() {
        FinalActivityBoard board = new FinalActivityBoard(finalBoard);
        boardGrid.setBoard(board);
        resultsList.setResults(BoardUtils.getSortedResults(finalBoard));
    }
}
