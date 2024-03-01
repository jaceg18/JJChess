package com.github.jaceg18;

import com.github.jaceg18.chess.game.Bitboard;
import com.github.jaceg18.chess.ui.GamePanel;
import com.github.jaceg18.chess.util.SettingsDialog;
import com.github.jaceg18.chess.util.styles.*;

import javax.swing.*;

/**
 * TO DO LIST
 * Rework AI class to include Opening Book, Zobrist Hashing for transposition tables, quiescence searches, working multi-threading, and iterative deepening.
 * Remove sample evaluation class and replace with optimized one
 * Make a visually appealing GUI
 * Optimize move generation
 * Add en passant functionality
 * Add move ordering strategies for better pruning
 * Make the code look good, optimize, clean up etc. (The majority of the code now is written badly, I wasn't expecting Chess programming to be this complex, so lots of rushed code needs to be fixed)
 * Restructure logic within the engine
 * Add java doc comments
 * Add comments that clarify hard to understand sections of my code
 * Probably a lot more I haven't thought of yet
 */
public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("JJChess");
        SettingsDialog settingsDialog = new SettingsDialog(frame);
        settingsDialog.setVisible(true);

        String boardDesign = settingsDialog.getSelectedBoardDesign();
        BoardStyle[] availableBoards = {new GradientBoard(), new ClearBoard(), new CopperIllusionBoard(), new MarbleBoard(), new RainbowBoard(), new RigidBoard(), new SpaceBoard()};

        BoardStyle selectedStyle = availableBoards[0]; // default

        for (BoardStyle availableBoard : availableBoards) {
            if (availableBoard.getClass().getSimpleName().toLowerCase().contains(boardDesign.toLowerCase())) {
                selectedStyle = availableBoard;
                break;
            }
        }

        int aiDepth = settingsDialog.getSelectedAIDepth();


        Bitboard board = new Bitboard();
        GamePanel gamePanel = new GamePanel(board, true, aiDepth, selectedStyle);

        frame.add(gamePanel);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}