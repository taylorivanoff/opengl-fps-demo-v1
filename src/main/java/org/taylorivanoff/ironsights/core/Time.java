package org.taylorivanoff.ironsights.core;

public class Time {
    private static double lastTime = 0;
    private static double deltaTime = 0;

    public static void update() {
        double currentTime = System.nanoTime() / 1_000_000_000.0; // Convert to seconds
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;
    }

    public static float getDeltaTime() {
        return (float) deltaTime;
    }
}