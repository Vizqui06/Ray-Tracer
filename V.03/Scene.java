// Scene is the container of the world. 
// Its job is to save all the objects and answer a single question: "what object does this ray hitsfirst?

import java.util.ArrayList; // for knowing what colors does each one of the objects has

public class Scene {
    private final ArrayList<Object3D> objects; // Objects list of Object3D
    private final Camera camera; // Takes camera as an object to confirm if its method "isInFrustum" is true or false in each "hit" evaluation

    public Scene(Camera camera){ // the constructor
        this.objects = new ArrayList<>();
        this.camera = camera;
    }

    // Getter of the list of 3D objects
    public ArrayList<Object3D> getObjects() {return objects;} 


    public void addObject(Object3D object){ // void function to add another object to the scene
        objects.add(object); // adds the object xd
    }

    // Method to find the nearest intersection between the ray and every object
    public Intersection intersect(Ray ray){
        // the closest intersection, that is the one that will be returned at the end of the function, 
        // is null at the beginning because there is no intersection yet
        Intersection closest = null; 

        for (Object3D object : objects) { // for each object, check if there is an intersection with the ray
            Intersection hit = object.intersect(ray);
            
            if(hit.isCollition_happened() && camera.isInFrustum(hit.getT())){ // if there was a collition and it is within the frustum, check if it is the closest one
                // first find collition or closest than previous one
                if(closest == null || hit.getT() < closest.getT()){closest = hit;} // if there was no closer hit, last one is
            }
        }

        if(closest == null){return new Intersection();} // if there was no closest, there was no collition
        return closest; // if there was, return it and git good
    }
}
