package com.github.jaceg18.chess.engine;

import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.game.moves.Move;
import com.github.jaceg18.chess.util.BitboardUtils;

import java.util.Arrays;
import java.util.List;


public class Evaluator {

    // Example evaluation function, not yet implemented.
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 300;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 0;
    private static final int CASTLE_VALUE = 225;
    private static final int MOBILITY_BONUS = 1;
    private static final int CHECKMATE_VALUE = Integer.MAX_VALUE;
    private static final int STALEMATE_VALUE = 0;
    private static final int CHECK_PUNISHMENT = 200;
    private static final int PROTECTING_PAWN_SCORE = 20;

    private static final int[] BLACK_PAWN_TABLE = {
            0,  0,  0,   0,   0,   0,  0, 0,
            25, 10, 15, -10, -10, 15, 10, 25,
            5, -5, -10,  5,   5,  -10, -5, 5,
            0, 0, 0, 20, 20, 0, 0, 0,
            5, 5, 10, 25, 25, 10, 5, 5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            500, 500, 500, 500, 500, 500, 500, 500
    };

    private static final int[] WHITE_PAWN_TABLE = flipTable(BLACK_PAWN_TABLE);

    public static int evaluate(Bitboard bitboard, boolean isWhite) {
        boolean currentSideKingInCheck = bitboard.isSquareAttacked(isWhite ? (int) bitboard.getWhiteKing() : (int) bitboard.getBlackKing(), isWhite);
        int whiteKingLocation = BitboardUtils.bitboardToSquareIndex(bitboard.getWhiteKing());
        int blackKingLocation = BitboardUtils.bitboardToSquareIndex(bitboard.getBlackKing());
        boolean isOutOfMoves = bitboard.getMoveList().stream().filter(Move::isWhite).toList().isEmpty();

       if (currentSideKingInCheck && isOutOfMoves) {
           return -CHECKMATE_VALUE;
       } else if (isOutOfMoves){
           return 0;
       }

        return getMaterialScore(bitboard, isWhite) + getKingProtectionScore(bitboard, isWhite, whiteKingLocation, blackKingLocation) + getPawnTableScore(bitboard, isWhite);// + mobilityBonus;
    }

    private static int getMaterialScore(Bitboard bitboard, boolean isWhite) {
        int whitePawnCount = Long.bitCount(bitboard.getWhitePawns());
        int whiteKnightCount = Long.bitCount(bitboard.getWhiteKnights());
        int whiteBishopCount = Long.bitCount(bitboard.getWhiteBishops());
        int whiteRookCount = Long.bitCount(bitboard.getWhiteRooks());
        int whiteQueenCount = Long.bitCount(bitboard.getWhiteQueens());

        int blackPawnCount = Long.bitCount(bitboard.getBlackPawns());
        int blackKnightCount = Long.bitCount(bitboard.getBlackKnights());
        int blackBishopCount = Long.bitCount(bitboard.getBlackBishops());
        int blackRookCount = Long.bitCount(bitboard.getBlackRooks());
        int blackQueenCount = Long.bitCount(bitboard.getBlackQueens());

        int whiteMaterialScore = PAWN_VALUE * whitePawnCount +
                KNIGHT_VALUE * whiteKnightCount +
                BISHOP_VALUE * whiteBishopCount +
                ROOK_VALUE * whiteRookCount +
                QUEEN_VALUE * whiteQueenCount +
                KING_VALUE;

        int blackMaterialScore = PAWN_VALUE * blackPawnCount +
                KNIGHT_VALUE * blackKnightCount +
                BISHOP_VALUE * blackBishopCount +
                ROOK_VALUE * blackRookCount +
                QUEEN_VALUE * blackQueenCount +
                KING_VALUE;

        return isWhite ? whiteMaterialScore - blackMaterialScore : blackMaterialScore - whiteMaterialScore;
    }

