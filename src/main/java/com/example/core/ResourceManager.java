package com.example.core;

import java.util.HashMap;
import java.util.Map;

import com.example.graphics.Model;
import com.example.graphics.OBJLoader;
import com.example.graphics.Texture;

public class ResourceManager {
    private static ResourceManager instance;
    private Map<String, Model> models = new HashMap<>();
    private Map<String, Texture> textures = new HashMap<>();

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public Model loadModel(String modelPath, String texturePath) throws Exception {
        String key = modelPath + "|" + texturePath;
        if (!models.containsKey(key)) {
            Model model = OBJLoader.loadModel(modelPath, texturePath);
            models.put(key, model);
        }
        return models.get(key);
    }

    public void cleanup() {
        models.values().forEach(Model::cleanup);
        models.clear();
        textures.values().forEach(Texture::cleanup);
        textures.clear();
    }
}
