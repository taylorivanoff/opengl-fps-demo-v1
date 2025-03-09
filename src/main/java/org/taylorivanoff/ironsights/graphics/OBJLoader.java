package org.taylorivanoff.ironsights.graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class OBJLoader {
    public static Model loadModel(String filePath, String texturePath) throws Exception {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        List<Float> vertexList = new ArrayList<>();
        List<Float> texCoordList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        Map<String, Integer> vertexMap = new HashMap<>();
        int index = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "v":
                        vertices.add(new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])));
                        break;
                    case "vt":
                        texCoords.add(new Vector2f(
                                Float.parseFloat(tokens[1]),
                                1 - Float.parseFloat(tokens[2])));
                        break;
                    case "vn":
                        normals.add(new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])));
                        break;
                    case "f":
                        for (int i = 1; i < tokens.length; i++) {
                            String[] parts = tokens[i].split("/");
                            String key = parts[0] + "/" + parts[1] + "/" + parts[2];

                            if (!vertexMap.containsKey(key)) {
                                vertexMap.put(key, index++);

                                Vector3f vertex = vertices.get(Integer.parseInt(parts[0]) - 1);
                                vertexList.add(vertex.x);
                                vertexList.add(vertex.y);
                                vertexList.add(vertex.z);

                                Vector2f texCoord = texCoords.get(Integer.parseInt(parts[1]) - 1);
                                texCoordList.add(texCoord.x);
                                texCoordList.add(texCoord.y);

                                Vector3f normal = normals.get(Integer.parseInt(parts[2]) - 1);
                                normalList.add(normal.x);
                                normalList.add(normal.y);
                                normalList.add(normal.z);
                            }
                            indexList.add(vertexMap.get(key));
                        }
                        break;
                }
            }
        }

        float[] vertexArray = listToFloatArray(vertexList);
        float[] texCoordArray = listToFloatArray(texCoordList);
        float[] normalArray = listToFloatArray(normalList);
        int[] indexArray = indexList.stream().mapToInt(i -> i).toArray();

        Texture texture = new Texture(texturePath);
        Mesh mesh = new Mesh(vertexArray, texCoordArray, normalArray, indexArray, texture);

        Model model = new Model();
        model.addMesh(mesh);
        return model;
    }

    private static float[] listToFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}