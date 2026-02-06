package com.team6.arcadesim.interfaces;

import java.applet.AudioClip;

public interface AudioHandler {
    public void init();
    public void playSFX(AudioClip clip, float volume);
    public void playMusic(AudioClip clip, boolean loop, float volume);
    public void stopMusic();
    public void setVolume(AudioClip clip, float volume);
    public void shutdown();
}
