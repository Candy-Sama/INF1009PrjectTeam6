package com.team6.arcadesim.managers;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.ecs.AudioClip;
import com.team6.arcadesim.interfaces.AudioHandler;

public class SoundManager implements Disposable {

    // --- State Variables ---
    private AudioHandler audioHandler;
    private float masterVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private float musicVolume = 1.0f;
    private String playingMusicId;

    // --- Storage (The Library) ---
    private Map<String, AudioClip> soundLibrary;

    public SoundManager() {
        this.soundLibrary = new HashMap<>();
        
        this.audioHandler = new AudioHandler() {
            private com.badlogic.gdx.audio.Music currentMusic;

            @Override
            public void init() { }

            @Override
            public void playSFX(AudioClip clip, float volume) {
                if (clip != null && !clip.isMusic()) {
                    clip.getSound().play(volume);
                }
            }

            @Override
            public void playMusic(AudioClip clip, boolean loop, float volume) {
                stopMusic();
                if (clip != null && clip.isMusic()) {
                    currentMusic = clip.getMusic();
                    currentMusic.setLooping(loop);
                    currentMusic.setVolume(volume);
                    currentMusic.play();
                }
            }

            @Override
            public void stopMusic() {
                if (currentMusic != null) {
                    currentMusic.stop();
                }
            }

            @Override
            public void setVolume(AudioClip clip, float volume) {
                if (currentMusic != null && clip.isMusic() && clip.getMusic() == currentMusic) {
                    currentMusic.setVolume(volume);
                }
            }

            @Override
            public void shutdown() {
                stopMusic();
            }
        };
        
        audioHandler.init();
    }

    public void preload(String id, AudioClip clip) {
        soundLibrary.put(id, clip);
    }

    public void playSFX(String id) {
        AudioClip clip = soundLibrary.get(id);
        if (clip != null) {
            // Calculate final volume: Master * SFX
            float finalVol = masterVolume * sfxVolume;
            audioHandler.playSFX(clip, finalVol);
        } else {
            System.err.println("SoundManager: SFX not found - " + id);
        }
    }

    public void playMusic(String id, boolean loop) {
        AudioClip clip = soundLibrary.get(id);
        if (clip != null) {
            playingMusicId = id;
            float finalVol = masterVolume * musicVolume;
            audioHandler.playMusic(clip, loop, finalVol);
        } else {
            System.err.println("SoundManager: Music not found - " + id);
        }
    }

    public void stopMusic() {
        audioHandler.stopMusic();
        playingMusicId = null;
    }

    public void pauseMusic() {
        if (playingMusicId != null) {
            AudioClip clip = soundLibrary.get(playingMusicId);
            if (clip != null && clip.isMusic()) {
                clip.getMusic().pause();
            }
        }
    }

    public void resumeMusic() {
        if (playingMusicId != null) {
            AudioClip clip = soundLibrary.get(playingMusicId);
            if (clip != null && clip.isMusic()) {
                clip.getMusic().play();
            }
        }
    }

    // --- Volume Controls ---

    public void setMasterVolume(float v) {
        this.masterVolume = Math.max(0, Math.min(1, v));
        if (playingMusicId != null) {
            AudioClip clip = soundLibrary.get(playingMusicId);
            audioHandler.setVolume(clip, masterVolume * musicVolume);
        }
    }

    public void setSFXVolume(float v) {
        this.sfxVolume = Math.max(0, Math.min(1, v));
    }

    public void setMusicVolume(float v) {
        this.musicVolume = Math.max(0, Math.min(1, v));
        if (playingMusicId != null) {
            AudioClip clip = soundLibrary.get(playingMusicId);
            audioHandler.setVolume(clip, masterVolume * musicVolume);
        }
    }

    @Override
    public void dispose() {
        audioHandler.shutdown();
        for (AudioClip clip : soundLibrary.values()) {
            clip.dispose();
        }
        soundLibrary.clear();
    }
}