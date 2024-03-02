package com.github.jaceg18.chess.util;

import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.game.moves.Move;
import com.github.jaceg18.chess.game.pieces.PieceType;
import com.github.jaceg18.chess.ui.GamePanel;
import com.github.jaceg18.chess.ui.Textures;
import com.github.jaceg18.chess.util.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
public class Drawer {
    private final Bitboard bitboard;
    public static final int BOARD_SIZE = 512;
    public static final int SQUARE_SIZE = BOARD_SIZE / 8;
    private Image boardImage = null;
    private BoardStyle boardStyle;
    public Drawer(Bitboard bitboard, BoardStyle boardStyle) {
        this.bitboard = bitboard;
        setBoardStyle(boardStyle);
    }
    public void draw(Graphics2D g2d, GamePanel panel){
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        boardStyle.drawBoard(g2d, panel);
        drawPieces(g2d);
    }
    public void highlightLegalMoves(Graphics2D g2d, int selectedSquare, List<Move> legalMoves) {
        if (selectedSquare != -1) {
            for (Move move : legalMoves) {
                int from = (int) move.getFrom();
                int to = (int) move.getTo();

                if (from == selectedSquare) {
                    int col = to % 8;
                    int row = to / 8;
                    int x = col * SQUARE_SIZE;
                    int y = row * SQUARE_SIZE;

                    RadialGradientPaint paint = new RadialGradientPaint(
                            new Point2D.Float(x + SQUARE_SIZE / 2f, y + SQUARE_SIZE / 2f), SQUARE_SIZE / 2f, new float[] {0.0f, 1.0f},
                            new Color[] {new Color(50, 205, 50, 120), new Color(50, 205, 50, 0)});
                    g2d.setPaint(paint);
                    g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
        }
    }
    private void drawPieces(Graphics2D g2d) {
        Map<PieceType, Long> piecePositionMap = new EnumMap<>(PieceType.class);

        piecePositionMap.put(PieceType.WHITE_PAWN, bitboard.getWhitePawns());
        piecePositionMap.put(PieceType.WHITE_KNIGHT, bitboard.getWhiteKnights());
        piecePositionMap.put(PieceType.WHITE_BISHOP, bitboard.getWhiteBishops());
        piecePositionMap.put(PieceType.WHITE_ROOK, bitboard.getWhiteRooks());
        piecePositionMap.put(PieceType.WHITE_QUEEN, bitboard.getWhiteQueens());
        piecePositionMap.put(PieceType.WHITE_KING, bitboard.getWhiteKing());

        piecePositionMap.put(PieceType.BLACK_PAWN, bitboard.getBlackPawns());
        piecePositionMap.put(PieceType.BLACK_KNIGHT, bitboard.getBlackKnights());
        piecePositionMap.put(PieceType.BLACK_BISHOP, bitboard.getBlackBishops());
        piecePositionMap.put(PieceType.BLACK_ROOK, bitboard.getBlackRooks());
        piecePositionMap.put(PieceType.BLACK_QUEEN, bitboard.getBlackQueens());
        piecePositionMap.put(PieceType.BLACK_KING, bitboard.getBlackKing());

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                long position = 1L << (row * 8 + col);
                for (Map.Entry<PieceType, Long> entry : piecePositionMap.entrySet()) {
                    if ((entry.getValue() & position) != 0) {
                        drawPiece(g2d, Textures.getTextureForPieceType(entry.getKey()), col, row);
                        break;
                    }
                }
            }
        }
    }
    private void drawPiece(Graphics2D g2d, Image sprite, int col, int row) {
       int x = col * SQUARE_SIZE;
       int y = row * SQUARE_SIZE;
       g2d.drawImage(sprite, x, y, Textures.ADJUSTED_SPRITE_SIZE, Textures.ADJUSTED_SPRITE_SIZE, null);
    }
    public Image getBoardImage(){
        return boardImage;
    }
    public void setBoardStyle(BoardStyle style){
        this.boardStyle = style;
        this.boardImage = null;
    }

}
