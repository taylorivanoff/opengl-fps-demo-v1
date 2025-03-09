package org.taylorivanoff.ironsights.core;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class Input {
    private static boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private static double mouseX, mouseY;
    private static double mouseDX, mouseDY;
    private static double lastMouseX, lastMouseY;
    private static boolean firstMouse = true;
    private static long windowHandle;
    private static boolean cursorLocked = false;
    public static boolean ignoreInput = true;
    private static boolean ignoreNextMouseInput = false;

    public static void init(long window) {
        windowHandle = window;

        // Keyboard input
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            keys[key] = action != GLFW_RELEASE;

            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                setCursorLocked(false);
            }
        });

        // Mouse input
        glfwSetCursorPosCallback(window, (w, xPos, yPos) -> {
            mouseX = xPos;
            mouseY = yPos;

            // Handle first mouse movement
            if (firstMouse || !cursorLocked) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                firstMouse = false;
            }
        });

        // Start with input disabled
        setCursorLocked(false);
        ignoreInput = true;
    }

    public static boolean isInputIgnored() {
        return ignoreInput;
    }

    public static void setCursorLocked(boolean locked) {
        cursorLocked = locked;
        glfwSetInputMode(windowHandle, GLFW_CURSOR,
                locked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);

        if (locked) {
            // Center cursor without creating delta
            int[] width = new int[1], height = new int[1];
            glfwGetWindowSize(windowHandle, width, height);
            double centerX = width[0] / 2.0;
            double centerY = height[0] / 2.0;

            // Set actual cursor position
            glfwSetCursorPos(windowHandle, centerX, centerY);

            // Update internal tracking without creating delta
            lastMouseX = centerX;
            lastMouseY = centerY;
            mouseX = centerX;
            mouseY = centerY;

            ignoreNextMouseInput = true;
        }

        ignoreInput = !locked;
    }

    public static boolean isCursorLocked() {
        return cursorLocked;
    }

    public static void update() {
        if (ignoreInput)
            return;

        if (ignoreNextMouseInput) {
            ignoreNextMouseInput = false;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return;
        }

        // Calculate mouse movement delta
        mouseDX = mouseX - lastMouseX;
        mouseDY = mouseY - lastMouseY;

        // Update last mouse position
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public static boolean isKeyDown(int key) {
        return !ignoreInput && keys[key];
    }

    public static double getMouseDX() {
        return mouseDX;
    }

    public static double getMouseDY() {
        return mouseDY;
    }
}