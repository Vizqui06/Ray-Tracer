import java.util.ArrayList;
import java.util.List;

public class Model3D {
    private final List<TriangleIntersection> triangles;
    private final Vector3D position; // center of the object in the scene
    private final double scale; // scale of the object (1.0 = default size by modeler)

    // position: where you want the object in the scene
    // scale: 1.0 = original size, 0.5 = half, 2.0 = double
    // rotationY: in degrees -> 0 = look towards +Z, 180 = look towards -Z (towards the camera)
    public Model3D(List<TriangleIntersection> rawTriangles, Vector3D position, double scale) {
        this.position = position;
        this.scale = scale;
        // to apply all transformation changes in the default/defaultModel model ObjReader provided
        this.triangles = applyTransformations(rawTriangles);
    }

    // Apply scale -> rotation Y -> translation to each vertex of each triangle
    // ObjReader already provided triangles based on the vertices and faces by the obj file
    // And because the faces already passed through the triangle verifier filter
    // It is secure to get each triangle's vertex and rearrange and transform them

    private List<TriangleIntersection> applyTransformations(List<TriangleIntersection> defaultModel) {
        // List of triangles that will contain the transformed vertices
        List<TriangleIntersection> transformed = new ArrayList<>();
        // for every default triangle, get its color and their 3 vertices
        for (TriangleIntersection triangle : defaultModel) {
            Vector3D v0 = transformVertex(triangle.getV0());
            Vector3D v1 = transformVertex(triangle.getV1());
            Vector3D v2 = transformVertex(triangle.getV2());
            // The color of each triangle is preserved from the original
            // Puts all tirangles (with the tag of its color) in the array
            transformed.add(new TriangleIntersection(v0, v2, v1, triangle.getColor()));
        }
        return transformed;
    }

    // Method to actually set, scale and rotate the object in the scene
    private Vector3D transformVertex(Vector3D vertex) {
        // According to the scale, the triangles must increment/decrement its size
        // Put the vertex to the desired center of the scene
        return new Vector3D(
            scale * vertex.getX() + position.getX(),
            scale * vertex.getY() + position.getY(),
            scale * vertex.getZ() + position.getZ()
        );
    }

    // Adds all the transformed and rearranged triangles to the scene 
    public void addToScene(Scene scene) {
        for (TriangleIntersection triangle : triangles) {
            scene.addObject(triangle);
        }
    }
    // Get the list of triangles that compose the model, in case it is needed for any reason
    public List<TriangleIntersection> getTriangles() {return triangles;}
}