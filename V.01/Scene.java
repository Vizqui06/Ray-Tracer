// Scene is the container of the world. 
// Its job is to save all the objects and answer a single question: "what object does this ray hitsfirst?

import java.util.ArrayList; // for knowing what colors does each one of the objects has

public class Scene {
    private final ArrayList<Object3D> objects; // Objects list of Object3D

    public Scene(){ // the constructor, I forgot to initialize the array objects
        this.objects = new ArrayList<>();
    }

    public void addObject(Object3D object){ // void function to add another object to the scene
        objects.add(object);
    }

    public ArrayList<Object3D> getObjects() {return objects;} // Getter of the array

    // Method to find the nearest intersection between the ray and every object
    public Intersection intersect(Ray ray){
        Intersection closest = null; // null for the moment
        for (Object3D object : objects) {
            Intersection hit = object.intersect(ray);
            
            if(hit.isCollition_happened()){
                // first find collition or closest than previous one
                if(closest == null || hit.getT() < closest.getT()){closest=hit;} // if there was no closer hit, last one is
            }
        }

        if(closest == null){return new Intersection();} // if there was no closest, there was no collition
        return closest; // if there was, return it
    }
}
