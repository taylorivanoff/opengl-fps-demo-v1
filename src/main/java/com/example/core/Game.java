package com.example.core;

import org.joml.Matrix4f;

import com.example.graphics.Camera;
import com.example.graphics.Model;
import com.example.graphics.Renderer;

public class Game {
    private Window window;
    private Renderer renderer;
    private Camera camera;
    private Model scene;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        window = new Window(1280, 720, "OpenGL FPS Demo v1");
        camera = new Camera(window.getWidth(), window.getHeight());
        renderer = new Renderer();
        Input.init(window.getWindowHandle());

        try {
            scene = ResourceManager.getInstance().loadModel(
                    "assets/models/scene.obj",
                    "assets/textures/cobble.jpeg");
        } catch (Exception ex) {
        }
    }

    private void loop() {
        while (!window.shouldClose()) {
            Time.update();
            Input.update();

            update();
            render();

            window.update();
        }
    }

    private void update() {
        camera.update();
    }

    private void render() {
        renderer.clear();
        Matrix4f modelMatrix = new Matrix4f().translate(4, -2f, 0);
        renderer.renderModel(scene, modelMatrix, camera);
    }

    private void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        window.cleanup();
    }

    public static void main(String[] args) {
        new Game().run();
    }
}