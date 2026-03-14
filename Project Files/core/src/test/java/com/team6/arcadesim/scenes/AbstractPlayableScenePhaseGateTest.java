package com.team6.arcadesim.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.systems.EngineSystem;
import com.team6.arcadesim.systems.SystemPipeline;

public class AbstractPlayableScenePhaseGateTest {

    @Test
    void systemsDoNotRunWhenSimulationIsPaused() {
        TestPlayableScene scene = new TestPlayableScene(false);

        scene.update(1f / 60f);
        assertEquals(1, scene.logicTickCount);
        assertEquals(0, scene.systemTickCount);

        scene.startSimulation();
        scene.update(1f / 60f);
        assertEquals(2, scene.logicTickCount);
        assertEquals(1, scene.systemTickCount);

        scene.pauseSimulation();
        scene.update(1f / 60f);
        assertEquals(3, scene.logicTickCount);
        assertEquals(1, scene.systemTickCount);
    }

    private static class TestPlayableScene extends AbstractPlayableScene {

        private int logicTickCount;
        private int systemTickCount;

        private TestPlayableScene(boolean startSimulating) {
            super(null, "TestPlayableScene", startSimulating);
            this.logicTickCount = 0;
            this.systemTickCount = 0;
        }

        @Override
        protected void configureSystems(SystemPipeline pipeline) {
            pipeline.addSystem(new EngineSystem() {
                @Override
                public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
                    systemTickCount++;
                }
            });
        }

        @Override
        protected void processLevelLogic(float dt) {
            logicTickCount++;
        }

        @Override
        public void onEnter() {
        }

        @Override
        public void onExit() {
        }

        @Override
        public void render(float dt) {
        }
    }
}
