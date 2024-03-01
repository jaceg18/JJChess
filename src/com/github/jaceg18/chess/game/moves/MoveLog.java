package com.github.jaceg18.chess.game.moves;

import com.github.jaceg18.chess.game.pieces.PieceType;

public class MoveLog {
     public final long fromPosition;
     public final long toPosition;
     public final PieceType movedPiece;
     public final PieceType capturedPiece; // Null if no capture
     public final int castlingRightsBeforeMove;

    public MoveLog(long fromPosition, long toPosition, PieceType movedPiece, PieceType capturedPiece, int castlingRightsBeforeMove) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.castlingRightsBeforeMove = castlingRightsBeforeMove;
    }
    @Override
    public String toString(){
        return "From : " + fromPosition + " To: " + toPosition;
    }
}
