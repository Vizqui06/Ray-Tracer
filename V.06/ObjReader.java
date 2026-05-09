import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap; // Importing HashMap to store key-value pairs for smoothed normal calculations
import java.util.List;
import java.util.Map; // Importing the Map interface to manage the smoothed normals mapping


public class ObjReader {

    // These comments explain how the program decides which normal to use for each triangle:
    // Case 1: When the face already provides vertex normal (vn) indices.
    // Case 2: When there are no vn indices: Automatic smooth on everything

    private static final long SMOOTH_KEY_MULT = 100000L; // Constant HashMap key
    
    public static List<TriangleIntersection> load(String filePath, Color color) {
        // Initializing a list to store the 3D coordinates of each vertex found in the file
        List<Vector3D> vertices = new ArrayList<>(); 
        // Initializing a list to store the explicit vertex normals defined by 'vn' lines
        List<Vector3D> normals = new ArrayList<>(); 
        // Initializing a list of integer arrays to store vertex indices for each face
        List<int[]> faces = new ArrayList<>(); 
        // Initializing a list of integer arrays to store normal indices for each face (if they exist)
        List<int[]> faceNormals = new ArrayList<>(); 
        // Initializing a list to keep track of which smoothing group each face belongs to
        List<Integer> faceSmoothGroup = new ArrayList<>(); 

        // Initializing the final list that will hold the triangle objects for the renderer
        List<TriangleIntersection> triangles = new ArrayList<>();

        // Variable to keep track of the current active smoothing group while reading the file
        int currentSmoothGroup = 0; 

        // Opening the file inside a try-with-resources block to ensure it closes automatically
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath)))) {

            // Variable to hold the content of the current line being read
            String textLine;
            // Looping through every line of the file until the end is reached
            while ((textLine = bufferedReader.readLine()) != null) {
                // Removing leading and trailing whitespace from the line
                textLine = textLine.trim();
                // Skipping the line if it is empty or starts with a '#' which indicates a comment
                if (textLine.isEmpty() || textLine.startsWith("#")) { continue; }

                // Splitting the line into an array of strings based on whitespace characters
                String[] words = textLine.split("\\s+");

                // Checking if the line defines a vertex position (starts with 'v')
                switch (words[0]) {
                    case "v" -> // Adding a new Vector3D to the vertices list using the next three values in the line
                        vertices.add(new Vector3D(
                                Double.parseDouble(words[1]), // X coordinate
                                Double.parseDouble(words[2]), // Y coordinate
                                Double.parseDouble(words[3])  // Z coordinate
                        ));
                    case "vn" -> // Adding a normalized Vector3D to the normals list from the provided coordinates
                        normals.add(new Vector3D(
                                Double.parseDouble(words[1]), // X direction
                                Double.parseDouble(words[2]), // Y direction
                                Double.parseDouble(words[3]) // Z direction
                        ).normalization()); // Ensuring the normal has a length of 1
                    case "s" -> {
                        // If the value is 'off' or '0', it means smoothing is disabled
                        if (words[1].equals("off") || words[1].equals("0")) {
                            currentSmoothGroup = 0;
                        } else {
                            // Otherwise, parse the group number and set it as the current active group
                            currentSmoothGroup = Integer.parseInt(words[1]);
                        }
                    }
                    case "f" -> {
                        // Determining how many vertices are in this face (usually 3 or 4)
                        int numVerts = words.length - 1;
                        // Creating an array to store vertex indices for this specific face
                        int[] faceIdx = new int[numVerts];
                        // Creating an array to store normal indices for this specific face
                        int[] normalIdx = new int[numVerts];
                        // Flag to track if this face actually contains vertex normal references
                        boolean hasVN = false;
                        // Iterating through each vertex definition in the face line
                        for (int i = 1; i < words.length; i++) {
                            // Splitting the part (v/vt/vn) by the forward slash character
                            String[] parts = words[i].split("/", -1);
                            // Parsing the vertex index and converting it to 0-based indexing
                            faceIdx[i - 1] = Integer.parseInt(parts[0]) - 1;
                            
                            // Checking if the third part (the normal index) exists and is not empty
                            if (parts.length >= 3 && !parts[2].isEmpty()) {
                                // Parsing the normal index and converting it to 0-based indexing
                                normalIdx[i - 1] = Integer.parseInt(parts[2]) - 1;
                                // Setting the flag to true because this face uses explicit normals
                                hasVN = true;
                            } else {
                                // Using -1 to indicate that no normal index was provided for this vertex
                                normalIdx[i - 1] = -1;
                            }
                        }   // Adding the gathered vertex indices to the faces list
                        faces.add(faceIdx);
                        // Adding normal indices if present, or null if this face lacks normal data
                        faceNormals.add(hasVN ? normalIdx : null);
                        // Storing the smoothing group that was active when this face was defined
                        faceSmoothGroup.add(currentSmoothGroup);
                    }
                    default -> {
                    }
                }
            } // End of the while loop reading lines

        } catch (IOException e) {
            // Printing an error message if the file could not be read properly
            System.err.println("\nThere was an error reading the OBJ file: " + e.getMessage());
            // Returning the empty triangles list to avoid further errors
            return triangles;
        }

        // Starting the process to center the model at the world origin (0,0,0)
        double cx = 0, cy = 0, cz = 0;
        // Summing up all vertex coordinates to find the geometric center
        for (Vector3D v : vertices) { cx += v.getX(); cy += v.getY(); cz += v.getZ(); }
        // Dividing the sums by the total number of vertices to get the average position
        cx /= vertices.size(); cy /= vertices.size(); cz /= vertices.size();

        // Creating a new list to store the centered vertex positions
        List<Vector3D> centeredVertices = new ArrayList<>();
        // Subtracting the center point from every vertex to shift the model to the origin
        for (Vector3D v : vertices) {
            centeredVertices.add(new Vector3D(v.getX() - cx, v.getY() - cy, v.getZ() - cz));
        }

        // Mapping to store smoothed normals based on a unique key of vertex index and smoothing group
        Map<Long, Vector3D> smoothedNormals = new HashMap<>();

        // First pass: Calculate the flat normals of faces and accumulate them for vertices in smoothing groups
        for (int fi = 0; fi < faces.size(); fi++) {
            // Skipping if the face already has explicit normals (Case 1)
            if (faceNormals.get(fi) != null) { continue; } 

            // Getting vertex indices and the smoothing group ID for the current face
            int[] faceIdx = faces.get(fi); // vertex indices for this face
            int sg = faceSmoothGroup.get(fi); // smoothing group for this face

            // Fetching the coordinates of the first three vertices to calculate the surface normal
            Vector3D fv0 = centeredVertices.get(faceIdx[0]); // the pivot vertex (upper vertex in the triangle fan)
            Vector3D fv1 = centeredVertices.get(faceIdx[1]); // the second vertex (lower left vertex in the triangle fan)
            Vector3D fv2 = centeredVertices.get(faceIdx[2]); // the third vertex (lower right vertex in the triangle fan)
            // Calculating the flat normal using the cross product of two edges of the face
            Vector3D flatN = fv1.vectorSubstraction(fv0).productCross(fv2.vectorSubstraction(fv0)).normalization();

            // Use sg=1 as a universal group if the OBJ does not have 's' groups
            int effectiveSG = (sg == 0) ? 1 : sg;

            // Adding this face normal to every vertex participating in this face and smoothing group
            for (int vi : faceIdx) {
                // Creating a unique key by combining the vertex index and the smoothing group number
                long key = (long) vi * SMOOTH_KEY_MULT + effectiveSG;
                // If the key is new, put the normal in the map; otherwise, add the current normal to the existing one
                if (!smoothedNormals.containsKey(key)) {
                    smoothedNormals.put(key, flatN); // first normal for this vertex and smoothing group
                } else { // If not
                    // Adding the current face normal to the accumulated normal for this vertex and smoothing group
                    smoothedNormals.put(key, smoothedNormals.get(key).vectorAddition(flatN));
                }
            }
        }
        
        // Second pass: Finalize the averaged normals by normalizing the accumulated sums
        // The for loop intends to iterate through each entry in the smoothedNormals map, 
        // where each entry consists of a unique key (combination of vertex index and smoothing group) 
        // and its corresponding accumulated normal vector.
        for (Map.Entry<Long, Vector3D> entry : smoothedNormals.entrySet()) {
            // Replacing the accumulated vector with its unit-length version
            smoothedNormals.put(entry.getKey(), entry.getValue().normalization());
        }

        // Final loop to build the actual TriangleIntersection objects for rendering
        for (int fi = 0; fi < faces.size(); fi++) {
            // Extracting indices for vertices and normals, and the smoothing group for the current face
            int[] faceIdx = faces.get(fi); // vertex indices for this face
            int[] normIdx = faceNormals.get(fi); // normal indices for this face (if they exist)
            int sg = faceSmoothGroup.get(fi); // smoothing group for this face

            // Converting polygons (n-gons) into triangles using the triangle fan method (pivot at index 0)
            for (int i = 1; i < faceIdx.length - 1; i++) {
                // Selecting indices for the current triangle in the fan
                int ai = faceIdx[0], bi = faceIdx[i], ci = faceIdx[i + 1];
                // Retrieving the centered vertex positions for the triangle
                Vector3D tv0 = centeredVertices.get(ai); // the pivot vertex (upper vertex in the triangle fan)
                Vector3D tv1 = centeredVertices.get(bi); // the second vertex (lower left vertex in the triangle fan)
                Vector3D tv2 = centeredVertices.get(ci); // the third vertex (lower right vertex in the triangle fan)

                // Case 1: When the face defines its own specific vertex normals
                if (normIdx != null) {
                    // Fetching the pre-defined normals from the list
                    Vector3D tn0 = normals.get(normIdx[0]); // normal for the pivot vertex
                    Vector3D tn1 = normals.get(normIdx[i]); // normal for the second vertex
                    Vector3D tn2 = normals.get(normIdx[i + 1]); // normal for the third vertex

                    // Creating the triangle with smooth shading using explicit normals
                    triangles.add(new TriangleIntersection(tv0, tv2, tv1, color, tn0, tn2, tn1));

                } 
                // Case 2: Automatic smooth (with or without S groups)
                else {
                    int effectiveSG = (sg == 0) ? 1 : sg;
                    // Fetching the averaged normals we computed earlier for each vertex
                    Vector3D tn0 = smoothedNormals.get((long) ai * SMOOTH_KEY_MULT + effectiveSG); // normal for the pivot vertex
                    Vector3D tn1 = smoothedNormals.get((long) bi * SMOOTH_KEY_MULT + effectiveSG); // normal for the second vertex
                    Vector3D tn2 = smoothedNormals.get((long) ci * SMOOTH_KEY_MULT + effectiveSG); // normal for the third vertex

                    // Creating the triangle with smooth shading using averaged normals
                    triangles.add(new TriangleIntersection(tv0, tv2, tv1, color, tn0, tn2, tn1));
                }
            }
        }

        // Printing a summary of the loaded model data to the console for confirmation
        System.out.println("\nOBJ Ready to Render.\nWith: \n" 
        + vertices.size() + " vertices.\n" // Total number of original vertices found
        + faces.size() + " faces.\n" // Total number of faces defined in the file
        + triangles.size() + " generated triangles."); // Total number of triangles after triangulation
        
        // Returning the final list of triangles to the caller
        return triangles;
    }
}