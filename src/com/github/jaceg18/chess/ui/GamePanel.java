package com.github.jaceg18.chess.ui;

import com.github.jaceg18.chess.audio.AudioPlayer;
import com.github.jaceg18.chess.engine.AI;
import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.game.moves.Move;
import com.github.jaceg18.chess.game.pieces.PieceType;
import com.github.jaceg18.chess.util.Drawer;
import com.github.jaceg18.chess.util.styles.BoardStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Represents the game panel where the chess game is displayed.
 * It handles user interactions and visual representation of the game state.
 */
public class GamePanel extends JPanel {
    private final Bitboard bitboard;
    private final boolean isPlayerWhite;
    private final Drawer drawer;
    private boolean isDragging;
    private int selectedSquare;
    private List<Move> legalMoves;
    private final AI aiPlayer;

    /**
     * Constructs a GamePanel with specified settings.
     *
     * @param bitboard       The bitboard representing the current game state.
     * @param isPlayerWhite  Determines if the player controls the white pieces.
     */
    public GamePanel(Bitboard bitboard, boolean isPlayerWhite, int MAX_DEPTH, BoardStyle boardStyle) {
        this.bitboard = bitboard;
        this.isPlayerWhite = isPlayerWhite;
        this.drawer = new Drawer(bitboard, boardStyle);
        this.isDragging = false;
        this.selectedSquare = -1;
        this.legalMoves = new ArrayList<>();
        this.aiPlayer = new AI(bitboard, !isPlayerWhite, MAX_DEPTH, 1);
        setPreferredSize(new Dimension(Drawer.BOARD_SIZE, Drawer.BOARD_SIZE));
        setFocusable(true);

        initializeMouseListener();

    }


    /**
     * Initializes the mouse listener to handle drag and drop chess piece movements.
     */
    private void initializeMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handlePieceSelection(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handlePiecePlacement(e);
            }
        });
    }

    /**
     * Handles the selection of a chess piece.
     *
     * @param e The mouse event triggered on mouse press.
     */
    private void handlePieceSelection(MouseEvent e) {
        int col = e.getX() / Drawer.SQUARE_SIZE;
        int row = e.getY() / Drawer.SQUARE_SIZE;
        int square = row * 8 + col;
        PieceType selectedPieceType = bitboard.getPieceAtSquare(square, isPlayerWhite);
        if (selectedPieceType != null) {
            selectedSquare = square;
            legalMoves = bitboard.getMoveGenerator().generateMovesForPieceAtSquare(square, isPlayerWhite);
            isDragging = true;
            repaint();
        }
    }

    /**
     * Handles the placement of a selected chess piece.
     *
     * @param e The mouse event triggered on mouse release.
     */
    private void handlePiecePlacement(MouseEvent e) {
        if (isDragging) {
            int col = e.getX() / Drawer.SQUARE_SIZE;
            int row = e.getY() / Drawer.SQUARE_SIZE;
            int targetSquare = row * 8 + col;

            if (targetSquare != selectedSquare) {
                Optional<Move> moveOptional = legalMoves.stream()
                        .filter(move -> move.getTo() == targetSquare)
                        .findFirst();

                moveOptional.ifPresent(move -> {
                    bitboard.movePiece(move, isPlayerWhite);
                    AudioPlayer.playSound(move.isCapture());
                    clearSelection();
                    repaint();
                    moveAI();
                });
            } else {
                clearSelection();
            }
        }
    }

    /**
     * Triggers the AI player to make a move and updates the GUI accordingly.
     */
    private void moveAI() {
        SwingUtilities.invokeLater(() -> {
            aiPlayer.playMove();
            repaint();
        });
    }

    /**
     * Clears the current piece selection and updates the GUI.
     */
    private void clearSelection() {
        isDragging = false;
        selectedSquare = -1;
        legalMoves.clear();
        repaint();
    }

    /**
     * Paints the game panel, including the board, pieces, and highlights for legal moves.
     *
     * @param g The Graphics object used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawer.draw(g2d, this);
        if (isDragging) {
            drawer.highlightLegalMoves(g2d, selectedSquare, legalMoves);
        }
    }

    /**
     * Returns the drawer used for drawing the game components.
     *
     * @return The drawer.
     */
    public Drawer getDrawer() {
        return drawer;
    }
}
