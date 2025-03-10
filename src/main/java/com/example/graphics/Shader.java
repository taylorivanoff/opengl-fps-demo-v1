package com.example.graphics;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class Shader {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public Shader(String vertexPath, String fragmentPath) {
        programID = GL20.glCreateProgram();
        vertexShaderID = loadShader(vertexPath, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        checkShaderError(programID, GL20.GL_LINK_STATUS, true, "Program linking failed");
    }

    private int loadShader(String path, int type) {
        try {
            String source = new String(Files.readAllBytes(Paths.get(path)));
            int shaderID = GL20.glCreateShader(type);
            GL20.glShaderSource(shaderID, source);
            GL20.glCompileShader(shaderID);

            checkShaderError(shaderID, GL20.GL_COMPILE_STATUS, false, "Shader compilation failed: " + path);
            return shaderID;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }

    private void checkShaderError(int shaderID, int flag, boolean isProgram, String errorMessage) {
        int success = isProgram ? GL20.glGetProgrami(shaderID, flag) : GL20.glGetShaderi(shaderID, flag);
        if (success == GL20.GL_FALSE) {
            String log = isProgram ? GL20.glGetProgramInfoLog(shaderID) : GL20.glGetShaderInfoLog(shaderID);
            throw new RuntimeException(errorMessage + ": " + log);
        }
    }

    public void bind() {
        GL20.glUseProgram(programID);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void setUniform4f(String name, Matrix4f value) {
        int location = GL20.glGetUniformLocation(programID, name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniform3f(String name, Vector3f value) {
        int location = GL20.glGetUniformLocation(programID, name);
        GL20.glUniform3f(location, value.x, value.y, value.z);
    }

    public void setUniform1f(String name, float value) {
        int location = GL20.glGetUniformLocation(programID, name);
        GL20.glUniform1f(location, value);
    }

    public void setUniform1i(String name, int value) {
        int location = GL20.glGetUniformLocation(programID, name);
        GL20.glUniform1i(location, value);
    }

    public void cleanup() {
        unbind();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }
}
