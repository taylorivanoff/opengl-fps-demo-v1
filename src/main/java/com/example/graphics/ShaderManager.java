package com.example.graphics;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static ShaderManager instance;
    private Map<String, Shader> shaders = new HashMap<>();

    private ShaderManager() {
        loadShaders();
    }

    public static ShaderManager getInstance() {
        if (instance == null) {
            instance = new ShaderManager();
        }
        return instance;
    }

    private void loadShaders() {
        shaders.put("default", new Shader(
                "shaders/default.vert",
                "shaders/default.frag"));

        // Add more shaders as needed
    }

    public Shader getShader(String name) {
        Shader shader = shaders.get(name);
        if (shader == null) {
            throw new RuntimeException("Shader not found: " + name);
        }
        return shader;
    }

    public void cleanup() {
        shaders.values().forEach(Shader::cleanup);
        shaders.clear();
    }
}
