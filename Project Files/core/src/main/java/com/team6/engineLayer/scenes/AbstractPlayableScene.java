package com.team6.engineLayer.scenes;

import com.team6.engineLayer.AbstractGameMaster;
import com.team6.engineLayer.systems.SystemPipeline;

public abstract class AbstractPlayableScene extends AbstractScene {

    private final SystemPipeline systemPipeline;
    private boolean simulationRunning;

    public AbstractPlayableScene(AbstractGameMaster gameMaster, String sceneName) {
        this(gameMaster, sceneName, true);
    }

    public AbstractPlayableScene(AbstractGameMaster gameMaster, String sceneName, boolean startSimulating) {
        super(gameMaster, sceneName);
        this.systemPipeline = new SystemPipeline();
        this.simulationRunning = startSimulating;
        configureSystems(systemPipeline);
    }

    @Override
    public final void update(float dt) {
        // specific playable scenes handle their own custom logic and inputs first
        processLevelLogic(dt);
        if (shouldRunSystems()) {
            systemPipeline.update(dt, gameMaster, getEntityManager());
        }
    }

    protected void configureSystems(SystemPipeline pipeline) {
        // Subclasses configure whichever systems they need (and in what order).
    }

    /**
     * Hook for scenes that need custom gating behavior on top of simulation state.
     */
    protected boolean shouldRunSystems() {
        return simulationRunning;
    }

    public void startSimulation() {
        simulationRunning = true;
    }

    public void pauseSimulation() {
        simulationRunning = false;
    }

    public void setSimulationRunning(boolean simulationRunning) {
        this.simulationRunning = simulationRunning;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }

    protected abstract void processLevelLogic(float dt);
}
