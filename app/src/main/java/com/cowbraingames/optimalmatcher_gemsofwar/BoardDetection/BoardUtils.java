package com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Dark_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Earth_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Fire_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Ground_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Light_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Normal.Water_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other.Block_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other.Lycanthropy_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Other.Unknown_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion.Dark_Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion.Earth_Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion.Fire_Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion.Ground_Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion.Light_Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Potion.Water_Potion_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Skull.Skull_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Skull.Super_Skull_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Skull.Uber_Doom_Skull_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Wild.Wild_X2_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Wild.Wild_X3_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Wild.Wild_X4_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.MatchType;

import org.checkerframework.checker.units.qual.A;
import org.opencv.core.Mat;

import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class BoardUtils {
    static final int BOARD_SIZE = Constants.BOARD_SIZE;
    static final int MIN_MATCH_SIZE = 3;
    static final int MIN_EXTRA_TURN_MATCH = 4;

    public static class MatchedGroup {
        private int multiplier;
        private final ArrayList<Gem> groupGems;
        private boolean validMatch;

        public MatchedGroup() {
            multiplier = 0;
            groupGems = new ArrayList<>();
            validMatch = false;
        }

        public void addMultiplier(int addedMultiplier){
            multiplier += addedMultiplier;
        }

        public int getMultiplier() {
            return Math.max(1, multiplier);
        }

        public void addGem(Gem addedGem) {
            groupGems.add(addedGem);
        }

        public ArrayList<Gem> getGroupGems() {
            return groupGems;
        }

        public void setValidMatch(boolean validMatch){
            this.validMatch = validMatch;
        }

        public boolean getValidMatch(){
            return validMatch;
        }
    }

    public static ArrayList<Result> getSortedResults(GemType[][] board){
        ArrayList<Result> results = getResults(board);
        results.sort((result1, result2) -> {
            if(result1.getExtraTurn() != result2.getExtraTurn()){
                if(result1.getExtraTurn()){
                    return -1;
                }else{
                    return 1;
                }
            }
            if (result1.totalMatched() != result2.totalMatched()) {
                return result2.totalMatched() - result1.totalMatched();
            }
            int index1 = result1.getDisplayResults().get(0).gemType.ordinal();
            int index2 = result2.getDisplayResults().get(0).gemType.ordinal();
            return index1 - index2;
        });
        return results;
    }

    public static ArrayList<Result> getResults(GemType[][] board){
        ArrayList<Result> results = new ArrayList<>();
        Gem[][] gemBoard = getGemBoard(board);

        // Vertical swaps
        for(int i = 0; i < BOARD_SIZE-1; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(!(gemBoard[i][j].canMove() && gemBoard[i+1][j].canMove())){
                    continue;
                }
                Result result = getResult(board, i, j, i+1, j);
                if(result.totalMatched() > 0){
                    results.add(result);
                }
            }
        }

        // Horizontal swaps
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE - 1; j++){
                if(!(gemBoard[i][j].canMove() && gemBoard[i][j+1].canMove())){
                    continue;
                }
                Result result = getResult(board, i, j, i, j+1);
                if(result.totalMatched() > 0){
                    results.add(result);
                }
            }
        }

        return results;
    }

    public static Result getResult(GemType[][] board, int r1, int c1, int r2, int c2) {
        Gem[][] gemBoard = getSwappedGemBoard(board, r1, c1, r2, c2);
        Result result = new Result(r1, c1, r2, c2);
        while(matchBoard(gemBoard, result)){
            fillGaps(gemBoard);
        }
        GemType finalBoard[][] = new GemType[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j< BOARD_SIZE; j++){
                finalBoard[i][j] = gemBoard[i][j].getDisplayGemType();
            }
        }
        result.setFinalBoard(finalBoard);
        return result;
    }

    public static boolean matchBoard(Gem[][] gemBoard, Result result){
        final int NUM_MATCH_TYPES = MatchType.values().length;
        for(int k = 0; k < NUM_MATCH_TYPES; k++){
            MatchType matchType = MatchType.values()[k];
            boolean[][] matched = new boolean[BOARD_SIZE][BOARD_SIZE];

            for(int i = 0; i < BOARD_SIZE; i++){
                for(int j = 0; j < BOARD_SIZE; j++){
                    matchHorizontalIfPossible(gemBoard, matchType, matched, i, j);
                    matchVerticalIfPossible(gemBoard, matchType, matched, i, j);
                }
            }

            processMatchedGems(gemBoard, matched, result, matchType);
        }

        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(gemBoard[i][j].isMatched()){
                    gemBoard[i][j].explode(gemBoard, i, j);
                }
            }
        }


        boolean wasMatch = false;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(gemBoard[i][j].isMatched()){
                    wasMatch = true;
                    result.addMatched(gemBoard[i][j]);
                    if(gemBoard[i][j].createsInvalidFinalBoard()){
                        result.setInvalidFinalBoard(true);
                    }
                    gemBoard[i][j] = new Unknown_Gem();
                }
            }
        }
        return wasMatch;
    }

    private static void matchHorizontalIfPossible(Gem[][] gemBoard, MatchType matchType, boolean[][] matched, int r, int c){
        if(c + MIN_MATCH_SIZE > BOARD_SIZE){
            // Can't fit match here.
            return;
        }
        for(int i = 0; i < MIN_MATCH_SIZE; i++){
            if(!gemBoard[r][c + i].matches(matchType)){
                // Not a complete match.
                return;
            }
        }
        for(int i = 0; i < MIN_MATCH_SIZE; i++){
            matched[r][c + i] = true;
        }
    }

    private static void matchVerticalIfPossible(Gem[][] gemBoard, MatchType matchType, boolean[][] matched, int r, int c){
        if(r + MIN_MATCH_SIZE > BOARD_SIZE){
            // Can't fit match here.
            return;
        }
        for(int i = 0; i < MIN_MATCH_SIZE; i++){
            if(!gemBoard[r + i][c].matches(matchType)){
                // Not a complete match.
                return;
            }
        }
        for(int i = 0; i < MIN_MATCH_SIZE; i++){
            matched[r + i][c] = true;
        }
    }

    private static void processMatchedGems(Gem[][] gemBoard, boolean[][] matched, Result result, MatchType matchType) {
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(matched[i][j] && !visited[i][j]){
                    MatchedGroup matchedGroup = new MatchedGroup();
                    DFS(gemBoard, matched, visited, i, j, matchedGroup);
                    ArrayList<Gem> matchedGems = matchedGroup.getGroupGems();
                    if(!matchedGroup.getValidMatch()){
                        // Currently, this occurs if only wild gems are matched.
                        continue;
                    }
                    int matchedMultiplier = matchedGroup.getMultiplier();
                    int numMatched = matchedGems.size();
                    if(numMatched >= MIN_EXTRA_TURN_MATCH){
                        result.setExtraTurn(true);
                    }
                    for(int k = 0; k < numMatched; k++){
                        matchedGems.get(k).match(matchType, matchedMultiplier);
                    }
                }
            }
        }
    }


    private static void DFS(Gem[][] board, boolean[][] matched, boolean[][] visited, int r, int c, MatchedGroup matchedGroup){
        if(r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE || !matched[r][c] || visited[r][c]){
            return;
        }
        visited[r][c] = true;
        matchedGroup.addGem(board[r][c]);
        matchedGroup.addMultiplier(board[r][c].getMultiplier());
        if(board[r][c].createsValidMatch()){
            matchedGroup.setValidMatch(true);
        }
        DFS(board, matched, visited, r-1, c, matchedGroup);
        DFS(board, matched, visited, r+1, c, matchedGroup);
        DFS(board, matched, visited, r, c-1, matchedGroup);
        DFS(board, matched, visited, r, c+1, matchedGroup);
    }

    public static void fillGaps(Gem[][] board){
        for(int j = 0; j < BOARD_SIZE; j++){
            int swapIndex = BOARD_SIZE-1;
            for(int i = BOARD_SIZE-1; i >= 0; i--){
                if(!(board[i][j] instanceof Unknown_Gem)){
                    if(swapIndex != i){
                        Gem temp = board[swapIndex][j];
                        board[swapIndex][j] = board[i][j];
                        board[i][j] = temp;
                    }
                    swapIndex--;
                }
            }
        }
    }

    public static Gem[][] getSwappedGemBoard(GemType[][] board, int r1, int c1, int r2, int c2) {
        Gem[][] gemBoard = getGemBoard(board);
        Gem temp = gemBoard[r1][c1];
        gemBoard[r1][c1] = gemBoard[r2][c2];
        gemBoard[r2][c2] = temp;
        return gemBoard;
    }


    public static Gem[][] getGemBoard(GemType[][] board){
        Gem[][] gemBoard = new Gem[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                gemBoard[i][j] = getGemFromType(board[i][j]);
            }
        }
        return gemBoard;
    }

    public static Gem getGemFromType(GemType gemType){
        switch (gemType) {
            case FIRE:
                return new Fire_Gem();
            case FIRE_POTION:
                return new Fire_Potion_Gem();
            case WATER:
                return new Water_Gem();
            case WATER_POTION:
                return new Water_Potion_Gem();
            case EARTH:
                return new Earth_Gem();
            case EARTH_POTION:
                return new Earth_Potion_Gem();
            case LIGHT:
                return new Light_Gem();
            case LIGHT_POTION:
                return new Light_Potion_Gem();
            case DARK:
                return new Dark_Gem();
            case DARK_POTION:
                return new Dark_Potion_Gem();
            case LYCANTHROPY:
                return new Lycanthropy_Gem();
            case GROUND:
                return new Ground_Gem();
            case GROUND_POTION:
                return new Ground_Potion_Gem();
            case SKULL:
                return new Skull_Gem();
            case SUPER_SKULL:
                return new Super_Skull_Gem();
            case UBER_DOOM_SKULL:
                return new Uber_Doom_Skull_Gem();
            case WILD_X2:
                return new Wild_X2_Gem();
            case WILD_X3:
                return new Wild_X3_Gem();
            case WILD_X4:
                return new Wild_X4_Gem();
            case BLOCK:
                return new Block_Gem();
            case UNKNOWN:
                return new Unknown_Gem();
        }
        throw new InvalidParameterException("Gem Type Invalid: " + gemType);
    }

}
