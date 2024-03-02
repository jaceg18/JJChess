package com.github.jaceg18.chess.game.moves;

public class Move {
    long from;
    long to;
    boolean isCapture;
    boolean isWhite;
    boolean isCastle;
    public Move(long from, long to, boolean isCapture, boolean isWhite, boolean isCastle){
        this.from = from;
        this.to = to;
        this.isCapture = isCapture;
        this.isWhite = isWhite;
        this.isCastle = isCastle;
    }

    public long getFrom(){
        return from;
    }

    public long getTo(){
        return to;
    }
    public boolean isCapture(){
        return isCapture;
    }
    public boolean isWhite(){
        return isWhite;
    }
    public boolean isCastle(){
        return isCastle;
    }

    @Override
    public String toString(){
        return "From: " + from + " To:" + to;
    }
    public String toNotation() {
        // Convert the bitboard positions to algebraic notation
        return bitToAlgebraic(from) + bitToAlgebraic(to);
    }

    private static String bitToAlgebraic(long bit) {
        // Find the index (0-63) of the bit that is set
        int index = Long.numberOfTrailingZeros(bit);
        // Calculate row and column from index
        int row = index / 8;
        int column = index % 8;
        // Convert to algebraic notation
        return "" + (char)('a' + column) + (row + 1);
    }
}
