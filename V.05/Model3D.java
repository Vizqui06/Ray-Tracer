import java.util.ArrayList;
import java.util.List;

public class Model3D {
    private final List<TriangleIntersection> triangles;
    private final Vector3D position; // center of the object in the scene
    private final double scale; // scale of the object (1.0 = default size by modeler)
    private final double rotationY; // rotates the object in the vertical axis, the Y axis

    // position: where you want the object in the scene
    // scale: 1.0 = original size, 0.5 = half, 2.0 = double
    // rotationY: in degrees -> 0 = look towards +Z, 180 = look towards -Z (towards the camera)
    public Model3D(Vector3D position, double scale, double rotationY, List<TriangleIntersection> rawTriangles) {
        this.position = position;
        this.scale = scale;
        this.rotationY = rotationY;
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
            // Transforming each vertex of the triangle according to the model's position, scale, and rotation
            Vector3D v0 = transformVertex(triangle.getV0());
            Vector3D v1 = transformVertex(triangle.getV1());
            Vector3D v2 = transformVertex(triangle.getV2());

            // If the triangle has per-vertex normals (for smooth shading), they will also be transformed
            // The != null check is to avoid trying to transform a normal that doesn't exist (in the case of flat shading)
            Vector3D n0 = triangle.getN0() != null ? transformNormal(triangle.getN0()) : null;
            Vector3D n1 = triangle.getN1() != null ? transformNormal(triangle.getN1()) : null;
            Vector3D n2 = triangle.getN2() != null ? transformNormal(triangle.getN2()) : null;

            if (n0 != null) { // If there are per-vertex normals, create the triangle with them for smooth shading
                transformed.add(new TriangleIntersection(v0, v2, v1, triangle.getColor(), n0, n2, n1));
            } else { // If there are no per-vertex normals, create the triangle without them for flat shading
                transformed.add(new TriangleIntersection(v0, v2, v1, triangle.getColor()));
            }
        }
        return transformed;
    }

    private Vector3D transformNormal(Vector3D normal) {
        // It just rotates, never moves from position nor scalarized
        return normal.rotateY(rotationY).normalization();
    }
    
    // Method to actually set, scale and rotate the object in the scene
    private Vector3D transformVertex(Vector3D vertex) {
        // Scale the object
        Vector3D scaled = new Vector3D(
            scale * vertex.getX(),
            scale * vertex.getY(),
            scale * vertex.getZ()
        );
        // Rotate around Y axis
        Vector3D rotated = scaled.rotateY(rotationY);
        // Translate to position in the scene
        return new Vector3D(
            rotated.getX() + position.getX(),
            rotated.getY() + position.getY(),
            rotated.getZ() + position.getZ()
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