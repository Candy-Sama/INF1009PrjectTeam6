package com.team6.arcadesim.managers;

import com.team6.arcadesim.interfaces.AudioHandler;
import com.team6.arcadesim.ecs.AudioClip;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

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
        
        // --- DEFAULT HANDLER (Bridge Implementation) ---
        // In a full engine, this might be a separate class 'LibGDXAudioHandler'
        this.audioHandler = new AudioHandler() {
            private com.badlogic.gdx.audio.Music currentMusic;

            @Override
            public void init() { /* LibGDX audio is static, no init needed */ }

            @Override
            public void playSFX(AudioClip clip, float volume) {
                if (clip != null && !clip.isMusic()) {
                    clip.getSound().play(volume);
                }
            }

            @Override
            public void playMusic(AudioClip clip, boolean loop, float volume) {
                stopMusic(); // Stop old music first
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
                // Not easily applicable to fire-and-forget SFX, mostly for music
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

    // --- Public API ---

    /**
     * Loads a sound into memory so it can be played by string ID.
     */
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

    // --- Volume Controls ---

    public void setMasterVolume(float v) {
        this.masterVolume = Math.max(0, Math.min(1, v)); // Clamp 0.0 to 1.0
        // Update currently playing music volume immediately
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