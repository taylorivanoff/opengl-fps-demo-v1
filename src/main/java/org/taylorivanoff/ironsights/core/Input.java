package org.taylorivanoff.ironsights.core;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class Input {
    private static boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private static double mouseX, mouseY;
    private static double mouseDX, mouseDY;
    private static double lastMouseX, lastMouseY;
    private static boolean firstMouse = true; // Track first mouse movement

    public static void init(long window) {
        // Keyboard input
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            keys[key] = action != GLFW_RELEASE;
        });

        // Mouse input
        glfwSetCursorPosCallback(window, (w, xPos, yPos) -> {
            mouseX = xPos;
            mouseY = yPos;

            // Handle first mouse movement
            if (firstMouse) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                firstMouse = false;
            }
        });

        // Hide and capture the cursor
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public static void update() {
        // Calculate mouse movement delta
        mouseDX = mouseX - lastMouseX;
        mouseDY = mouseY - lastMouseY;

        // Update last mouse position
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public static boolean isKeyDown(int key) {
        return keys[key];
    }

    public static double getMouseDX() {
        return mouseDX;
    }

    public static double getMouseDY() {
        return mouseDY;
    }
}