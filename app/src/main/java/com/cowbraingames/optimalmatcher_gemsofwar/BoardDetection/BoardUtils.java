package com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection;

import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.Result.Result;

import java.util.ArrayList;

public class BoardUtils {
    static final int BOARD_SIZE = 8;
    static final int EMPTY = -1;
    static final int SKULL = 0;
    static final int SUPER_SKULL = 1;
    static final int FIRE = 2;
    static final int WATER = 3;
    static final int EARTH = 4;
    static final int GROUND = 5;
    static final int LIGHT = 6;
    static final int DARK = 7;
    static final int BLOCK = 8;
    static final int LYCANTHROPY = 9;

    private static boolean isMatching(int orbType1, int orbType2){
        if(orbType1 == BLOCK || orbType2 == BLOCK || orbType1 == EMPTY || orbType2 == EMPTY){
            return false;
        }
        return orbType1 == orbType2 ||
                (orbType1 == SKULL && orbType2 == SUPER_SKULL) ||
                (orbType1 == SUPER_SKULL && orbType2 == SKULL) ||
                (orbType1 == DARK && orbType2 == LYCANTHROPY) ||
                (orbType1 == LYCANTHROPY && orbType2 == DARK);
    }

    private static boolean canMove(int orbType){
        return orbType != BLOCK;
    }

    private static boolean hasHorizontalMatch(int[][] board, int r, int c){
        return c > 0 && c < BOARD_SIZE - 1 && isMatching(board[r][c-1], board[r][c])
                && isMatching(board[r][c], board[r][c+1]);
    }

    private static boolean hasVerticalMatch(int[][] board, int r, int c){
        return r > 0 && r < BOARD_SIZE - 1 && isMatching(board[r-1][c], board[r][c])
                && isMatching(board[r][c], board[r+1][c]);
    }

    public static void fillGaps(int[][] board){
        for(int j = 0; j < BOARD_SIZE; j++){
            int swapIndex = BOARD_SIZE-1;
            for(int i = BOARD_SIZE-1; i >= 0; i--){
                if(board[i][j] != EMPTY){
                    if(swapIndex != i){
                        board[swapIndex][j] = board[i][j];
                        board[i][j] = EMPTY;
                    }
                    swapIndex--;
                }
            }
        }
    }

    private static void matchOrb(int[][] board, boolean[][] matched, int r, int c){
        // Check if out of bounds.
        if(r < 0 || c < 0 || r >= BOARD_SIZE || c >= BOARD_SIZE || matched[r][c] || board[r][c] == EMPTY){
            return;
        }
        matched[r][c] = true;
    }

    private static void explodeOrb(int[][] board, boolean[][] matched, boolean[][] exploded, int r, int c){
        matchOrb(board, matched, r, c);
        // Check if out of bounds.
        if(r < 0 || c < 0 || r >= BOARD_SIZE || c >= BOARD_SIZE || exploded[r][c] || board[r][c] != SUPER_SKULL){
            return;
        }
        exploded[r][c] = true;
        for(int i = r-1; i <= r+1; i++){
            for(int j = c-1; j <= c+1; j++){
                explodeOrb(board, matched, exploded, i, j);
            }
        }
    }

    public static boolean matchBoard(int[][] board, Result result){
        boolean[][] matched = new boolean[BOARD_SIZE][BOARD_SIZE];
        boolean[][] exploded = new boolean[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(hasHorizontalMatch(board, i, j)){
                    matchOrb(board, matched, i, j-1);
                    matchOrb(board, matched, i, j);
                    matchOrb(board, matched, i, j+1);
                }
                if(hasVerticalMatch(board, i, j)){
                    matchOrb(board, matched, i-1, j);
                    matchOrb(board, matched, i, j);
                    matchOrb(board, matched, i+1, j);
                }
            }
        }
        if(hasExtraTurn(board, matched)){
            result.setExtraTurn(true);
        }
        // Do explosions after checking for extra turn
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(matched[i][j]){
                    explodeOrb(board, matched, exploded, i, j);
                }
            }
        }
        boolean wasMatch = false;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(matched[i][j] && board[i][j] != EMPTY){
                    wasMatch = true;
                    result.addMatched(board[i][j]);
                    board[i][j] = -1;
                }
            }
        }
        return wasMatch;
    }

    public static boolean hasExtraTurn(int[][] board, boolean[][] matched){
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        boolean extraTurn = false;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(matched[i][j]){
                    int thisMatch = DFS(board, matched, visited, i, j, board[i][j]);
                    if(thisMatch > 3){
                        extraTurn = true;
                    }
                }
            }
        }
        return extraTurn;
    }

    private static int DFS(int[][] board, boolean[][] matched, boolean[][] visited, int r, int c, int orbType){
        if(r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE || !matched[r][c] || visited[r][c] || !isMatching(board[r][c], orbType)){
            return 0;
        }
        visited[r][c] = true;
        int orbsMatched = 1;
        orbsMatched += DFS(board, matched, visited, r-1, c, orbType);
        orbsMatched += DFS(board, matched, visited, r+1, c, orbType);
        orbsMatched += DFS(board, matched, visited, r, c-1, orbType);
        orbsMatched += DFS(board, matched, visited, r, c+1, orbType);
        return orbsMatched;
    }

    public static ArrayList<Result> getResults(int[][] board){
        ArrayList<Result> results = new ArrayList<>();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(j+1 < BOARD_SIZE && canMove(board[i][j]) && canMove(board[i][j+1])){
                    int[][] boardCopy = copyBoard(board);
                    int temp = boardCopy[i][j];
                    boardCopy[i][j] = boardCopy[i][j+1];
                    boardCopy[i][j+1] = temp;
                    Result result = new Result(i, j, i, j+1, boardCopy);
                    while (matchBoard(boardCopy, result)){
                        fillGaps(boardCopy);
                    }
                    //result.setFinalBoard(boardCopy);
                    if(result.totalMatched() > 0){
                        results.add(result);
                    }
                }
                if(i+1 < BOARD_SIZE && canMove(board[i][j]) && canMove(board[i+1][j])){
                    int[][] boardCopy = copyBoard(board);
                    int temp = boardCopy[i][j];
                    boardCopy[i][j] = boardCopy[i+1][j];
                    boardCopy[i+1][j] = temp;
                    Result result = new Result(i, j, i+1, j, boardCopy);
                    while (matchBoard(boardCopy, result)){
                        fillGaps(boardCopy);
                    }
                    //result.setFinalBoard(boardCopy);
                    if(result.totalMatched() > 0){
                        results.add(result);
                    }
                }
            }
        }
        return results;
    }

    public static ArrayList<Result> getSortedResults(int[][] board){
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
            return result1.getDisplayResults().get(0).orbType - result2.getDisplayResults().get(0).orbType;
        });
        return results;
    }

    public static int[][] copyBoard(int[][] board){
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            System.arraycopy(board[i], 0, newBoard[i], 0, BOARD_SIZE);
        }
        return newBoard;
    }

}
