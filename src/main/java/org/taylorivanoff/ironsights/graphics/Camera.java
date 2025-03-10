package org.taylorivanoff.ironsights.graphics;

import java.lang.Math;

import org.joml.*;
import static org.lwjgl.glfw.GLFW.*;
import org.taylorivanoff.ironsights.core.*;

public class Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0);
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

    // Head bob parameters
    private float headBobTimer = 0.0f;
    private Vector3f headBobOffset = new Vector3f();
    private float headBobSpeed = 3.0f;
    private float headBobVerticalAmount = 0.005f;
    private float headBobHorizontalAmount = 0.003f;
    private boolean isActivelyMoving = false; // New movement state tracker

    public Camera(int width, int height) {
        projectionMatrix.setPerspective(
                (float) Math.toRadians(60.0f), // Field of view
                (float) width / height, // Aspect ratio
                0.1f, // Near plane
                1000.0f // Far plane
        );
    }

    public void update() {
        handleKeyboard();
        handleMouse();
        applyGravity();
        updateHeadBob();
        updateViewMatrix();
    }

    private void updateHeadBob() {
        float delta = Time.getDeltaTime();
        boolean shouldBob = isGrounded && isActivelyMoving;

        if (shouldBob) {
            headBobTimer += delta * headBobSpeed;
            float verticalOffset = (float) Math.sin(headBobTimer) * headBobVerticalAmount;
            float horizontalOffset = (float) Math.cos(headBobTimer * 2) * headBobHorizontalAmount;

            Vector3f forward = calculateForwardVector();
            Vector3f right = calculateRightVector(forward);
            Vector3f up = new Vector3f(0, 1, 0);

            // Calculate offset in camera's local space
            Vector3f localOffset = new Vector3f()
                    .add(right.mul(horizontalOffset))
                    .add(up.mul(verticalOffset));

            headBobOffset.set(localOffset);
        } else {
            // Smoothly return to neutral position
            headBobOffset.lerp(new Vector3f(0, 0, 0), 5f * delta);
            if (headBobOffset.lengthSquared() < 1e-6f) {
                headBobOffset.set(0, 0, 0);
            }
            headBobTimer = 0;
        }
    }

    private void handleKeyboard() {
        if (Input.ignoreInput)
            return;

        float delta = Time.getDeltaTime();

        isActivelyMoving = Input.isKeyDown(GLFW_KEY_W) ||
                Input.isKeyDown(GLFW_KEY_S) ||
                Input.isKeyDown(GLFW_KEY_A) ||
                Input.isKeyDown(GLFW_KEY_D);

        // Update speed modifiers
        currentSpeed = baseSpeed;
        if (isGrounded) {
            if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT))
                currentSpeed *= sprintMultiplier;
            if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
                currentSpeed *= crouchMultiplier;
                normalYPosition = crouchYOffset;
            } else {
                normalYPosition = 0.0f;
            }
        }

        // Calculate movement direction
        Vector3f movement = new Vector3f();
        if (isActivelyMoving) {
            Vector3f forward = calculateForwardVector();
            Vector3f right = calculateRightVector(forward);
            Vector3f horizontalForward = new Vector3f(forward.x, 0, forward.z).normalize();

            if (Input.isKeyDown(GLFW_KEY_W))
                movement.add(horizontalForward);
            if (Input.isKeyDown(GLFW_KEY_S))
                movement.sub(horizontalForward);
            if (Input.isKeyDown(GLFW_KEY_A))
                movement.sub(right);
            if (Input.isKeyDown(GLFW_KEY_D))
                movement.add(right);

            if (movement.lengthSquared() > 0) {
                movement.normalize().mul(currentSpeed);
            }
        }

        // Apply movement
        velocity.set(movement);
        position.add(velocity.mul(delta));

        // Handle jumping
        if (Input.isKeyDown(GLFW_KEY_SPACE) && isGrounded) {
            verticalVelocity = jumpForce;
            isGrounded = false;
        }
    }

    private void applyGravity() {
        float delta = Time.getDeltaTime();

        if (!isGrounded) {
            verticalVelocity += gravity * delta;
            position.add(velocity.mul(delta)); // Maintain horizontal velocity
        }

        position.y += verticalVelocity * delta;

        if (position.y <= normalYPosition) {
            position.y = normalYPosition;
            verticalVelocity = 0.0f;
            isGrounded = true;
            velocity.set(0, 0, 0); // Reset velocity when landing
        }
    }

    private Vector3f calculateForwardVector() {
        Vector3f forward = new Vector3f();

        float yaw = (float) Math.toRadians(rotation.y);
        float pitch = (float) Math.toRadians(rotation.x);

        forward.x = (float) (Math.cos(yaw) * Math.cos(pitch));
        forward.y = (float) Math.sin(pitch);
        forward.z = (float) (Math.sin(yaw) * Math.cos(pitch));

        return forward.normalize();
    }

    private Vector3f calculateRightVector(Vector3f forward) {
        Vector3f right = new Vector3f();
        Vector3f up = new Vector3f(0, 1, 0);

        forward.cross(up, right);
        return right.normalize();
    }

    private void handleMouse() {
        if (!Input.isCursorLocked() || Input.isInputIgnored())
            return;

        float mouseDX = (float) Input.getMouseDX();
        float mouseDY = (float) Input.getMouseDY();

        if (firstMouseInput) {
            firstMouseInput = false;
            return;
        }

        rotation.y += mouseDX * mouseSensitivity; // Yaw (left/right)
        rotation.x -= mouseDY * mouseSensitivity; // Pitch (up/down)

        rotation.x = Math.max(-89, Math.min(89, rotation.x));
    }

    private void updateViewMatrix() {
        Vector3f forward = calculateForwardVector();
        Vector3f viewPosition = new Vector3f(position).add(headBobOffset);
        Vector3f target = new Vector3f(viewPosition).add(forward);
        Vector3f up = new Vector3f(0, 1, 0);

        viewMatrix.identity().lookAt(viewPosition, target, up);
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