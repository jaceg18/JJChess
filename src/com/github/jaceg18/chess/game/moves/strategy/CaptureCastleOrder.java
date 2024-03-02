package com.github.jaceg18.chess.game.moves.strategy;

import com.github.jaceg18.chess.game.moves.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CaptureCastleOrder implements OrderingStrategy {

    // This orders moves based on captures and castling availability. Good for balance between offense and defense.
    @Override
    public List<Move> orderMoves(List<Move> moves) {
        List<Move> orderedMoves = new ArrayList<>(moves.size());
        List<Move> otherMoves = new ArrayList<>();

        for (Move move : moves) {
            if (move.isCastle() || move.isCapture()) {
                orderedMoves.add(move);
            } else {
                otherMoves.add(move);
            }
        }

        Collections.shuffle(otherMoves);

        orderedMoves.addAll(otherMoves);

        return orderedMoves;
    }

}
