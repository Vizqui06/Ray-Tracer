import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjReader {

    // The main objective of this class is that, receiving the obj file path and color,
    // returns a list of TriangleIntersection that is ready for initialization in the scene
    public static List<TriangleIntersection> load(String filePath, Color color) {
        List<Vector3D> vertices = new ArrayList<>(); // List of vertices of the obj
        List<int[]> faces = new ArrayList<>(); // One face = one array of vertex indices (ideally, 3 vertices)
        List<TriangleIntersection> triangles = new ArrayList<>(); // The list that will be given to main to initialize

        try (BufferedReader bufferedReader = new BufferedReader( // This try will close the reader after use 
            new InputStreamReader(new FileInputStream(filePath)))) { // 

            String textLine; // Literally, the text line of the obj file that is being readed
            while ((textLine = bufferedReader.readLine()) != null) { // Read line by line until the end of the file
                textLine = textLine.trim(); // Remove leading and trailing whitespace for easier parsing

                // Ignore empty lines and comments, comments start with "#"
                if (textLine.isEmpty() || textLine.startsWith("#")) {continue;}

                String[] words = textLine.split("\\s+"); // Divides the line into words based on whitespace, 
                // for example: "v 1.0 2.0 3.0" turns to -> ["v", "1.0", "2.0", "3.0"]
                // The argument "\\s+" is an expression that matches one or more spaces characters, so it can handle multiple spaces between words

                // If the first word is "v", so the next three words are the coordinates of the vertex 
                // But because it is in string and Vector3D only handles doubles -> convert them to double and create a Vector3D
                if (words[0].equals("v")) {
                    // Convert the string to a double: "1.0" turns to 1.0
                    double x = Double.parseDouble(words[1]); 
                    double y = Double.parseDouble(words[2]);
                    double z = Double.parseDouble(words[3]);
                    // Add the vertex to the list of vertices
                    vertices.add(new Vector3D(x, y, z));
                }

                // Read the faces: "f v0/vt0/vn0 v1/vt1/vn1 v2/vt2/vn2 ..."
                // But also has to handle without a parameter (normal or textures) "f v0//vn0 v1//vn1" y "f v0 v1 v2"
                else if (words[0].equals("f")) {
                    int numVertices = words.length - 1; // The number of vertices in the face is the total number of words minus 1 (the first word is "f")
                    int[] faceIndices = new int[numVertices]; // Array to store the vertex indices of the face (hope result in triangle)

                    for (int i = 1; i < words.length; i++) { // Start from 1 to skip the "f" word and go through each vertex definition in the face
                        // Divides by "/" to separate v/vt/vn (vertex, texture, normal)
                        // For this version, only the first value (vertex index) matters.

                        // The split("/", -1) is where the real magic is: the -1 makes it preserve empty strings between separators.
                        // In this way, "1//2" becomes ["1", "", "2"] instead of ["1", "2"], making it easier to classify in the arrays
                        String[] parts = words[i].split("/", -1);

                        // OBJ uses indices from 1, so it must be converted into a 0-start-based index
                        faceIndices[i - 1] = Integer.parseInt(parts[0]) - 1;
                    }
                    faces.add(faceIndices); // Add the face (array of vertex indices) to the list of faces
                }
                // vn (normals) and vt (textures) are omitted for now
            }

        } catch (IOException e) { // In case of ANY error:
            System.err.println("\nThere was an error reading the OBJ file: " + e.getMessage());
            return triangles; // Return empty list if failed
        }


        // To avoid the object be somewhere in the scene, I want it to be at the center of the scene
        // This can be made by normalizing the object (with the average of all vertices)
        // With the new class (Model3D), the object can be placed elsewhere and change its size

        double centeredX = 0, centeredY = 0, centeredZ = 0; 
        for (Vector3D vertex : vertices) { 
            // Sum all the coordinates of the vertices
            centeredX += vertex.getX();
            centeredY += vertex.getY();
            centeredZ += vertex.getZ();
        }
        // Divide by the number of vertices to get the average (normalization)
        centeredX /= vertices.size();
        centeredY /= vertices.size();
        centeredZ /= vertices.size();

        // For the object to be physically in the center, all vertices must be moved to center them at the origin

        // List to store the new centered vertices, which will be used for triangulation instead of the original vertices
        List<Vector3D> centeredVertices = new ArrayList<>(); 
        for (Vector3D vertex : vertices) { // For each vertex in the original list of vertices
            centeredVertices.add(new Vector3D( 
                // Create a new vertex that is the original vertex minus its normalization, so it is centered at the origin
                vertex.getX() - centeredX,
                vertex.getY() - centeredY,
                vertex.getZ() - centeredZ
            ));
        }


        // Convert faces into triangles
        // If a face has more than 3 vertices, it is triangulated with v0 as the pivot:
        // [0,1,2,3] -> 1 quad -> 2 triangles -> 1 by vertices: (0,1,2) + second by vertices: (0,2,3)
        // Any face with more than 3 vertices will be triangulated in this way, so it can handle quads and polygons with more than 4 vertices
        for (int[] face : faces) { // Checks each face (array of vertex indices) in the list of faces
            for (int i = 1; i < face.length - 1; i++) { // Start from 1 and go until the second to last vertex index, because the last one will be used in the last triangle
                Vector3D v0 = centeredVertices.get(face[0]); // The first vertex of the face is the pivot for triangulation, so it is always the first vertex index in the face array
                Vector3D v1 = centeredVertices.get(face[i]); // The second vertex of the triangle is the current vertex index in the loop, starting from the second vertex index in the face array
                Vector3D v2 = centeredVertices.get(face[i + 1]); // The third vertex of the triangle is the next vertex index in the loop, so it is the current vertex index + 1 in the face array
                triangles.add(new TriangleIntersection(v0, v2, v1, color)); // Create a new TriangleIntersection with the three vertices and the color, and add it to the list of triangles
            }   // NOTE: It is v0, v2, v1 to be accurate with the counter-clockwise order that TriangleIntersection manages for the normal calculation
        }

        System.out.println("\nOBJ Ready to Render.\nWith: \n" + vertices.size() + " vertices.\n" + 
        faces.size() + " faces.\n" + triangles.size() + " generated triangles.");
        return triangles; // Return the list of TriangleIntersection that is ready for initialization in the scene
    }
} 