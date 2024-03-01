package com.github.jaceg18.chess.ui;

import com.github.jaceg18.chess.game.pieces.PieceType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class Textures {
    private static final Map<PieceType, Image> pieceTextures = new EnumMap<>(PieceType.class);
    private static final String SPRITE_PATH = "resources/textures/pieces.png";
    private static final Image[] PIECE_ICONS = new Image[12];
    public static final int ADJUSTED_SPRITE_SIZE = 64;
    public static final int DEFAULT_SPRITE_SIZE = 200;
    private static final int SPRITE_HEIGHT = 400;
    private static final int SPRITE_WIDTH = 1200;
    public static final Image textureLight;
    public static final Image textureDark;
    public static Image textureWhitePawn;
    public static Image textureBlackPawn;
    public static Image textureWhiteKnight;
    public static Image textureBlackKnight;
    public static Image textureWhiteBishop;
    public static Image textureBlackBishop;
    public static Image textureWhiteRook;
    public static Image textureBlackRook;
    public static Image textureWhiteQueen;
    public static Image textureBlackQueen;
    public static Image textureWhiteKing;
    public static Image textureBlackKing;

    static {
        BufferedImage all;
        try {
            ImageIcon iconLight = new ImageIcon("resources/textures/lightsquare.png");
            ImageIcon iconDark = new ImageIcon("resources/textures/darksquare.png");

            textureLight = iconLight.getImage();
            textureDark = iconDark.getImage();

            all = ImageIO.read(new File(SPRITE_PATH));
            int index = 0;

            for (int y = 0; y < SPRITE_HEIGHT; y+=DEFAULT_SPRITE_SIZE){
                for (int x = 0; x < SPRITE_WIDTH; x+=DEFAULT_SPRITE_SIZE){
                    PIECE_ICONS[index] = all.getSubimage(x, y, DEFAULT_SPRITE_SIZE, DEFAULT_SPRITE_SIZE).getScaledInstance(ADJUSTED_SPRITE_SIZE, ADJUSTED_SPRITE_SIZE, BufferedImage.SCALE_SMOOTH);
                    index++;
                }
            }

            textureWhiteKing = PIECE_ICONS[0];
            textureWhiteQueen = PIECE_ICONS[1];
            textureWhiteBishop = PIECE_ICONS[2];
            textureWhiteKnight = PIECE_ICONS[3];
            textureWhiteRook = PIECE_ICONS[4];
            textureWhitePawn = PIECE_ICONS[5];

            textureBlackPawn = PIECE_ICONS[11];
            textureBlackKnight = PIECE_ICONS[9];
            textureBlackBishop = PIECE_ICONS[8];
            textureBlackRook = PIECE_ICONS[10];
            textureBlackQueen = PIECE_ICONS[7];
            textureBlackKing = PIECE_ICONS[6];

            pieceTextures.put(PieceType.WHITE_PAWN, textureWhitePawn);
            pieceTextures.put(PieceType.WHITE_KNIGHT, textureWhiteKnight);
            pieceTextures.put(PieceType.WHITE_BISHOP, textureWhiteBishop);
            pieceTextures.put(PieceType.WHITE_ROOK, textureWhiteRook);
            pieceTextures.put(PieceType.WHITE_QUEEN, textureWhiteQueen);
            pieceTextures.put(PieceType.WHITE_KING, textureWhiteKing);

            pieceTextures.put(PieceType.BLACK_PAWN, textureBlackPawn);
            pieceTextures.put(PieceType.BLACK_KNIGHT, textureBlackKnight);
            pieceTextures.put(PieceType.BLACK_BISHOP, textureBlackBishop);
            pieceTextures.put(PieceType.BLACK_ROOK, textureBlackRook);
            pieceTextures.put(PieceType.BLACK_QUEEN, textureBlackQueen);
            pieceTextures.put(PieceType.BLACK_KING, textureBlackKing);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load piece images.", e);
        }
    }
    public static Image getTextureForPieceType(PieceType pieceType) {
        return pieceTextures.get(pieceType);
    }
}
