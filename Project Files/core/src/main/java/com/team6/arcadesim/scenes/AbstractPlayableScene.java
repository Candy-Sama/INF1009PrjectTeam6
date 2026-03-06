package com.team6.arcadesim.scenes;

import com.team6.arcadesim.AbstractGameMaster;

public abstract class AbstractPlayableScene extends AbstractScene {

    public AbstractPlayableScene(AbstractGameMaster gameMaster, String sceneName) {
        super(gameMaster, sceneName);
    }

    @Override
    public final void update(float dt) {
        // specific playable scenes handler their own custom logic and inputs
        processLevelLogic(dt);

        // Automatically run engine managers for playable scenes.
        // Todo: create a GravityManager
        // gameMaster.getGravityManager().update(dt, getEntityManager().getAllEntities());

        gameMaster.getMovementManager().update(dt, getEntityManager().getAllEntities());
        gameMaster.getCollisionManager().update(dt, getEntityManager().getAllEntities());
    }

    protected abstract void processLevelLogic(float dt);
}
