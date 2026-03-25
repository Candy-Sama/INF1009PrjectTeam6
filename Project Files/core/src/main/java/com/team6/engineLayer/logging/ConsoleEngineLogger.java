package com.team6.engineLayer.logging;

public class ConsoleEngineLogger implements EngineLogger {

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void warn(String message) {
        System.err.println("WARN: " + message);
    }

    @Override
    public void error(String message) {
        System.err.println("ERROR: " + message);
    }
}
