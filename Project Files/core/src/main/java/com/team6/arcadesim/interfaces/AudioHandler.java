package com.team6.arcadesim.interfaces;

public interface AudioHandler {
    void init();
    void playSFX(String id);
    void playMusic(String id, boolean loop);
    void stopMusic();
    void shutdown();
}
