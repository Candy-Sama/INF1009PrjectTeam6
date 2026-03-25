package com.team6.arcadesim.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;

public abstract class AbstractScene implements Disposable {

    private String sceneName;
    protected AbstractGameMaster gameMaster;
    
    protected EntityManager entityManager;
    private final List<InputProcessor> sceneInputProcessors;
    private final List<InputProcessor> sceneInputProcessorsFirst;
    private boolean inputProcessorsAttached;

    public AbstractScene(AbstractGameMaster gameMaster, String sceneName) {
        this.gameMaster = gameMaster;
        this.sceneName = sceneName;
        this.entityManager = new EntityManager();
        this.sceneInputProcessors = new ArrayList<>();
        this.sceneInputProcessorsFirst = new ArrayList<>();
        this.inputProcessorsAttached = false;
    }

    public abstract void onEnter();
    public void onPause(){}
    public void onResume(){}
    public abstract void onExit();
    public abstract void update(float dt);
    public abstract void render(float dt);

    public String getName() { return sceneName; }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Register an input processor owned by this scene.
     * SceneManager will automatically attach/detach it during push/pop/pause/resume.
     */
    protected final void registerSceneInputProcessor(InputProcessor processor) {
        if (processor == null || sceneInputProcessors.contains(processor) || sceneInputProcessorsFirst.contains(processor)) {
            return;
        }
        sceneInputProcessors.add(processor);
        if (inputProcessorsAttached) {
            gameMaster.getInputManager().addInputProcessor(processor);
        }
    }

    /**
     * Register an input processor owned by this scene with top priority.
     */
    protected final void registerSceneInputProcessorFirst(InputProcessor processor) {
        if (processor == null || sceneInputProcessors.contains(processor) || sceneInputProcessorsFirst.contains(processor)) {
            return;
        }
        sceneInputProcessorsFirst.add(processor);
        if (inputProcessorsAttached) {
            gameMaster.getInputManager().addInputProcessorFirst(processor);
        }
    }

    protected final void unregisterSceneInputProcessor(InputProcessor processor) {
        if (processor == null) {
            return;
        }
        sceneInputProcessors.remove(processor);
        sceneInputProcessorsFirst.remove(processor);
        gameMaster.getInputManager().removeInputProcessor(processor);
    }

    public final void attachSceneInputProcessors() {
        if (inputProcessorsAttached) {
            return;
        }

        // Add "first" processors in reverse registration order to preserve intended ordering.
        for (int i = sceneInputProcessorsFirst.size() - 1; i >= 0; i--) {
            gameMaster.getInputManager().addInputProcessorFirst(sceneInputProcessorsFirst.get(i));
        }
        for (InputProcessor processor : sceneInputProcessors) {
            gameMaster.getInputManager().addInputProcessor(processor);
        }
        inputProcessorsAttached = true;
    }

    public final void detachSceneInputProcessors() {
        if (!inputProcessorsAttached) {
            return;
        }
        for (InputProcessor processor : sceneInputProcessorsFirst) {
            gameMaster.getInputManager().removeInputProcessor(processor);
        }
        for (InputProcessor processor : sceneInputProcessors) {
            gameMaster.getInputManager().removeInputProcessor(processor);
        }
        inputProcessorsAttached = false;
    }

    @Override
    public void dispose() {
        detachSceneInputProcessors();
        entityManager.removeAll();
    }
}
