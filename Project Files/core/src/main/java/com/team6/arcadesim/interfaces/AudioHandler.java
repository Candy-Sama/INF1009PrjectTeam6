package com.team6.arcadesim.interfaces;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public interface AudioHandler {
    void init();
    void playSFX(Sound sound, float volume);
    void playMusic(Music music, boolean loop, float volume);
    void stopMusic();
    void setMusicVolume(float volume);
    void shutdown();
}