    private static int getKingProtectionScore(Bitboard bitboard, boolean isWhite, int whiteKingLocation, int blackKingLocation){
        int score = 0;
        score += getCastleScore(bitboard, isWhite) + kingLocationScore(bitboard, isWhite, whiteKingLocation, blackKingLocation) + getKingBlockScore(bitboard, isWhite, whiteKingLocation, blackKingLocation);
        return score;
    }

    private static int getPawnTableScore(Bitboard bitboard, boolean isWhite) {
        long pawns = isWhite ? bitboard.getWhitePawns() : bitboard.getBlackPawns();
        int[] pawnTable = isWhite ? WHITE_PAWN_TABLE : BLACK_PAWN_TABLE;
        int score = 0;

        for (int i = 0; i < 64; i++) {
            if ((pawns & (1L << i)) != 0) {
                score += pawnTable[i];
            }
        }

        return score;
    }

    private static int getKingBlockScore(Bitboard bitboard, boolean isWhite, int whiteKingLocation, int blackKingLocation){
        int score = 0;
        int kingLocation = isWhite ? whiteKingLocation : blackKingLocation;
        long pawns = isWhite ? bitboard.getWhitePawns() : bitboard.getBlackPawns();
        int dir = isWhite ? -8 : 8;

        int[] frontSquares = {kingLocation + dir, kingLocation + dir - 1, kingLocation + dir + 1};

        for (int frontSquare : frontSquares) {
            if (frontSquare >= 0 && frontSquare < 64) {
                if ((pawns & (1L << frontSquare)) != 0) {
                    score += PROTECTING_PAWN_SCORE;
                }
            }
        }

        return score;
    }

    private static int getCastleScore(Bitboard bitboard, boolean isWhite) {
        int whiteCastleScore = bitboard.isWhiteCastled() ? CASTLE_VALUE : 0;
        int blackCastleScore = bitboard.isBlackCastled() ? CASTLE_VALUE : 0;

        return isWhite ? whiteCastleScore - blackCastleScore : blackCastleScore - whiteCastleScore;
    }


    private static int getMobilityBonus(boolean isWhite, int movesSize) {
        return movesSize * MOBILITY_BONUS;
    }

    private static int kingLocationScore(Bitboard bitboard, boolean isWhite, int whiteKingLocation, int blackKingLocation) {
        int blackScore = 0;
        int whiteScore = 0;

        if (blackKingLocation == 0 || blackKingLocation == 1 || blackKingLocation == 2 || blackKingLocation == 3 || blackKingLocation == 4 || blackKingLocation == 5 || blackKingLocation == 6 || blackKingLocation == 7)
            blackScore += 500;

        if (blackKingLocation == 8 || blackKingLocation == 9 || blackKingLocation == 10 || blackKingLocation == 11 || blackKingLocation == 12 || blackKingLocation == 13 || blackKingLocation == 14 || blackKingLocation == 15)
            blackScore -= 500;

        if (whiteKingLocation == 56 || whiteKingLocation == 57 || whiteKingLocation == 58 || whiteKingLocation == 59 || whiteKingLocation == 60 || whiteKingLocation == 61 || whiteKingLocation == 62 || whiteKingLocation == 63)
            whiteScore += 500;
        else {
            whiteScore -= 500;
        }

        return isWhite ? whiteScore : blackScore;
    }

    public static boolean isCheckmate(Bitboard bitboard, boolean isWhite, boolean kingInCheck, List<Move> moves) {
        return moves.isEmpty() && kingInCheck;
    }

    public static boolean isStalemate(Bitboard bitboard, boolean isWhite, boolean kingInCheck, List<Move> moves) {
        return moves.isEmpty() && !kingInCheck;
    }

    private static int[] flipTable(int[] table) {
        int[] flippedTable = new int[table.length];
        for (int i = 0; i < table.length; i++) {
            flippedTable[i] = table[table.length - i - 1];
        }
        return flippedTable;
    }

}
