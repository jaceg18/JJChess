package com.github.jaceg18.chess.engine;

import com.github.jaceg18.chess.audio.AudioPlayer;
import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.game.moves.Move;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AI {

    // Lots of placeholders need to be filled here

    private final boolean IS_AI_WHITE;
    private int MAX_DEPTH;
    private final int NUM_THREADS;
    private final ExecutorService executor;
    private final Bitboard bitboard;
    public AI(Bitboard bitboard, boolean IS_AI_WHITE, int MAX_DEPTH, int NUM_THREADS){
        this.IS_AI_WHITE = IS_AI_WHITE;
        this.MAX_DEPTH = MAX_DEPTH;
        this.NUM_THREADS = NUM_THREADS;
        this.bitboard = bitboard;
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public synchronized void playMove(){
        Move move = search();
        bitboard.movePiece(move, IS_AI_WHITE);
        AudioPlayer.playSound(move.isCapture());
    }

    public Move search(){
        AtomicInteger alpha = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger beta = new AtomicInteger(Integer.MAX_VALUE);
        ConcurrentLinkedQueue<ScoredMove> bestMoves = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        if (bitboard.getTotalPieceCount() < 16 && MAX_DEPTH != 6){
            MAX_DEPTH = 6;
            System.out.println("Depth switched to 6");
        }

        for (int i=0; i < NUM_THREADS; i++){
            executor.submit(() -> {
                ScoredMove bestMove = searchHelper(MAX_DEPTH, alpha, beta);
                bestMoves.add(bestMove);
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (ScoredMove scoredMove : bestMoves){
            if (scoredMove.score > bestScore){
                bestScore = scoredMove.score;
                bestMove = scoredMove.move;
            }
        }

        return bestMove;

    }
@SuppressWarnings("all")
    private ScoredMove searchHelper(int depth, AtomicInteger alpha, AtomicInteger beta){
        boolean currentPlayer = IS_AI_WHITE; // temp
        List<Move> moves = bitboard.getMoveGenerator().generateMoves(currentPlayer);

        int localAlpha = alpha.get();
        int localBeta = beta.get();
        Move bestMove = null;
        boolean first = true;

        for (int i=0; i<moves.size(); i++){
            Move move = moves.get(i);
            bitboard.movePiece(move, currentPlayer);
            int score;
            if (currentPlayer == IS_AI_WHITE) {
                score = min(localAlpha, localBeta, depth - 1, 1, true, i);
            } else {
                score = max(localAlpha, localBeta, depth - 1, 1, true, i);
            }


            bitboard.undoMove();

            if (first || (currentPlayer == IS_AI_WHITE && score > localAlpha) || (currentPlayer != IS_AI_WHITE && score < localBeta)) {
                first = false;
                bestMove = move;

                if (currentPlayer == IS_AI_WHITE) {
                    localAlpha = Math.max(localAlpha, score);
                    alpha.set(localAlpha);
                } else {
                    localBeta = Math.min(localBeta, score);
                    beta.set(localBeta);
                }
            }

            if (localAlpha >= localBeta) {
                break;
            }
        }

        if (bestMove != null) {
            return new ScoredMove(bestMove, (currentPlayer == IS_AI_WHITE) ? localAlpha : localBeta);
        }

        return null;
    }

    private int min(int alpha, int beta, int depth, int moveCount, boolean allowNullMove, int moveIndex){
        if (depth <= 0){
            return Evaluator.evaluate(bitboard, !IS_AI_WHITE);
        }

        int minScore = Integer.MAX_VALUE;
        for (Move move : bitboard.getMoveGenerator().generateMoves(!IS_AI_WHITE)){
            bitboard.movePiece(move, !IS_AI_WHITE);
            int score = max(alpha, beta, depth - 1, moveCount + 1, true, moveIndex);
            bitboard.undoMove();
            minScore = Math.min(minScore, score);
            if (beta <= alpha){
                break;
            }
        }
        return minScore;
    }

    private int max(int alpha, int beta, int depth, int moveCount, boolean allowNullMove, int moveIndex){
        if (depth <= 0){
            return Evaluator.evaluate(bitboard, IS_AI_WHITE);
        }


        int maxScore = Integer.MIN_VALUE;
        for (Move move : bitboard.getMoveGenerator().generateMoves(IS_AI_WHITE)){

            bitboard.movePiece(move, IS_AI_WHITE);
            int score = min(alpha, beta, depth - 1, moveCount + 1, true, moveIndex);
            bitboard.undoMove();

            maxScore = Math.max(maxScore, score);
            alpha = Math.max(alpha, maxScore);
            if (beta <= alpha){
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

        public void setScore(int score) {
            this.score = score;
        }
    }

}
