package org.taylorivanoff.ironsights.core;

import java.util.HashMap;
import java.util.Map;

import org.taylorivanoff.ironsights.graphics.Model;
import org.taylorivanoff.ironsights.graphics.OBJLoader;
import org.taylorivanoff.ironsights.graphics.Texture;

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
