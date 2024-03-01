package com.github.jaceg18.chess.game;

import com.github.jaceg18.chess.game.moves.Move;
import com.github.jaceg18.chess.game.moves.MoveGenerator;
import com.github.jaceg18.chess.game.moves.MoveLog;
import com.github.jaceg18.chess.game.pieces.PieceType;
import com.github.jaceg18.chess.util.BitboardUtils;

import java.util.ArrayList;
import java.util.List;

public class Bitboard {
    // SWITCH THIS TO ENUM MAP
    private long whitePawns,
            whiteKnights,
            whiteBishops,
            whiteRooks,
            whiteQueens,
            whiteKing,
            blackPawns,
            blackKnights,
            blackBishops,
            blackRooks,
            blackQueens,
            blackKing,
            whitePieces,
            blackPieces,
            occupiedSquares;
    private int castlingRights = 0b1111;
    private final MoveGenerator moveGenerator;
    private List<Move> moveList = new ArrayList<>();
    private List<MoveLog> moveLogs = new ArrayList<>();
    private boolean whiteCastled = false;
    private boolean blackCastled = false;

    /**
     * Constructor that creates a bitboard for our chess game
     */
    public Bitboard() {
        setupBoard();
        this.moveGenerator = new MoveGenerator(this);
    }

    /**
     * Sets up default chess position
     */
    private void setupBoard() {
        whitePawns = 0x00FF000000000000L;
        whiteKnights = 0x4200000000000000L;
        whiteBishops = 0x2400000000000000L;
        whiteRooks = 0x8100000000000000L;
        whiteQueens = 0x0800000000000000L;
        whiteKing = 0x1000000000000000L;

        blackPawns = 0x000000000000FF00L;
        blackKnights = 0x0000000000000042L;
        blackBishops = 0x0000000000000024L;
        blackRooks = 0x0000000000000081L;
        blackQueens = 0x0000000000000008L;
        blackKing = 0x0000000000000010L;
    }

    /**
     * Moves the piece
     *
     * @param move    The move
     * @param isWhite Is the piece we are moving white?
     */
    public void movePiece(Move move, boolean isWhite) {
        PieceType movedPiece = getPieceAtSquare((int) move.getFrom(), isWhite);
        PieceType capturedPiece = getPieceAtSquare((int) move.getTo(), !isWhite);
        if (movedPiece == null) {
            throw new IllegalStateException("No piece found at move source: " + move.getFrom());
        }


        moveLogs.add(new MoveLog(move.getFrom(), move.getTo(), movedPiece, capturedPiece, castlingRights));
        checkCastleRights(move, movedPiece, capturedPiece);

        if (Math.abs(move.getFrom() - move.getTo()) == 2 && movedPiece.name().toLowerCase().contains("king")) {
            boolean kingSide = move.getTo() > move.getFrom();
            moveRookForCastling(isWhite, kingSide);
            if (isWhite) whiteCastled = true;
            else blackCastled = true;
        }


        long fromMask = 1L << move.getFrom();
        long toMask = 1L << move.getTo();
        updateBitboards(movedPiece, fromMask, toMask, isWhite);
        clearOpponentPieceFromSquare(toMask, !isWhite);


        if (movedPiece == PieceType.WHITE_PAWN && (move.getTo() >= 0 && move.getTo() <= 7)) {
            whitePawns &= ~toMask;
            whiteQueens |= toMask;
        } else if (movedPiece == PieceType.BLACK_PAWN && (move.getTo() >= 56 && move.getTo() <= 63)) {
            blackPawns &= ~toMask;
            blackQueens |= toMask;
        }

        updateCompositeBitboards();
    }

    public int getTotalPieceCount() {
        int pieceCount = Long.bitCount(whitePawns) + Long.bitCount(whiteKnights) + Long.bitCount(whiteBishops) +
                Long.bitCount(whiteRooks) + Long.bitCount(whiteQueens) + Long.bitCount(whiteKing) +
                Long.bitCount(blackPawns) + Long.bitCount(blackKnights) + Long.bitCount(blackBishops) +
                Long.bitCount(blackRooks) + Long.bitCount(blackQueens) + Long.bitCount(blackKing);
        System.out.println(pieceCount);
        return pieceCount;
    }



