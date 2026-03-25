package com.team6.engineLayer.logging;

public class NoOpEngineLogger implements EngineLogger {

    @Override
    public void info(String message) {
    }

    @Override
    public void warn(String message) {
    }

    @Override
    public void error(String message) {
    }
}
