package com.github.jaceg18.chess.game.moves;

import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.game.moves.strategy.OrderingStrategy;
import com.github.jaceg18.chess.game.pieces.PieceType;
import com.github.jaceg18.chess.util.BitboardUtils;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    // En passant and promotions aren't implemented yet.
    private static final int[] KING_DIRECTIONS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final int[] KNIGHT_DIRECTIONS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private OrderingStrategy orderingStrategy; // not yet implemented
    private final Bitboard bitboard;
    public MoveGenerator(Bitboard bitboard, OrderingStrategy orderingStrategy){
        this.orderingStrategy = orderingStrategy;
        this.bitboard = bitboard;
    }
    /**
     * Generates all piece moves
     * @param isWhite Are we looking for white or black moves?
     * @return A list of moves
     */
    public List<Move> generateMoves(boolean isWhite) {
        bitboard.clearMoveList();

        generatePawnMoves(isWhite);
        generateKnightMoves(isWhite);
        generateBishopMoves(isWhite);
        generateRookMoves(isWhite);
        generateQueenMoves(isWhite);
        generateKingMoves(isWhite);

        return orderingStrategy.orderMoves(bitboard.getMoveList()); // Return a copy of the move list
    }

    /**
     * Generates pawn moves, temporarily leaving out promotions and en passant
     * @param isWhite Are we looking for white or black pawn moves?
     */
    private void generatePawnMoves(boolean isWhite) {
        long pawns = isWhite ? bitboard.getWhitePawns() : bitboard.getBlackPawns();
        long emptySquares = ~bitboard.getOccupiedSquares();
        long singleMoves, doubleMoves;
        int direction = isWhite ? 8 : -8;
        long doubleMoveRowMask = isWhite ? 0x0000FF0000000000L : 0x0000000000FF0000L;

        // Single step moves
        singleMoves = isWhite ? (pawns >>> 8) & emptySquares : (pawns << 8) & emptySquares;

        // Double step moves from the second rank (for white) or seventh rank (for black)
        doubleMoves = isWhite ? ((singleMoves & doubleMoveRowMask) >>> 8) & emptySquares :
                ((singleMoves & doubleMoveRowMask) << 8) & emptySquares;

        // Add moves to the list
        bitboard.addMoves(singleMoves, direction, isWhite);
        bitboard.addMoves(doubleMoves, direction * 2, isWhite);

        // Captures to the left and right
        generatePawnCaptures(pawns, isWhite);
    }

    /**
     * Generates the capture moves for pawns
     * @param pawns Pawns bitboard
     * @param isWhite Are the pawns white?
     */
    private void generatePawnCaptures(long pawns, boolean isWhite) {
        long opponentPieces = isWhite ? bitboard.getBlackPieces() : bitboard.getWhitePieces();
        for (int fromSquare = 0; fromSquare < 64; fromSquare++) {
            if ((pawns & (1L << fromSquare)) != 0) {
                int leftCaptureSquare = fromSquare + (isWhite ? -9 : 7);
                int rightCaptureSquare = fromSquare + (isWhite ? -7 : 9);

                if (BitboardUtils.isSquareOnBoard(leftCaptureSquare) && !BitboardUtils.isEdgeWrap(fromSquare, leftCaptureSquare) && (opponentPieces & (1L << leftCaptureSquare)) != 0) {
                    bitboard.addMove(fromSquare, leftCaptureSquare, isWhite);
                }

                // Check for right capture
                if (BitboardUtils.isSquareOnBoard(rightCaptureSquare) && !BitboardUtils.isEdgeWrap(fromSquare, rightCaptureSquare) && (opponentPieces & (1L << rightCaptureSquare)) != 0) {
                    bitboard.addMove(fromSquare, rightCaptureSquare, isWhite);
                }
            }
        }
    }

    /**
     * Generates knight moves
     * @param isWhite The given color to generate knight moves for
     */
    private void generateKnightMoves(boolean isWhite) {
        long knights = isWhite ? bitboard.getWhiteKnights() : bitboard.getBlackKnights();
        long potentialMoves, fromBitboard;
        int fromSquare;

        long whitePieces = bitboard.getWhitePieces();
        long blackPieces = bitboard.getBlackPieces();

        while (knights != 0) {
            fromSquare = Long.numberOfTrailingZeros(knights);
            fromBitboard = 1L << fromSquare;
            potentialMoves = knightMoveTargets(fromSquare, isWhite, whitePieces, blackPieces) & ~(isWhite ? whitePieces : blackPieces);

            bitboard.addMovesFromBitboard(BitboardUtils.bitboardToSquareIndex(fromBitboard), potentialMoves, isWhite);

            // Clear this knight from the knights bitboard.
            knights &= ~fromBitboard;
        }
    }

    /**
     * Generates bishop moves
     * @param isWhite Are we looking for white or black moves?
     */
    private void generateBishopMoves(boolean isWhite) {
        long bishops = isWhite ? bitboard.getWhiteBishops() : bitboard.getBlackBishops();
        long potentialMoves, fromBitboard;
        int fromSquare;

        while (bishops != 0) {
            fromSquare = Long.numberOfTrailingZeros(bishops);
            fromBitboard = 1L << fromSquare;
            potentialMoves = slidingMoveTargets(fromSquare, isWhite, true) & ~(isWhite ? bitboard.getWhitePieces() : bitboard.getBlackPieces());

            bitboard.addMovesFromBitboard(BitboardUtils.bitboardToSquareIndex(fromBitboard), potentialMoves, isWhite);

            // Clear this bishop from the bishops bitboard.
            bishops &= ~fromBitboard;
        }
    }
    /**
     * Generates rook moves
     * @param isWhite Are we looking for white or black moves?
     */
    private void generateRookMoves(boolean isWhite) {
        long rooks = isWhite ? bitboard.getWhiteRooks() : bitboard.getBlackRooks();
        long potentialMoves, fromBitboard;
        int fromSquare;

        while (rooks != 0) {
            fromSquare = Long.numberOfTrailingZeros(rooks);
            fromBitboard = 1L << fromSquare;
            potentialMoves = slidingMoveTargets(fromSquare, isWhite, false) & ~(isWhite ? bitboard.getWhitePieces() : bitboard.getBlackPieces());

            bitboard.addMovesFromBitboard(BitboardUtils.bitboardToSquareIndex(fromBitboard), potentialMoves, isWhite);

            // Clear this rook from the rooks bitboard.
            rooks &= ~fromBitboard;
        }
    }
    /**
     * Generates queen moves
     * @param isWhite Are we looking for white or black moves?
     */
    private void generateQueenMoves(boolean isWhite) {
        long queens = isWhite ? bitboard.getWhiteQueens() : bitboard.getBlackQueens();
        long potentialMoves, fromBitboard;
        int fromSquare;

        long whitePieces = bitboard.getWhitePieces();
        long blackPieces = bitboard.getBlackPieces();

        while (queens != 0) {
            fromSquare = Long.numberOfTrailingZeros(queens);
            fromBitboard = 1L << fromSquare;
            // Combine rook and bishop moves for the queen
            potentialMoves = (slidingMoveTargets(fromSquare, isWhite, true) & ~(isWhite ? whitePieces : blackPieces) |
                    slidingMoveTargets(fromSquare, isWhite, false)) &
                    ~(isWhite ? whitePieces : blackPieces);

            bitboard.addMovesFromBitboard(BitboardUtils.bitboardToSquareIndex(fromBitboard), potentialMoves, isWhite);

            // Clear this queen from the queens bitboard.
            queens &= ~fromBitboard;
        }
    }
    /**
     * Generates king moves
     * @param isWhite Are we looking for white or black moves?
     */
    private void generateKingMoves(boolean isWhite) {
        long king = isWhite ? bitboard.getWhiteKing() : bitboard.getBlackKing();
        int fromSquare = Long.numberOfTrailingZeros(king);

        long whitePieces = bitboard.getWhitePieces();
        long blackPieces = bitboard.getBlackPieces();

        // Generate normal king moves
        long potentialMoves = kingMoveTargets(fromSquare, isWhite, whitePieces, blackPieces) & ~(isWhite ? whitePieces : blackPieces);

        // Add normal king moves to the move list
        bitboard.addMovesFromBitboard(BitboardUtils.bitboardToSquareIndex((1L << fromSquare)), potentialMoves, isWhite);

        long castlingRights = bitboard.getCastlingRights();

        // Adjusted castling logic considering the board setup
        if (isWhite) {
            // White's castling logic
            // White king-side castling check corrected
            if ((castlingRights & 0b0100) != 0 && // Check if white king-side castling is available
                    ((whitePieces | blackPieces) & (1L << 61 | 1L << 62)) == 0 && // Correctly checks if squares between king and rook are empty
                    !bitboard.isSquareAttacked(60, true) && !bitboard.isSquareAttacked(61, true) && !bitboard.isSquareAttacked(62, true)) { // Check if king passes through or ends in check
                // Add white king-side castling move
                bitboard.addMove(fromSquare, 62, true);
            }

            if ((castlingRights & 0b1000) != 0 && // White queen-side castling is available
                    ((whitePieces | blackPieces) & (1L << 57 | 1L << 58 | 1L << 59)) == 0 && // Squares between king and rook are empty
                    !bitboard.isSquareAttacked(60, true) && !bitboard.isSquareAttacked(59, true) && !bitboard.isSquareAttacked(58, true)) { // King does not pass through or end in check
                // Add white queen-side castling move
                bitboard.addMove(fromSquare, 58, true);
            }
        } else {
            // Black's castling logic
            if ((castlingRights & 0b0010) != 0 && // Black king-side castling is available
                    ((whitePieces | blackPieces) & (1L << 5 | 1L << 6)) == 0 && // Squares between king and rook are empty
                    !bitboard.isSquareAttacked(4, false) && !bitboard.isSquareAttacked(5, false) && !bitboard.isSquareAttacked(6, false)) { // King does not pass through or end in check
                // Add black king-side castling move
                bitboard.addMove(fromSquare, 6, false);
            }
            if ((castlingRights & 0b0001) != 0 && // Black queen-side castling is available
                    ((whitePieces | blackPieces) & (1L << 1 | 1L << 2 | 1L << 3)) == 0 && // Squares between king and rook are empty
                    !bitboard.isSquareAttacked(4, false) && !bitboard.isSquareAttacked(3, false) && !bitboard.isSquareAttacked(2, false)) { // King does not pass through or end in check
                // Add black queen-side castling move
                bitboard.addMove(fromSquare, 2, false);
            }
        }
    }

    /**
     * Helper method too get sliding moves
     * @param square The current square
     * @param isWhite Is the piece white?
     * @param isBishop Is this a bishop or a rook?
     * @return A long containing the sliding moves
     */
    private long slidingMoveTargets(int square, boolean isWhite, boolean isBishop) {
        long moves = 0L;
        int[] directions = isBishop ? new int[]{-9, -7, 7, 9} : new int[]{-8, -1, 1, 8};
        for (int direction : directions) {
            int currentSquare = square;
            while (true) {
                int nextSquare = currentSquare + direction;

                if (!BitboardUtils.isSquareOnBoard(nextSquare) || BitboardUtils.isEdgeWrap(currentSquare, nextSquare)) {
                    break;
                }

                moves |= 1L << nextSquare;

                if ((bitboard.getOccupiedSquares() & (1L << nextSquare)) != 0) {
                    break;
                }

                currentSquare = nextSquare;
            }
        }

        return moves;
    }

    /**
     * A slow method that generates moves for a given piece.
     * This is only used for highlighting legal move squares in the GamePanel
     * @param square The square we are checking
     * @param isWhite Are we looking for white or black moves?
     * @return A list of moves from a given piece.
     */
    public List<Move> generateMovesForPieceAtSquare(int square, boolean isWhite) {
        List<Move> specificMoves = new ArrayList<>();
        PieceType pieceType = bitboard.getPieceAtSquare(square, isWhite);

        List<Move> originalMoveList = new ArrayList<>(bitboard.getMoveList());

        bitboard.clearMoveList();

        switch (pieceType) {
            case WHITE_PAWN, BLACK_PAWN -> generatePawnMoves(isWhite);
            case WHITE_KNIGHT, BLACK_KNIGHT -> generateKnightMoves(isWhite);
            case WHITE_BISHOP, BLACK_BISHOP -> generateBishopMoves(isWhite);
            case WHITE_ROOK, BLACK_ROOK -> generateRookMoves(isWhite);
            case WHITE_QUEEN, BLACK_QUEEN -> generateQueenMoves(isWhite);
            case WHITE_KING, BLACK_KING -> generateKingMoves(isWhite);
            default -> {
                System.out.println("Couldn't find any moves for piece type");
            }
        }
        for (Move move : bitboard.getMoveList()) {
            if (move.getFrom() == square) {
                specificMoves.add(move);
            }
        }
        bitboard.clearMoveList();
        bitboard.addAllToMoveList(originalMoveList);

        return specificMoves;
    }

    public static long knightMoveTargets(int square, boolean isWhite, long whitePieces, long blackPieces) {
        long targets = 0L;
        for (int direction : KNIGHT_DIRECTIONS) {
            int targetSquare = square + direction;
            if (BitboardUtils.isSquareOnBoard(targetSquare)) {
                int abs = Math.abs(targetSquare % 8 - square % 8);
                int abs1 = Math.abs(targetSquare / 8 - square / 8);
                if (abs == 2 && abs1 == 1
                        || abs == 1 && abs1 == 2) {
                    targets |= 1L << targetSquare;
                }
            }
        }
        targets &= isWhite ? ~whitePieces : ~blackPieces;

        return targets;
    }
    public static long kingMoveTargets(int square, boolean isWhite, long whitePieces, long blackPieces) {
        long targets = 0L;

        for (int direction : KING_DIRECTIONS) {
            int targetSquare = square + direction;
            if (BitboardUtils.isSquareOnBoard(targetSquare) && !BitboardUtils.isEdgeWrap(square, targetSquare)) {
                targets |= 1L << targetSquare;
            }
        }

        targets &= isWhite ? ~whitePieces : ~blackPieces;

        return targets;
    }

}
