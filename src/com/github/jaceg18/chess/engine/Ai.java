package com.github.jaceg18.chess.engine;

import com.github.jaceg18.chess.audio.AudioPlayer;
import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.game.moves.Move;

import java.util.List;


public class Ai {

    private final boolean isAiWhite;
    private int maxDepth;
    private final Bitboard bitboard;

    public Ai(Bitboard bitboard, boolean isAiWhite, int maxDepth) {
        this.isAiWhite = isAiWhite;
        this.maxDepth = maxDepth;
        this.bitboard = bitboard;
    }

    public void playMove() {
        Move move = search();
        bitboard.movePiece(move, isAiWhite);
        AudioPlayer.playSound(move.isCapture());
    }

    public Move search() {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        if (bitboard.getTotalPieceCount() < 8 && maxDepth != 6) {
            maxDepth = 6;
            System.out.println("Depth increased for end game");
        }

        ScoredMove bestMove = searchHelper(maxDepth, alpha, beta);

        if (bestMove == null){
            throw new NullPointerException("Move is null");
        }

        return bestMove.move;
    }

    @SuppressWarnings("all")
    private ScoredMove searchHelper(int depth, int alpha, int beta) {
        boolean currentPlayer = isAiWhite;
        List<Move> moves = bitboard.getMoveGenerator().generateMoves(currentPlayer);


        Move bestMove = null;
        boolean first = true;

        for (Move move : moves) {
            bitboard.movePiece(move, currentPlayer);
            int score = currentPlayer == isAiWhite ?
                    min(alpha, beta, depth - 1) :
                    max(alpha, beta, depth - 1);

            bitboard.undoMove();

            if (first || currentPlayer == isAiWhite && score > alpha || currentPlayer != isAiWhite && score < beta) {
                first = false;
                bestMove = move;
                if (currentPlayer == isAiWhite) {
                    alpha = Math.max(alpha, score);
                } else {
                    beta = Math.min(beta, score);
                }
            }

            if (alpha >= beta) {
                break;
            }
        }

        return bestMove == null ? null : new ScoredMove(bestMove, currentPlayer == isAiWhite ? alpha : beta);
    }


    private int min(int alpha, int beta, int depth) {
        if (depth <= 0) {
            return Evaluator.evaluate(bitboard, !isAiWhite);
        }

        int minScore = Integer.MAX_VALUE;
        for (Move move : bitboard.getMoveGenerator().generateMoves(!isAiWhite)) {
            bitboard.movePiece(move, !isAiWhite);
            int score = max(alpha, beta, depth - 1);
            bitboard.undoMove();
            minScore = Math.min(minScore, score);
            if (beta <= alpha) {
                break;
            }
        }
        return minScore;
    }

    private int max(int alpha, int beta, int depth) {
        if (depth <= 0) {
            return Evaluator.evaluate(bitboard, isAiWhite);
        }

        int maxScore = Integer.MIN_VALUE;
        for (Move move : bitboard.getMoveGenerator().generateMoves(isAiWhite)) {
            bitboard.movePiece(move, isAiWhite);
            int score = min(alpha, beta, depth - 1);
            bitboard.undoMove();
            maxScore = Math.max(maxScore, score);
            alpha = Math.max(alpha, maxScore);
            if (beta <= alpha) {
                break;
            }
        }
        return maxScore;
    }


    private static class ScoredMove {
        int score;
        Move move;

        public ScoredMove(Move move, int score) {
            this.move = move;
            this.score = score;
        }
    }

}
