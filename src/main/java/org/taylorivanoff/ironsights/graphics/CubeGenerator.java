package org.taylorivanoff.ironsights.graphics;

public class CubeGenerator {
    public static Model createCube(float size, String texturePath) {
        float halfSize = size / 2.0f;

        // Vertices for a cube
        float[] vertices = {
                // Front face
                -halfSize, -halfSize, halfSize,
                halfSize, -halfSize, halfSize,
                halfSize, halfSize, halfSize,
                -halfSize, halfSize, halfSize,

                // Back face
                -halfSize, -halfSize, -halfSize,
                halfSize, -halfSize, -halfSize,
                halfSize, halfSize, -halfSize,
                -halfSize, halfSize, -halfSize
        };

        // Texture coordinates
        float[] texCoords = {
                // Front face
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                // Back face
                1.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f
        };

        // Normals (pointing outwards)
        float[] normals = {
                // Front face
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                // Back face
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f
        };

        // Indices (two triangles per face)
        int[] indices = {
                // Front face
                0, 1, 2,
                2, 3, 0,

                // Back face
                4, 5, 6,
                6, 7, 4,

                // Left face
                4, 0, 3,
                3, 7, 4,

                // Right face
                1, 5, 6,
                6, 2, 1,

                // Top face
                3, 2, 6,
                6, 7, 3,

                // Bottom face
                4, 5, 1,
                1, 0, 4
        };

        Texture texture = new Texture(texturePath);
        Mesh mesh = new Mesh(vertices, texCoords, normals, indices, texture);

        Model model = new Model();
        model.addMesh(mesh);
        return model;
    }
}