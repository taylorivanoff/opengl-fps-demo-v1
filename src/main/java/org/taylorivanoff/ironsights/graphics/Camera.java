package org.taylorivanoff.ironsights.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import org.taylorivanoff.ironsights.core.Input;
import org.taylorivanoff.ironsights.core.Time;

public class Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0); // Pitch, Yaw, Roll
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    private boolean firstMouseInput = true;

    // Movement parameters
    private float baseSpeed = 5.0f;
    private float currentSpeed = 5.0f;
    private float sprintMultiplier = 1.8f;
    private float crouchMultiplier = 0.5f;
    private float mouseSensitivity = 0.1f;
    private Vector3f velocity = new Vector3f(0, 0, 0);

    // Jump/gravity parameters
    private float verticalVelocity = 0.0f;
    private float gravity = -15.0f;
    private float jumpForce = 7.0f;
    private boolean isGrounded = true;
    private float crouchYOffset = -0.5f;
    private float normalYPosition = 0.0f;

    public Camera(int width, int height) {
        projectionMatrix.setPerspective(
                (float) Math.toRadians(70.0f), // Field of view
                (float) width / height, // Aspect ratio
                0.1f, // Near plane
                1000.0f // Far plane
        );
    }

    public void update() {
        handleKeyboard();
        handleMouse();
        applyGravity();
        updateViewMatrix();
    }

    private void handleKeyboard() {
        float delta = Time.getDeltaTime();

        // Only allow movement if grounded
        if (isGrounded) {
            currentSpeed = baseSpeed;

            // Sprint mechanic
            if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                currentSpeed *= sprintMultiplier;
            }

            // Crouch mechanic
            if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
                currentSpeed *= crouchMultiplier;
                normalYPosition = crouchYOffset; // Lower camera height
            } else {
                normalYPosition = 0.0f; // Reset camera height
            }

            // Get movement direction
            Vector3f forward = calculateForwardVector();
            Vector3f right = calculateRightVector(forward);
            Vector3f horizontalForward = new Vector3f(forward.x, 0, forward.z).normalize();

            // Movement inputs
            Vector3f movement = new Vector3f();
            if (Input.isKeyDown(GLFW_KEY_W)) {
                movement.add(horizontalForward);
            }
            if (Input.isKeyDown(GLFW_KEY_S)) {
                movement.sub(horizontalForward);
            }
            if (Input.isKeyDown(GLFW_KEY_A)) {
                movement.sub(right);
            }
            if (Input.isKeyDown(GLFW_KEY_D)) {
                movement.add(right);
            }

            // Normalize movement to avoid faster diagonal movement
            if (movement.lengthSquared() > 0) {
                movement.normalize().mul(currentSpeed * delta);
            }

            // Update position
            position.add(movement);

            // Store velocity for when jumping
            velocity.set(movement);
        }

        // Jump mechanic
        if (Input.isKeyDown(GLFW_KEY_SPACE) && isGrounded) {
            verticalVelocity = jumpForce;
            isGrounded = false;
        }
    }

    private void handleMouse() {

        // Get mouse movement delta
        float mouseDX = (float) Input.getMouseDX();
        float mouseDY = (float) Input.getMouseDY();

        if (firstMouseInput) {
            firstMouseInput = false;
            return; // Skip this frame's movement
        }

        // Adjust camera rotation based on mouse movement
        rotation.y += mouseDX * mouseSensitivity; // Yaw (left/right)
        rotation.x -= mouseDY * mouseSensitivity; // Pitch (up/down)

        // Clamp pitch to avoid flipping
        rotation.x = Math.max(-89, Math.min(89, rotation.x));
    }

    private void applyGravity() {
        float delta = Time.getDeltaTime();

        // Apply gravity while airborne
        if (!isGrounded) {
            verticalVelocity += gravity * delta;
            position.add(velocity.mul(delta, new Vector3f())); // Apply stored momentum
        }

        position.y += verticalVelocity * delta;

        // Ground check
        float groundLevel = normalYPosition;
        if (position.y <= groundLevel) {
            position.y = groundLevel;
            verticalVelocity = 0.0f;
            isGrounded = true;
            velocity.set(0, 0, 0); // Reset stored velocity on landing
        }
    }

    private Vector3f calculateForwardVector() {
        Vector3f forward = new Vector3f();

        float yaw = (float) Math.toRadians(rotation.y);
        float pitch = (float) Math.toRadians(rotation.x);

        // Calculate forward vector using yaw and pitch
        forward.x = (float) (Math.cos(yaw) * Math.cos(pitch));
        forward.y = (float) Math.sin(pitch);
        forward.z = (float) (Math.sin(yaw) * Math.cos(pitch));

        return forward.normalize();
    }

    private Vector3f calculateRightVector(Vector3f forward) {
        Vector3f right = new Vector3f();
        Vector3f up = new Vector3f(0, 1, 0);

        // Calculate right vector using cross product
        forward.cross(up, right); // Forward x Up = Right

        return right.normalize();
    }

    private void updateViewMatrix() {
        Vector3f forward = calculateForwardVector();
        Vector3f target = new Vector3f(position).add(forward);
        Vector3f up = new Vector3f(0, 1, 0);

        viewMatrix.identity().lookAt(position, target, up);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }
}