    private void moveRookForCastling(boolean isWhite, boolean kingSide) {
        long fromMask, toMask;
        if (isWhite) {
            if (kingSide) {
                fromMask = 1L << 63;
                toMask = 1L << 61;
            } else {
                fromMask = 1L << 56;
                toMask = 1L << 59;
            }
        } else {
            if (kingSide) {
                fromMask = 1L << 7;
                toMask = 1L << 5;
            } else {
                fromMask = 1L;
                toMask = 1L << 3;
            }
        }

        updateBitboards(isWhite ? PieceType.WHITE_ROOK : PieceType.BLACK_ROOK, fromMask, toMask, isWhite);
    }

    private void checkCastleRights(Move move, PieceType movedPiece, PieceType capturedPiece) {
        if (movedPiece == PieceType.WHITE_KING) {
            castlingRights &= 0b0011;
        } else if (movedPiece == PieceType.BLACK_KING) {
            castlingRights &= 0b1100;
        }


        if (movedPiece == PieceType.WHITE_ROOK) {
            if (move.getFrom() == 63) {
                castlingRights &= 0b1011;
            } else if (move.getFrom() == 56) {
                castlingRights &= 0b0111;
            }
        } else if (movedPiece == PieceType.BLACK_ROOK) {
            if (move.getFrom() == 7) {
                castlingRights &= 0b1101; // 0b1110
            } else if (move.getFrom() == 0) {
                castlingRights &= 0b1110; // 0b1101
            }
        }

        if (capturedPiece == PieceType.WHITE_ROOK) {
            if (move.getTo() == 63) {
                castlingRights &= 0b1011;
            } else if (move.getTo() == 56) {
                castlingRights &= 0b0111;
            }
        } else if (capturedPiece == PieceType.BLACK_ROOK) {
            if (move.getTo() == 7) {
                castlingRights &= 0b1101; // 0b1110
            } else if (move.getTo() == 0) {
                castlingRights &= 0b1110; // 0b1101
            }
        }
    }

    public void undoMove() {
        if (moveLogs.isEmpty()) return;

        MoveLog lastMove = moveLogs.remove(moveLogs.size() - 1);

        boolean isUndoingPromotion = false;
        if ((lastMove.movedPiece == PieceType.WHITE_PAWN && (lastMove.toPosition >= 0 && lastMove.toPosition <= 7)) ||
                (lastMove.movedPiece == PieceType.BLACK_PAWN && (lastMove.toPosition >= 56 && lastMove.toPosition <= 63))) {
            isUndoingPromotion = true;
            // Remove the queen from the promotion square
            if (lastMove.movedPiece == PieceType.WHITE_PAWN) {
                whiteQueens &= ~(1L << lastMove.toPosition);
            } else {
                blackQueens &= ~(1L << lastMove.toPosition);
            }
        }

        updateBitboards(lastMove.movedPiece, 1L << lastMove.toPosition, 1L << lastMove.fromPosition, isPieceWhite(lastMove.movedPiece));

        if (isUndoingPromotion) {
            // Place the pawn back to its original position
            if (lastMove.movedPiece == PieceType.WHITE_PAWN) {
                whitePawns |= 1L << lastMove.fromPosition;
            } else {
                blackPawns |= 1L << lastMove.fromPosition;
            }
        }

        if (lastMove.capturedPiece != null) {
            restoreCapturedPiece(lastMove.capturedPiece, 1L << lastMove.toPosition, !isPieceWhite(lastMove.movedPiece));
        }

        castlingRights = lastMove.castlingRightsBeforeMove;

        if (Math.abs(lastMove.fromPosition - lastMove.toPosition) == 2 && (lastMove.movedPiece == PieceType.WHITE_KING || lastMove.movedPiece == PieceType.BLACK_KING)) {

            boolean kingSide = lastMove.toPosition > lastMove.fromPosition;
            long rookFromPosition, rookToPosition;

            if (lastMove.movedPiece == PieceType.WHITE_KING) whiteCastled = false;
            else blackCastled = false;

            if (kingSide) {
                rookFromPosition = isPieceWhite(lastMove.movedPiece) ? 63 : 7;
                rookToPosition = rookFromPosition - 2;
            } else {
                rookFromPosition = isPieceWhite(lastMove.movedPiece) ? 56 : 0;
                rookToPosition = rookFromPosition + 3;
            }
            updateBitboards(lastMove.movedPiece == PieceType.WHITE_KING ? PieceType.WHITE_ROOK : PieceType.BLACK_ROOK, 1L << rookToPosition, 1L << rookFromPosition, isPieceWhite(lastMove.movedPiece));
        }




        updateCompositeBitboards();
    }

