package com.team6.arcadesim.managers;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.team6.arcadesim.interfaces.AudioHandler;

public class SoundManager {
    private AudioHandler audioHandler;
    private float masterVolume = 1.0f;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private String playingMusicId;

    //Why Maps? Because we can map String IDs to audio resources for easy retrieval.
    private Map<String, Sound> soundEffects;
    private Map<String, Music> musicTracks;
    
    private Music currentMusic;

    public SoundManager(AudioHandler audioHandler) {
        if (audioHandler == null) {
            throw new IllegalArgumentException("AudioHandler cannot be null");
        }
        this.audioHandler = audioHandler;
        this.soundEffects = new HashMap<>();
        this.musicTracks = new HashMap<>();
        audioHandler.init();
    }

    public void playSFX(String id) {
        Sound sound = soundEffects.get(id);
        if (sound != null) {
            audioHandler.playSFX(sound, sfxVolume * masterVolume);
        }
    }

    public void playMusic(String id, boolean loop) {
        Music music = musicTracks.get(id);
        if (music != null) {
            playingMusicId = id;
            currentMusic = music;
            audioHandler.playMusic(music, loop, musicVolume * masterVolume);
        }
    }

    public void stopMusic() {
        audioHandler.stopMusic();
        playingMusicId = null;
        currentMusic = null;
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateMusicVolume();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            audioHandler.setMusicVolume(musicVolume * masterVolume);
        }
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public void preloadSFX(String id, Sound sound) {
        if (sound != null) {
            soundEffects.put(id, sound);
        }
    }

    public void preloadMusic(String id, Music music) {
        if (music != null) {
            musicTracks.put(id, music);
        }
    }

    private void updateMusicVolume() {
        if (currentMusic != null) {
            audioHandler.setMusicVolume(musicVolume * masterVolume);
        }
    }

    public void shutdown() {
        audioHandler.shutdown();
    }

}
