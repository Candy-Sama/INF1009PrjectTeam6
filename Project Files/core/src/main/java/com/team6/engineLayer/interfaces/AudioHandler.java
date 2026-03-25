package com.team6.engineLayer.interfaces;
import com.team6.engineLayer.ecs.AudioClip;

public interface AudioHandler {
    void init();
    void playSFX(AudioClip clip, float volume);
    void playMusic(AudioClip clip, boolean loop, float volume);
    void stopMusic();
    void setVolume(AudioClip clip, float volume);
    void shutdown();
}
