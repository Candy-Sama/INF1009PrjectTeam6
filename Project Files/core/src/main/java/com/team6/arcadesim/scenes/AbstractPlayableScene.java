package com.team6.arcadesim.scenes;

import com.team6.arcadesim.systems.SystemPipeline;
import com.team6.arcadesim.AbstractGameMaster;

public abstract class AbstractPlayableScene extends AbstractScene {

    private final SystemPipeline systemPipeline;

    public AbstractPlayableScene(AbstractGameMaster gameMaster, String sceneName) {
        super(gameMaster, sceneName);
        this.systemPipeline = new SystemPipeline();
        configureSystems(systemPipeline);
    }

    @Override
    public final void update(float dt) {
        // specific playable scenes handle their own custom logic and inputs first
        processLevelLogic(dt);
        systemPipeline.update(dt, gameMaster, getEntityManager());
    }

    protected void configureSystems(SystemPipeline pipeline) {
        // Subclasses configure whichever systems they need (and in what order).
    }

    protected abstract void processLevelLogic(float dt);
}
