// Scene is the container of the world. 
// Its job is to save all the objects and answer a single question: "what object does this ray hitsfirst?

import java.util.ArrayList; // for knowing what colors does each one of the objects has
import java.util.List;

public class Scene {
    private final ArrayList<Object3D> objects; // Objects list of non-triangle objects (after Bounding Boxes)
    private final ArrayList<Light> lights; // Lights list of the scene
    private final Camera camera; // Takes camera as an object to confirm if its method "isInFrustum" is true or false in each "hit" evaluation

    // The Bounding Volume Hierarchy tree, which contains all the triangles within the scene
    private BoxesTreeNode boundingVolumeHierarchyRoot;

    public Scene(Camera camera){ // the constructor
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.camera = camera;
        // It is declared null until buildTheBoundingVolumeHierarchyTree() is called, or if there are no triangles at all
        this.boundingVolumeHierarchyRoot = null;
    }

    // Getter of the list of 3D objects
    public ArrayList<Object3D> getObjects() {return objects;} 
    // Getter of the list of Lights
    public ArrayList<Light> getLights() {return lights;}

    // void function to add another object to the scene
    public void addObject(Object3D object){objects.add(object);}
    // void function to add another light to the scene
    public void addLight(Light light){lights.add(light);}

    // Method to find the nearest intersection between the ray and every object

    // NOTE: After implementing the logic of the bounding boxes in Boxes.java and BoxesTreeNode.java, it is only left to apply it to the scene

    // The latest version of my Ray-Tracer was calculating the intersections by this logic:
    //     For each object (spheres + many triangles) --> try the intersection --> resulting in a O(n) search algorithm (linear -> bad)

    // With the Bounding Boxes, it drastically changes:
    //     For every non-triangle object --> try intersection --> In tests, few objects meets this requirement
    //     For every triangle object --> ask the tree node --> resulting in a O(log2 n) search algorithm (optimized -> gud)

    // So, the objects list is for the spheres. Triangles are taken, putting them in the tree for consulting as a normal mere object


    public void buildTheBoundingVolumeHierarchyTree() {
        // Separate triangles from the rest of the objects
        List<TriangleIntersection> triangles = new ArrayList<>(); // List FOR TRIANGLES
        List<Object3D> nonTriangles = new ArrayList<>(); // List for EVERYTHING ELSE THAT IS NOT A TRIANGLE

        // For every 3D object in the objects list:
        for (Object3D obj : objects) {
            // If it is a triangle, add the triangle intersection to the triangles list
            // The "instanceof" operator checks if the object is an instance of the TriangleIntersection class, 
            // and if it is, it casts it to that type and adds it to the triangles list.
            if (obj instanceof TriangleIntersection triangleIntersection) {
                triangles.add(triangleIntersection);
            } else { // Otherwise, add the object to the non-triangles list
                nonTriangles.add(obj);
            }
        }

        // If there are no triangles therefore there is no tree to build
        if (triangles.isEmpty()) {
            System.out.println("\nBounding Volume Hierarchy says: There are NO triangles within the scene.\n");
            return; // There is nothing to return if there is no tree to build
        }

        // Leave only what is NOT a triangle in the normal objects array
        // And the triangles now are exclusively inside the tree
        objects.clear();
        objects.addAll(nonTriangles);

        // Build the tree --> all the BoxesTreeNode recursion happens here
        System.out.println("\nBuilding a Bounding Volume Hierarchy with " + triangles.size() + " triangles...");
        // Just to see how much does it takes to do the calculations
        long start = System.currentTimeMillis();

        // Does all the BoxesTreeNode intersection search logic in this line of code
        boundingVolumeHierarchyRoot = new BoxesTreeNode(triangles);

        long elapsed = System.currentTimeMillis() - start; // Time that took the Bounding Boxes process
        System.out.println("\nBounding Volume Hierarchy built in " + elapsed + " ms.");
    }




    public Intersection intersect(Ray ray){
        // the closest intersection, that is the one that will be returned at the end of the function, 
        // is null at the beginning because there is no intersection yet
        Intersection closest = null; 

        for (Object3D object : objects) { // for every non-triangle object in the scene, checks if there is an intersection with the ray
            Intersection hit = object.intersect(ray);
            // if there was a collition and it is within the frustum, check if it is the closest one
            if(hit.isCollition_happened() && camera.isInFrustum(hit.getT())){ 
                // first find collition or closest than previous one
                // If there was no closest, or if the new hit is closer than the previous closest, update the closest intersection
                if(closest == null || hit.getT() < closest.getT()){closest = hit;} 
            }
        }

        if (boundingVolumeHierarchyRoot != null) { // If there is at least one triangle inside the scene:
            // Ask the tree for the closest intersection with the ray, within the frustum defined by the camera's near and far planes
            Intersection hit = boundingVolumeHierarchyRoot.intersect(ray, camera.getNearPlane(), camera.getFarPlane());
            if (hit.isCollition_happened()) { // If there is a collition intersecting a triangle:
                // If there was no closer hit (or is the first one), last one is the closest, even if it is a triangle
                if (closest == null || hit.getT() < closest.getT()) {
                    closest = hit; // The closest hit is the one that will be rendered
                }
            }
        }
        

        if(closest == null){return new Intersection();} // if there was no closest, there was no collition
        return closest; // if there was, return it and git good
    }
}
