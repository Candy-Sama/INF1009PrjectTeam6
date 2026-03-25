package com.team6.arcadesim.ecs;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * A wrapper class that can hold either a Sound (SFX) or Music (Streaming).
 */
public class AudioClip implements Disposable {
    private Sound sound;
    private Music music;
    private boolean isMusic;

    public AudioClip(Sound sound) {
        this.sound = sound;
        this.isMusic = false;
    }

    public AudioClip(Music music) {
        this.music = music;
        this.isMusic = true;
    }

    public Sound getSound() { return sound; }
    public Music getMusic() { return music; }
    public boolean isMusic() { return isMusic; }

    @Override
    public void dispose() {
        if (isMusic && music != null) music.dispose();
        if (!isMusic && sound != null) sound.dispose();
    }
}