package com.team6.arcadesim.interfaces;
import com.team6.arcadesim.ecs.AudioClip;

public interface AudioHandler {
    void init();
    void playSFX(AudioClip clip, float volume);
    void playMusic(AudioClip clip, boolean loop, float volume);
    void stopMusic();
    void setVolume(AudioClip clip, float volume);
    void shutdown();
}