    private boolean isPieceWhite(PieceType piece) {
        return piece.ordinal() <= PieceType.WHITE_KING.ordinal();
    }

    private void restoreCapturedPiece(PieceType pieceType, long positionMask, boolean isWhite) {
        switch (pieceType) {
            case WHITE_PAWN -> {
                if (isWhite) whitePawns |= positionMask;
            }
            case WHITE_KNIGHT -> {
                if (isWhite) whiteKnights |= positionMask;
            }
            case WHITE_BISHOP -> {
                if (isWhite) whiteBishops |= positionMask;
            }
            case WHITE_ROOK -> {
                if (isWhite) whiteRooks |= positionMask;
            }
            case WHITE_QUEEN -> {
                if (isWhite) whiteQueens |= positionMask;
            }
            case WHITE_KING -> {
                if (isWhite) whiteKing |= positionMask;
            }
            case BLACK_PAWN -> {
                if (!isWhite) blackPawns |= positionMask;
            }
            case BLACK_KNIGHT -> {
                if (!isWhite) blackKnights |= positionMask;
            }
            case BLACK_BISHOP -> {
                if (!isWhite) blackBishops |= positionMask;
            }
            case BLACK_ROOK -> {
                if (!isWhite) blackRooks |= positionMask;
            }
            case BLACK_QUEEN -> {
                if (!isWhite) blackQueens |= positionMask;
            }
            case BLACK_KING -> {
                if (!isWhite) blackKing |= positionMask;
            }
        }

        updateCompositeBitboards();
    }

    public void addMoves(long moveBitboard, int offset, boolean isWhite) {
        while (moveBitboard != 0) {
            int to = Long.numberOfTrailingZeros(moveBitboard);
            int from = to + offset;
            addMove(from, to, isWhite);
            moveBitboard &= moveBitboard - 1;
        }
    }

    public void addMove(int from, int to, boolean isWhite) {
        long toPosition = 1L << to;
        boolean isToPositionKing = (toPosition & (whiteKing | blackKing)) != 0;
        boolean isCapture = getPieceAtSquare(to, !isWhite) != null;
        Move move = new Move(from, to, isCapture, isWhite);
        boolean leavesKingInCheck = isKingInCheckAfterMove(move, isWhite);

        if (!isToPositionKing && !leavesKingInCheck) {
            moveList.add(move);
        }
    }

    public void addMovesFromBitboard(int from, long toBitboard, boolean isWhite) {
        while (toBitboard != 0) {
            int to = Long.numberOfTrailingZeros(toBitboard);
            addMove(from, to, isWhite);
            toBitboard &= toBitboard - 1;
        }
    }

