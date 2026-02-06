package com.team6.arcadesim.managers;

import java.applet.AudioClip;
import java.util.HashMap;
import java.util.Map;

import com.team6.arcadesim.interfaces.AudioHandler;

public class SoundManager {
    private AudioHandler audioHandler;
    private float masterVolume = 1.0f;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private String playingMusicId;

    //Why Maps? Because we can map String IDs to AudioClips for easy retrieval.
    private Map<String, AudioClip> audioClips;
    
    private AudioClip currentMusicClip;

    public SoundManager(AudioHandler audioHandler) {
        this.audioHandler = audioHandler;
        this.audioClips = new HashMap<>();
        audioHandler.init();
    }

    public void playSFX(String id) {
        AudioClip clip = audioClips.get(id);
        if (clip != null) {
            audioHandler.playSFX(clip, sfxVolume * masterVolume);
        }
    }

    public void playMusic(String id, boolean loop) {
        AudioClip clip = audioClips.get(id);
        if (clip != null) {
            playingMusicId = id;
            currentMusicClip = clip;
            audioHandler.playMusic(clip, loop, musicVolume * masterVolume);
        }
    }

    public void stopMusic() {
        audioHandler.stopMusic();
        playingMusicId = null;
        currentMusicClip = null;
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateVolumes();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusicClip != null) {
            audioHandler.setVolume(currentMusicClip, musicVolume * masterVolume);
        }
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public void preload(String id, AudioClip clip) {
        audioClips.put(id, clip);
    }

    private void updateVolumes() {
        if (currentMusicClip != null) {
            audioHandler.setVolume(currentMusicClip, musicVolume * masterVolume);
        }
    }

    public void shutdown() {
        audioHandler.shutdown();
    }

}
