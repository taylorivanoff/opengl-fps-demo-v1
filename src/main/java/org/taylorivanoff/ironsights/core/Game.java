package org.taylorivanoff.ironsights.core;

import org.joml.Matrix4f;
import org.taylorivanoff.ironsights.graphics.Camera;
import org.taylorivanoff.ironsights.graphics.Model;
import org.taylorivanoff.ironsights.graphics.Renderer;

public class Game {
    private Window window;
    private Renderer renderer;
    private Camera camera;
    private Model monkey;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        window = new Window(1280, 720, "Iron Sights");
        camera = new Camera(window.getWidth(), window.getHeight());
        renderer = new Renderer();
        Input.init(window.getWindowHandle());

        try {
            monkey = ResourceManager.getInstance().loadModel(
                    "assets/models/monkey.obj",
                    "assets/textures/cube.png");
        } catch (Exception ex) {
        }
    }

    /**
     * ░█▀▀░█▀█░█▄█░█▀▀░░░█░░░█▀█░█▀█░█▀█
     * ░█░█░█▀█░█░█░█▀▀░░░█░░░█░█░█░█░█▀▀
     * ░▀▀▀░▀░▀░▀░▀░▀▀▀░░░▀▀▀░▀▀▀░▀▀▀░▀░░
     */
    private void loop() {
        while (!window.shouldClose()) {
            Time.update();
            Input.update();

            update();
            render();

            window.update();
        }
    }

    /**
     * ░█░█░█▀█░█▀▄░█▀█░▀█▀░█▀▀░░░█░░░█▀█░█▀█░█▀█
     * ░█░█░█▀▀░█░█░█▀█░░█░░█▀▀░░░█░░░█░█░█░█░█▀▀
     * ░▀▀▀░▀░░░▀▀░░▀░▀░░▀░░▀▀▀░░░▀▀▀░▀▀▀░▀▀▀░▀░░
     */
    private void update() {
        camera.update();
    }

    /**
     * ░█▀▄░█▀▀░█▀█░█▀▄░█▀▀░█▀▄░░░█░░░█▀█░█▀█░█▀█
     * ░█▀▄░█▀▀░█░█░█░█░█▀▀░█▀▄░░░█░░░█░█░█░█░█▀▀
     * ░▀░▀░▀▀▀░▀░▀░▀▀░░▀▀▀░▀░▀░░░▀▀▀░▀▀▀░▀▀▀░▀░░
     */
    private void render() {
        renderer.clear();

        // Render game objects here
        Matrix4f modelMatrix = new Matrix4f().translate(4, 0, 0); // Move cube 5 units away from camera
        renderer.renderModel(monkey, modelMatrix, camera);
    }

    private void cleanup() {
        monkey.cleanup();
        renderer.cleanup();
        window.cleanup();
    }

    public static void main(String[] args) {
        new Game().run();
    }
}