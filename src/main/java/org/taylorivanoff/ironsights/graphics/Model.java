package org.taylorivanoff.ironsights.graphics;

import org.joml.Matrix4f;
import java.util.*;

public class Model {
    private List<Mesh> meshes = new ArrayList<>();

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public void render(Matrix4f modelMatrix, Shader shader) {
        shader.setUniform4f("model", modelMatrix);
        for (Mesh mesh : meshes) {
            mesh.render(shader);
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshes) {
            mesh.cleanup();
        }
    }
}