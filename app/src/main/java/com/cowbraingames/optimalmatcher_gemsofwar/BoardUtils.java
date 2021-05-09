package com.cowbraingames.optimalmatcher_gemsofwar;

public class BoardUtils {
    static final int BOARD_SIZE = 8;

    private static boolean isMatching(int orbType1, int orbType2){
        if(orbType1 == 8 || orbType2 == 8 || orbType1 == -1 || orbType2 == -1){
            return false;
        }
        return orbType1 == orbType2 || (orbType1 == 0 && orbType2 == 1) || (orbType1 == 1 && orbType2 == 0);
    }

    private static boolean hasHorizontalMatch(int[][] board, int r, int c){
        return c > 0 && c < BOARD_SIZE && isMatching(board[r][c-1], board[r][c])
                && isMatching(board[r][c], board[r][c+1]);
    }

    private static boolean hasVerticalMatch(int[][] board, int r, int c){
        return r > 0 && r < BOARD_SIZE && isMatching(board[r-1][c], board[r][c])
                && isMatching(board[r][c], board[r+1][c]);
    }

    public static void fillGaps(int[][] board){
        for(int i = 0; i < BOARD_SIZE; i++){
            int emptyPos = BOARD_SIZE-1;
            for(int j = BOARD_SIZE-1; j >= 0; j--){
                if(board[i][j] != -1){
                    if(j != emptyPos){
                        board[i][emptyPos] = board[i][j];
                        board[i][j] = -1;
                    }
                    emptyPos++;
                }
            }
        }
    }

    private static void matchOrb(int[][] board, boolean[][] matched, int r, int c){
        // Check if out of bounds.
        if(r < 0 || c < 0 || r >= BOARD_SIZE || c >= BOARD_SIZE){
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
        for(int i = 1; i < BOARD_SIZE-1; i++){
            for(int j = 1; j < BOARD_SIZE-1; j++){
                if(board[i][j] == 0 || board[i][j] == 1){
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

}
