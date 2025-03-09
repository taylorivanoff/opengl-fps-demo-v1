package org.taylorivanoff.ironsights.core;

import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import org.taylorivanoff.ironsights.graphics.Camera;
import org.taylorivanoff.ironsights.graphics.Model;
import org.taylorivanoff.ironsights.graphics.Renderer;

public class Game {
    private Window window;
    private Renderer renderer;
    private Camera camera;
    private Model cube;

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
            cube = ResourceManager.getInstance().loadModel(
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

        // Update game state here
        if (Input.isKeyDown(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getWindowHandle(), true);
        }
    }

    /**
     * ░█▀▄░█▀▀░█▀█░█▀▄░█▀▀░█▀▄░░░█░░░█▀█░█▀█░█▀█
     * ░█▀▄░█▀▀░█░█░█░█░█▀▀░█▀▄░░░█░░░█░█░█░█░█▀▀
     * ░▀░▀░▀▀▀░▀░▀░▀▀░░▀▀▀░▀░▀░░░▀▀▀░▀▀▀░▀▀▀░▀░░
     */
    private void render() {
        renderer.clear();

        // Render game objects here
        Matrix4f modelMatrix = new Matrix4f()
                .translate(4, 0, 0); // Move cube 5 units away from camera
        renderer.renderModel(cube, modelMatrix, camera);
    }

    private void cleanup() {
        cube.cleanup();
        renderer.cleanup();
        window.cleanup();
    }

    public static void main(String[] args) {
        new Game().run();
    }
}