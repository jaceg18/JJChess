package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;
import com.github.jaceg18.chess.ui.Textures;

import java.awt.*;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class MarbleBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Image texture = ((row + col) % 2 == 0) ? Textures.textureDark : Textures.textureLight;
                    g2dBoard.drawImage(texture, col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, null);
                }
            }
            g2dBoard.dispose();
        }

        g2d.drawImage(boardImage, 0, 0, null);
    }
}
