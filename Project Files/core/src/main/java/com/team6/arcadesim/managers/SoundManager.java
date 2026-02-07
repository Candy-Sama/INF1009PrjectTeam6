package com.team6.arcadesim.managers;

import com.team6.arcadesim.interfaces.AudioHandler;

public class SoundManager {
    private AudioHandler audioHandler;
    private float masterVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private float musicVolume = 1.0f;
    private String playingMusicId;;

    public SoundManager() {
        // to be initiliased in GameMaster or Scene
    }

    public void playSFX(String id) {
        if (audioHandler != null) {
            audioHandler.playSFX(id); // Delegates to the interface
        }
    }

    public void playMusic(String id, boolean loop) {
        if (audioHandler != null) {
            this.playingMusicId = id;
            audioHandler.playMusic(id, loop);
        }
    }

    public void stopMusic() {
        if (audioHandler != null) {
            audioHandler.stopMusic();
            this.playingMusicId = null;
        }
    }

    public void setMasterVolume(float v) {
        this.masterVolume = v;
        
    }
}
