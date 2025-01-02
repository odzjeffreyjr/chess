package org.cis1200.chess;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffects {
    private final String castleSound = "files/castle.wav";
    private final String moveSound = "files/move-self.wav";
    private final String checkmateSound = "files/game-end.wav";
    private final String checkSound = "files/move-check.wav";
    private Clip checkmateClip;
    private Clip moveClip;
    private Clip checkClip;
    private Clip castleClip;

    public SoundEffects() {
        try {
            File soundFile1 = new File(moveSound);
            AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(soundFile1);
            File soundFile2 = new File(checkmateSound);
            AudioInputStream audioStream2 = AudioSystem.getAudioInputStream(soundFile2);
            File soundFile3 = new File(checkSound);
            AudioInputStream audioStream3 = AudioSystem.getAudioInputStream(soundFile3);
            File soundFile4 = new File(castleSound);
            AudioInputStream audioStream4 = AudioSystem.getAudioInputStream(soundFile4);

            // Get the clip and open the audio stream
            moveClip = AudioSystem.getClip();
            moveClip.open(audioStream1);
            checkClip = AudioSystem.getClip();
            checkClip.open(audioStream3);
            checkmateClip = AudioSystem.getClip();
            checkmateClip.open(audioStream2);
            castleClip = AudioSystem.getClip();
            castleClip.open(audioStream4);

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public void playEffect(Effect effectType) {
        switch (effectType) {
            case MOVE:
                moveClip.setFramePosition(0);
                moveClip.start();
                break;
            case CHECK:
                checkClip.setFramePosition(0);
                checkClip.start();
                break;
            case CHECKMATE:
                checkmateClip.setFramePosition(0);
                checkmateClip.start();
                break;
            case CASTLE:
                castleClip.setFramePosition(0);
                castleClip.start();
                break;
            default:
                break;
        }
    }
}
