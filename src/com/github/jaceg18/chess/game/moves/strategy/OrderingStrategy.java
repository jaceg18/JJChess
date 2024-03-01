package com.github.jaceg18.chess.game.moves.strategy;

import com.github.jaceg18.chess.game.moves.Move;

import java.util.List;

public interface OrderingStrategy {
    List<Move> orderMoves(List<Move> moves);
}
