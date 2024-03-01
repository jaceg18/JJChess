package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;

import java.awt.*;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class ClearBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();

            Color lightTransparent = new Color(255, 255, 255, 100);
            Color darkTransparent = new Color(0, 0, 0, 100);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int x = col * SQUARE_SIZE;
                    int y = row * SQUARE_SIZE;

                    Color color = (row + col) % 2 == 0 ? lightTransparent : darkTransparent;
                    g2dBoard.setColor(color);
                    g2dBoard.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
            g2dBoard.dispose();
        }
        g2d.drawImage(boardImage, 0, 0, null);
    }
}
