package com.cowbraingames.optimalmatcher_gemsofwar;

import java.util.ArrayList;
import java.util.Arrays;

public class BoardUtils {
    static final int BOARD_SIZE = 8;

    private static boolean isMatching(int orbType1, int orbType2){
        if(orbType1 == 8 || orbType2 == 8 || orbType1 == -1 || orbType2 == -1){
            return false;
        }
        return orbType1 == orbType2 || (orbType1 == 0 && orbType2 == 1) || (orbType1 == 1 && orbType2 == 0);
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
                if(board[i][j] != -1){
                    if(swapIndex != i){
                        board[swapIndex][j] = board[i][j];
                        board[i][j] = -1;
                    }
                    swapIndex--;
                }
            }
        }
    }

    private static void matchOrb(int[][] board, boolean[][] matched, int r, int c){
        // Check if out of bounds.
        if(r < 0 || c < 0 || r >= BOARD_SIZE || c >= BOARD_SIZE || matched[r][c] || board[r][c] == -1){
            return;
        }
        boolean shouldExpload = board[r][c] == 1;
        matched[r][c] = true;
        if(shouldExpload){
            explodeOrb(board, matched, r, c);
        }
    }

    private static void explodeOrb(int[][] board, boolean[][] matched, int r, int c){
        for(int i = r-1; i <= r+1; i++){
            for(int j = c-1; j <= c+1; j++){
                matchOrb(board, matched, i, j);
            }
        }
    }

    public static boolean matchBoard(int[][] board, Result result){
        boolean[][] matched = new boolean[BOARD_SIZE][BOARD_SIZE];
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
        boolean wasMatch = false;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(matched[i][j] && board[i][j] != -1){
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
                if(j+1 < BOARD_SIZE){
                    int[][] boardCopy = copyBoard(board);
                    int temp = boardCopy[i][j];
                    boardCopy[i][j] = boardCopy[i][j+1];
                    boardCopy[i][j+1] = temp;
                    Result result = new Result(i, j, i, j+1);
                    while (matchBoard(boardCopy, result)){
                        fillGaps(boardCopy);
                    }
                    if(result.totalMatched() > 0){
                        results.add(result);
                    }
                }
                if(i+1 < BOARD_SIZE){
                    int[][] boardCopy = copyBoard(board);
                    int temp = boardCopy[i][j];
                    boardCopy[i][j] = boardCopy[i+1][j];
                    boardCopy[i+1][j] = temp;
                    Result result = new Result(i, j, i+1, j);
                    while (matchBoard(boardCopy, result)){
                        fillGaps(boardCopy);
                    }
                    if(result.totalMatched() > 0){
                        results.add(result);
                    }
                }
            }
        }
        return results;
    }

    public static int[][] copyBoard(int[][] board){
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

}
