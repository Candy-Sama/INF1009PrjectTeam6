package com.team6.arcadesim.sandbox.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.ecs.AudioClip;
import com.team6.arcadesim.events.EngineEventListener;
import com.team6.arcadesim.sandbox.events.SandboxAudioEvent;

public class SandboxAudioService implements EngineEventListener<SandboxAudioEvent> {

    private static final String MENU_BGM_ID = "sandbox.menu_bgm";
    private static final String SANDBOX_BGM_ID = "sandbox.sim_bgm";
    private static final String SPAWN_SFX_ID = "sandbox.spawn_sfx";
    private static final String BOOM_SFX_ID = "sandbox.boom_sfx";
    private static boolean assetsPreloaded = false;

    private final AbstractGameMaster gameMaster;

    public SandboxAudioService(AbstractGameMaster gameMaster) {
        this.gameMaster = gameMaster;
        preloadAssetsIfNeeded();
    }

    public void playMenuBgm() {
        gameMaster.getSoundManager().playMusic(MENU_BGM_ID, true);
    }

    public void stopMenuBgm() {
        gameMaster.getSoundManager().stopMusic();
    }

    public void playSandboxBgm() {
        gameMaster.getSoundManager().playMusic(SANDBOX_BGM_ID, true);
    }

    public void stopSandboxBgm() {
        gameMaster.getSoundManager().stopMusic();
    }

    @Override
    public void onEvent(SandboxAudioEvent event) {
        if (event == null || event.getType() == null) {
            return;
        }

        switch (event.getType()) {
            case ENTITY_SPAWNED:
                playSpawnSfx();
                break;
            case MUTUAL_DESTRUCTION:
                playBoomSfx();
                break;
            default:
                break;
        }
    }

    public void dispose() {
        // SoundManager owns the clips lifecycle and disposes them on app shutdown.
    }

    private void playSpawnSfx() {
        gameMaster.getSoundManager().playSFX(SPAWN_SFX_ID);
    }

    private void playBoomSfx() {
        gameMaster.getSoundManager().playSFX(BOOM_SFX_ID);
    }

    private void preloadAssetsIfNeeded() {
        if (assetsPreloaded) {
            return;
        }
        preloadMusic(MENU_BGM_ID, "audio/menu_bgm.wav");
        preloadMusic(SANDBOX_BGM_ID, "audio/sandbox_bgm.wav");
        preloadSound(SPAWN_SFX_ID, "audio/spawn.wav");
        preloadSound(BOOM_SFX_ID, "audio/boom.wav");
        assetsPreloaded = true;
    }

    private void preloadMusic(String id, String path) {
        try {
            if (!Gdx.files.internal(path).exists()) {
                System.err.println("Missing music file: " + path);
                return;
            }
            Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
            gameMaster.getSoundManager().preload(id, new AudioClip(music));
        } catch (Exception ex) {
            System.err.println("Failed to load music: " + path + " (" + ex.getMessage() + ")");
        }
    }

    private void preloadSound(String id, String path) {
        try {
            if (!Gdx.files.internal(path).exists()) {
                System.err.println("Missing sound file: " + path);
                return;
            }
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            gameMaster.getSoundManager().preload(id, new AudioClip(sound));
        } catch (Exception ex) {
            System.err.println("Failed to load sound: " + path + " (" + ex.getMessage() + ")");
        }
    }
}
