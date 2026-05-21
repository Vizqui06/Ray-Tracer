// Scene is the container of the world. 
// Its job is to save all the objects and answer a single question: "what object does this ray hitsfirst?

import java.awt.Color; // for knowing what colors does each one of the objects has
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final ArrayList<Object3D> objects; // Objects list of non-triangle objects (after Bounding Boxes)
    private final ArrayList<Light> lights; // Lights list of the scene
    private final Camera camera; // Takes camera as an object to confirm if its method "isInFrustum" is true or false in each "hit" evaluation

    // Max reflection bounces: 2 minimum (have to take care of my laptop)
    public static final int MAX_BOUNCES = 3;

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

    // Bounding boxes method t oapply voxes and boxes tree node in the scene
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

    // Shadow method, it will tell if a pixel is in a shadow, depending on position of light, camera, object and frustum
    // Returns true if the hit point of the ray is in shadow from the given light direction
    // With multiple cameras, the process will be iterative; last light will decide what remains shadow and what is iluminated
    // The variables this method needs are:
    // shadowRayOrigin: the point on the surface that was hit (it is not decided yet if its a shadow or no-shadow)
    // directionToLight: normalized vector from hit point toward the light (if it can freely reach the light withot obstructions)
    // distanceToLight: how far the light actually is (for point lights and set the intensity if is not shadow)

    // pass Double.MAX_VALUE for directional lights (infinitely far)
    public boolean IsInShadow(Vector3D shadowRayOrigin, Vector3D directionToLight, double distanceToLight){
        // A small offset to push the ray origin slightly away from the surface
        // Without this, the shadow ray immediately hits the same triangle it originated from
        // This should protect the rule of "do not collide with yourself" warning from the slide
        double shadowAntiCollide = 1e-4;
        // The partial origin is the hit point plus a vector that pushes it slightly away from the surface 
        // in the direction of the light to avoid self-intersection issues when casting the shadow ray
        Vector3D partialOrigin = shadowRayOrigin.vectorAddition(directionToLight.scalarIt(shadowAntiCollide));

        // Create the shadow ray from the partial origin towards the light
        Ray shadowRay = new Ray(partialOrigin, directionToLight);

        // Chack for non-triangle objects:
        for(Object3D object : objects) {
            Intersection hit = object.intersect(shadowRay);
            // The "hit" must exist, be in front of the ray, and be closer than the light itself to have a visible shadow to me
            if(hit.isCollition_happened() && hit.getT() > 0 && hit.getT() < distanceToLight){return true;}
        }

        // Check triangles through the Bounding Volume Hierarchy (BVH) tree
        if (boundingVolumeHierarchyRoot != null){
            // Ask the tree for any intersection with the shadow ray, within the frustum defined by the camera's near and far planes, 
            // and closer than the light itself
            Intersection hit = boundingVolumeHierarchyRoot.intersect(shadowRay, shadowAntiCollide, distanceToLight);
            // If there is any hit with the shadow ray, it means that the point is in shadow from this light, so returns true
            if (hit.isCollition_happened()){return true;} 
        }
        // If there is nothing blocking the ray, then the point is not in shadow from this light
        return false;
    }

    // Intersection of rays with objects inside the scene
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


    // RefLEction functions:
    // Method to simply calculate if there was a hit and hasn't reached bounces limit, return a reflection bounce
    public Color bounceRay(Ray ray, int bounces, Color backgroundColor){
        Intersection hit = intersect(ray); // Use to prove if there is an intersection
        // If there were no intersection, returns the background color (void)
        if(!hit.isCollition_happened()) {return backgroundColor;}
        // If there were an intersection (and bounces still less than limit), do a reflection
        return shade(hit, ray, bounces, backgroundColor);
    }

    private Color shade (Intersection hit, Ray ray, int bounce, Color backgroundColor){
        Object3D objectHit = hit.getObjectHit(); // The object that is hit by the ray
        Vector3D normal = hit.getNormal(); // The normal of the hitten object

        // Here is needed to know WHICH object was hit to take its color
        // Gets the color from the object hitted, now implementing textures, reflection and refraction

        Color surfaceColor = objectHit.rightColor(hit.getHitU(), hit.getHitV()); // First take its UVs (if exists)
        double red = 0, green = 0, blue = 0; // At first, no object hitten --> No color to give --> black

        for (Light light : lights){  // For every light in the scene
            // Calculate the direction and distance to the light from the hit point
            Vector3D directionToLight = light.getDirectionToLight(hit.getHitPoint());
            // The distance to the light is needed for point lights to determine if the light is blocked 
            // by an object (shadow) and to calculate the intensity of the light based on distance
            double distanceToLight = light.getDistanceToLight(hit.getHitPoint());

            // Check if the hit point is in shadow from this light, if it is, skip the contribution of this light to the final color
            if(IsInShadow(hit.getHitPoint(), directionToLight, distanceToLight)){continue;}

            // If the point is not in shadow, calculate the light contribution to the color at this point using the light's method
            // The "makeColor" method of the light will calculate the color contribution based on its arguments
            Color contribution = light.makeColor(normal, hit.getHitPoint(), surfaceColor, camera);
            // The red, green and blue contributions are added to the final color, normalized to the range [0, 1] by dividing by 255
            red += contribution.getRed() / 255.0; 
            green += contribution.getGreen() / 255.0;
            blue += contribution.getBlue() / 255.0;
        }
        // Clamp the color values to the range [0, 1] to avoid overflow when converting back to RGB
        red = Math.min(1.0, red); 
        green = Math.min(1.0, green);
        blue = Math.min(1.0, blue);

        // Reflection: spawn a new ray in the mirrored direction if bounce limit hasn't reached its max (variable)
        double refLEctivity = objectHit.getRefLEctivity(); // Determines the reflection of the object (0.0 = null, 1.0 = perfect mirror)
        // If an object HAS reflectivity and the bounces limit has not been reached
        if (refLEctivity > 0.0 && bounce < MAX_BOUNCES) {
            // Calculate the reflected ray direction using the formula: R = D - 2(D·N)N
            Vector3D incRayDirection = ray.getDirection(); // Incoming ray direction (D)
            // The reflectedDir vector is the Normal vector at the hit point (N) is already calculated and stored in the "normal" variable
            Vector3D reflectedDir = incRayDirection.vectorSubstraction(normal.scalarIt(2.0 * incRayDirection.productPoint(normal)));
            // The reflected ray origin is slightly offset from the hit point in the direction of the normal to avoid self-intersection issues
            Vector3D reflectedOrigin = hit.getHitPoint().vectorAddition(normal.scalarIt(1e-4));
            // Spawn a new ray in the reflected direction and calculate its color contribution by calling bounceRay recursively, increasing the bounce count
            Color reflectedColor = bounceRay(new Ray(reflectedOrigin, reflectedDir), bounce + 1, backgroundColor);

            // Blend: surface color scaled down by reflectivity, reflection scaled up
            // RGB values are blended: original color is scaled down by (1 - reflectivity) 
            // And the reflected color is scaled up by reflectivity, then added together
            red = red * (1.0 - refLEctivity) + (reflectedColor.getRed() / 255.0) * refLEctivity;
            green = green * (1.0 - refLEctivity) + (reflectedColor.getGreen() / 255.0) * refLEctivity;
            blue = blue * (1.0 - refLEctivity) + (reflectedColor.getBlue() / 255.0) * refLEctivity;
        }

        double refRActivity = objectHit.getRefRActivity(); // Determines the refraction of the object
        // If an object HAS refractivity and the bounces limit has not been reached
        if(refRActivity > 0.0 && bounce < MAX_BOUNCES){
            // Calculate the reflected ray direction using the formula: R = D - 2(D·N)N
            Vector3D incRayDirection = ray.getDirection(); // Incoming ray direction (D)
            // Product point between the incoming ray direction and the surface normal of the object
            double incRayDirectionDotNormal = incRayDirection.productPoint(normal);
            // Determine if ray is entering or exiting the material
            double n1, n2; // n1 and n2 are the refractive indices of the media the ray is coming from and going into, respectively
            Vector3D refractNormal; // The normal vector used for refraction calculations, which may be flipped if the ray is exiting the object
            if(incRayDirectionDotNormal < 0){ // If the ray is ENTERING the object: air --> obj
                n1 = 1.0; // Assuming the ray is coming from air, which has a refractive index of 1.0
                n2 = objectHit.getRefractiveIndex(); // The refractive index of the object being hit, so it can be different for each object
                refractNormal = normal; // The normal is used as is for refraction calculations when the ray is entering the object
            } else{ // If the ray is not entering, is EXITING the object: obj --> air
                n1 = objectHit.getRefractiveIndex(); // The ray is exiting the object, so n1 is the refractive index of the object
                n2 = 1.0; // The ray is exiting into air, which has a refractive index of 1.0
                // Flip the normal, the normal should point against the incoming ray direction when the ray is exiting the object
                refractNormal = normal.scalarIt(-1.0); 
                // The dot product is flipped due to the normal being flipped, this because the angle of incidence is measured 
                // between the incoming ray direction and the normal, and when the normal is flipped, the angle of incidence is 
                // now reversed, so the dot product changes sign to stay consistent with the new orientation of the normal vector
                incRayDirectionDotNormal = -incRayDirectionDotNormal;
            }

            double ratio = n1 / n2; // Ratio of the refractive indices, by Snell's law to determine the bending of the ray
            double cosTheta1 = -incRayDirectionDotNormal; // Angle of incidence
            double sinThetaSquare = ratio * ratio * (1.0 - cosTheta1 * cosTheta1); // Square of the sine of the angle of refraction

            if(sinThetaSquare <= 1.0){
                // There is NO total internal reflection --> calculate the refracted direction
                double cosTheta2 = Math.sqrt(1.0 - sinThetaSquare);
                // From the formula, T = ray direction = ratio*D + (ratio*cosTheta1 - cosTheta2)*N
                Vector3D refractedDirection = incRayDirection.scalarIt(ratio).vectorAddition(refractNormal.scalarIt(ratio * cosTheta1 - cosTheta2));
                // Push origin inward (opposite to normal) to enter the surface
                // The refracted origin is slightly offset from the hit point in the opposite direction 
                // of the normal to avoid self-intersection issues when spawning the refracted ray inside the object
                Vector3D refractedOrigin = hit.getHitPoint().vectorSubstraction(refractNormal.scalarIt(1e-4));
                // The refracted color is calculated by spawning a new ray in the refracted 
                // direction and calling bounceRay recursively, increasing the bounce count
                Color refractedColor = bounceRay(new Ray(refractedOrigin, refractedDirection), bounce + 1, backgroundColor);

                // Blend: surface color scaled down, refraction scaled up by refractivity
                // RGB values are blended: original color is scaled down by (1 - reflectivity) 
                // And the reflected color is scaled up by reflectivity, then added together
                red = red * (1.0 - refRActivity) + (refractedColor.getRed() / 255.0) * refRActivity;
                green = green * (1.0 - refRActivity) + (refractedColor.getGreen() / 255.0) * refRActivity;
                blue = blue * (1.0 - refRActivity) + (refractedColor.getBlue() / 255.0) * refRActivity;
            }
            // If total internal reflection occurs, the ray stays as-is (no refraction contribution)
        }

        // Returns the final color, converting the normalized RGB values back to the range [0, 255] for the Color constructor
        return new Color((int)(red * 255), (int)(green * 255), (int)(blue * 255));
    }
}