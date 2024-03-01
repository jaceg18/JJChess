package com.github.jaceg18.chess.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioPlayer {
    private static final String CAPTURE_SOUND_PATH = "resources/audio/capture.wav";
    private static final String MOVE_SOUND_PATH = "resources/audio/move.wav";
    private static final Logger LOGGER = Logger.getLogger(AudioPlayer.class.getName());

    /**
     * Plays sound effect based on move
     * @param isCapture whether the move was a capture or not
     */
    public static synchronized void playSound(boolean isCapture){
        String filePath = isCapture ? CAPTURE_SOUND_PATH : MOVE_SOUND_PATH;
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath))) {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e){
            LOGGER.log(Level.WARNING, "Unable to play sound effect: " + filePath.split("/")[2]);
        }
    }
}
