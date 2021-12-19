package com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Result {
    static final int NUM_GEMS = GemType.values().length;
    private final int[] matchedGems;
    private GemType[][] finalBoard;
    private boolean extraTurn;
    private boolean invalidFinalBoard;
    public final int r1, c1, r2, c2;


    public Result(int r1, int c1, int r2, int c2){
        matchedGems = new int[NUM_GEMS];
        extraTurn = false;
        invalidFinalBoard = false;
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
    }

    public void addMatched(Gem gem){
        Map<GemType, Integer> addResult = gem.getAddResult();
        for(Map.Entry<GemType, Integer> entry: addResult.entrySet()){
            GemType gemType = entry.getKey();
            Integer numGems = entry.getValue();
            matchedGems[gemType.ordinal()] += numGems;
        }
    }

    public int totalMatched(){
        int total = 0;
        for(int i = 0; i < NUM_GEMS; i++){
            total += matchedGems[i];
        }
        return total;
    }

    public void setFinalBoard(GemType[][] finalBoard) {
        this.finalBoard = finalBoard;
    }

    public GemType[][] getFinalBoard(){
        return finalBoard;
    }

    public void setExtraTurn(boolean extraTurn){
        this.extraTurn = extraTurn;
    }

    public boolean getExtraTurn(){
        return extraTurn;
    }

    public void setInvalidFinalBoard(boolean invalidFinalBoard) {
        this.invalidFinalBoard = invalidFinalBoard;
    }

    public boolean getInvalidFinalBoard() {
        return invalidFinalBoard;
    }

    public ArrayList<ResultPair> getDisplayResults(){
        ArrayList<ResultPair> displayResults = new ArrayList<>();
        for(int i = 0; i < NUM_GEMS; i++){
            if(matchedGems[i] > 0){
                displayResults.add(new ResultPair(matchedGems[i], GemType.values()[i]));
            }
        }
        Collections.sort(displayResults, (r1, r2) -> {
            if (r1.numGems != r2.numGems) {
                return r2.numGems - r1.numGems;
            } else {
                return r1.gemType.ordinal() - r2.gemType.ordinal();
            }
        });
        return displayResults;
    }

}
