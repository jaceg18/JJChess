package com.github.jaceg18.chess.util;

public class BitboardUtils {
    public static boolean isSquareOnBoard(int square) {
        return square >= 0 && square < 64;
    }
    public static int bitboardToSquareIndex(long bitboard) {
        return Long.numberOfTrailingZeros(bitboard);
    }
    public static boolean isEdgeWrap(int currentSquare, int nextSquare) {
        int currentRow = currentSquare / 8;
        int nextRow = nextSquare / 8;
        int currentCol = currentSquare % 8;
        int nextCol = nextSquare % 8;

        return Math.abs(currentRow - nextRow) > 1 || Math.abs(currentCol - nextCol) > 1;
    }

}