    private void updateBitboards(PieceType pieceType, long fromMask, long toMask, boolean isWhite) {
        if (pieceType == null) {
            throw new IllegalStateException("Attempted to move a piece that is null");
        } else {
            switch (pieceType) {
                case WHITE_PAWN -> whitePawns = (whitePawns & ~fromMask) | toMask;
                case WHITE_KNIGHT -> whiteKnights = (whiteKnights & ~fromMask) | toMask;
                case WHITE_BISHOP -> whiteBishops = (whiteBishops & ~fromMask) | toMask;
                case WHITE_ROOK -> whiteRooks = (whiteRooks & ~fromMask) | toMask;
                case WHITE_QUEEN -> whiteQueens = (whiteQueens & ~fromMask) | toMask;
                case WHITE_KING -> whiteKing = (whiteKing & ~fromMask) | toMask;
                case BLACK_PAWN -> blackPawns = (blackPawns & ~fromMask) | toMask;
                case BLACK_KNIGHT -> blackKnights = (blackKnights & ~fromMask) | toMask;
                case BLACK_BISHOP -> blackBishops = (blackBishops & ~fromMask) | toMask;
                case BLACK_ROOK -> blackRooks = (blackRooks & ~fromMask) | toMask;
                case BLACK_QUEEN -> blackQueens = (blackQueens & ~fromMask) | toMask;
                case BLACK_KING -> blackKing = (blackKing & ~fromMask) | toMask;
            }
        }
    }
    private void updateCompositeBitboards() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        occupiedSquares = whitePieces | blackPieces;
    }

    private void clearOpponentPieceFromSquare(long mask, boolean isOpponentWhite) {
        if (isOpponentWhite) {
            if ((whitePawns & mask) != 0) whitePawns &= ~mask;
            else if ((whiteKnights & mask) != 0) whiteKnights &= ~mask;
            else if ((whiteBishops & mask) != 0) whiteBishops &= ~mask;
            else if ((whiteRooks & mask) != 0) whiteRooks &= ~mask;
            else if ((whiteQueens & mask) != 0) whiteQueens &= ~mask;
            else if ((whiteKing & mask) != 0) whiteKing &= ~mask;
        } else {
            // Check and clear the piece from black bitboards
            if ((blackPawns & mask) != 0) blackPawns &= ~mask;
            else if ((blackKnights & mask) != 0) blackKnights &= ~mask;
            else if ((blackBishops & mask) != 0) blackBishops &= ~mask;
            else if ((blackRooks & mask) != 0) blackRooks &= ~mask;
            else if ((blackQueens & mask) != 0) blackQueens &= ~mask;
            else if ((blackKing & mask) != 0) blackKing &= ~mask;
        }


        updateCompositeBitboards();
    }

    /**
     * Is a same color pieced on a given square?
     *
     * @param square  The square
     * @param isWhite The color your checking
     * @return a boolean that determines if a square contains a same team piece
     */
    private boolean isOwnPieceOnSquare(int square, boolean isWhite) {
        long position = 1L << square;
        return isWhite ? (position & whitePieces) != 0 : (position & blackPieces) != 0;
    }

    /**
     * Helper method to get a PieceType for a given square
     *
     * @param square  The square
     * @param isWhite Is the piece white?
     * @return The PieceType on the square
     */
    public PieceType getPieceAtSquare(int square, boolean isWhite) {
        long mask = 1L << square;

        if ((whitePawns & mask) != 0 && isWhite) return PieceType.WHITE_PAWN;
        if ((whiteKnights & mask) != 0 && isWhite) return PieceType.WHITE_KNIGHT;
        if ((whiteBishops & mask) != 0 && isWhite) return PieceType.WHITE_BISHOP;
        if ((whiteRooks & mask) != 0 && isWhite) return PieceType.WHITE_ROOK;
        if ((whiteQueens & mask) != 0 && isWhite) return PieceType.WHITE_QUEEN;
        if ((whiteKing & mask) != 0 && isWhite) return PieceType.WHITE_KING;

        if ((blackPawns & mask) != 0 && !isWhite) return PieceType.BLACK_PAWN;
        if ((blackKnights & mask) != 0 && !isWhite) return PieceType.BLACK_KNIGHT;
        if ((blackBishops & mask) != 0 && !isWhite) return PieceType.BLACK_BISHOP;
        if ((blackRooks & mask) != 0 && !isWhite) return PieceType.BLACK_ROOK;
        if ((blackQueens & mask) != 0 && !isWhite) return PieceType.BLACK_QUEEN;
        if ((blackKing & mask) != 0 && !isWhite) return PieceType.BLACK_KING;

        return null; // No piece at the given square for the specified color
    }

    // PRETEND THERE IS LIKE 20 GETTERS AND SETTERS HERE! LEFT OUT FOR LENGTH
    public long getWhitePawns() {
        return whitePawns;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getWhiteQueens() {
        return whiteQueens;
    }

    public long getBlackQueens() {
        return blackQueens;
    }
    public long getWhiteKing() {
        return whiteKing;
    }
    public long getBlackKing() {
        return blackKing;
    }

    private int getKingPosition(boolean isWhite) {
        return Long.numberOfTrailingZeros(isWhite ? whiteKing : blackKing);
    }

    public long getWhitePieces() {
        return whitePieces;
    }

    public long getBlackPieces() {
        return blackPieces;
    }
    public boolean isWhiteCastled(){
        return whiteCastled;
    }

    public boolean isBlackCastled(){
        return blackCastled;
    }

    public long getOccupiedSquares() {
        return occupiedSquares;
    }

    public long getCastlingRights() {
        return castlingRights;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public void clearMoveList() {
        moveList.clear();
    }

    public void addToMoveList(Move move) {
        moveList.add(move);
    }

    public void addAllToMoveList(List<Move> moves) {
        moveList.addAll(moves);
    }

    public MoveGenerator getMoveGenerator() {
        return moveGenerator;
    }

    /**
     * Checks if the king is in check
     *
     * @param isWhite Is the black or white king in check?
     * @return A boolean that determines if the king of a given color is in check.
     */
    public boolean isKingInCheckAfterMove(Move move, boolean isWhite) {
        movePiece(move, isWhite);
        boolean isInCheck = isSquareAttacked(getKingPosition(isWhite), isWhite);
        undoMove();
        return isInCheck;
    }

    /**
     * A method that checks for attacks on a given square
     * This is very inefficient, however it's more efficient than regenerating a full list of moves to check for illegal moves
     * This will be replaced soon.
     *
     * @param square  The square we are testing
     * @param isWhite Is white or black getting attacked?
     * @return Is the square being attacked
     */
    public boolean isSquareAttacked(int square, boolean isWhite) {
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        long opponentKnights = isWhite ? blackKnights : whiteKnights;
        long opponentBishopsQueens = isWhite ? (blackBishops | blackQueens) : (whiteBishops | whiteQueens);
        long opponentRooksQueens = isWhite ? (blackRooks | blackQueens) : (whiteRooks | whiteQueens);
        long opponentKing = isWhite ? blackKing : whiteKing;
        long allPieces = whitePieces | blackPieces;

        int[] pawnAttackOffsets = isWhite ? new int[]{-9, -7} : new int[]{7, 9};
        for (int offset : pawnAttackOffsets) {
            int targetSquare = square + offset;
            if (BitboardUtils.isSquareOnBoard(targetSquare) && (opponentPawns & (1L << targetSquare)) != 0) {
                return true;
            }
        }

        int[] knightOffsets = {-17, -15, -10, -6, 6, 10, 15, 17};
        for (int offset : knightOffsets) {
            int targetSquare = square + offset;
            if (BitboardUtils.isSquareOnBoard(targetSquare) && (opponentKnights & (1L << targetSquare)) != 0) {
                return true;
            }
        }

        int[] bishopOffsets = {-9, -7, 7, 9};
        int[] rookOffsets = {-8, -1, 1, 8};

        for (int offset : bishopOffsets) {
            int targetSquare = square;
            while (true) {
                targetSquare += offset;
                if (!BitboardUtils.isSquareOnBoard(targetSquare) || BitboardUtils.isEdgeWrap(targetSquare - offset, targetSquare)) {
                    break;
                }
                if ((allPieces & (1L << targetSquare)) != 0) {
                    if ((opponentBishopsQueens & (1L << targetSquare)) != 0) return true;
                    break;
                }
            }
        }
        for (int offset : rookOffsets) {
            int targetSquare = square;
            while (true) {
                targetSquare += offset;
                if (!BitboardUtils.isSquareOnBoard(targetSquare) || BitboardUtils.isEdgeWrap(targetSquare - offset, targetSquare)) {
                    break;
                }
                if ((allPieces & (1L << targetSquare)) != 0) {
                    if ((opponentRooksQueens & (1L << targetSquare)) != 0) return true;
                    break;
                }
            }
        }

        int[] kingOffsets = {-9, -8, -7, -1, 1, 7, 8, 9};
        for (int offset : kingOffsets) {
            int targetSquare = square + offset;
            if (BitboardUtils.isSquareOnBoard(targetSquare) && (opponentKing & (1L << targetSquare)) != 0) {
                return true;
            }
        }

        return false;
    }


}
