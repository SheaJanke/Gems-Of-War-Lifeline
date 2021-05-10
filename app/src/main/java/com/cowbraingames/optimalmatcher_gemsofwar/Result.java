package com.cowbraingames.optimalmatcher_gemsofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Result {
    static final int ORB_TYPES = 9;
    private final int[] matched;
    private boolean extraTurn;
    public final int r1, c1, r2, c2;


    public Result(int r1, int c1, int r2, int c2){
        matched = new int[9];
        extraTurn = false;
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
    }

    public void addMatched(int orbIndex){
        matched[orbIndex]++;
    }

    public int totalMatched(){
        int total = 0;
        for(int i = 0; i < ORB_TYPES; i++){
            total += matched[i];
        }
        return total;
    }

    public void setExtraTurn(boolean extraTurn){
        this.extraTurn = extraTurn;
    }

    public boolean getExtraTurn(){
        return extraTurn;
    }

    public int[] getMatched(){
        return matched;
    }

    public ArrayList<ResultPair> getDisplayResults(){
        ArrayList<ResultPair> displayResults = new ArrayList<>();
        for(int i = 0; i < ORB_TYPES; i++){
            if(matched[i] > 0){
                displayResults.add(new ResultPair(matched[i], i));
            }
        }
        Collections.sort(displayResults, new Comparator<ResultPair>() {
            @Override
            public int compare(ResultPair r1, ResultPair r2) {
                if(r1.numOrbs != r2.numOrbs){
                    return r2.numOrbs - r1.numOrbs;
                }else{
                    return r1.orbType - r2.orbType;
                }
            }
        });
        return displayResults;
    }

}
