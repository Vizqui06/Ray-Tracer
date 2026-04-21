public class Intersection{

    private boolean collitionHappened; // boolean that says if there were a collition
    private double t; // distance t that will be used in class Ray
    private Vector3D hitPoint; // where does the ray hit
    private Object3D objectHit;
    

    // Thinking deeply, creating 2 constructors, one for collitionHappened is true and one if is not

    // If is TRUE there was a collition
    // If there was a collition, is useful to know where was the collition and what distance the ray traveled
    public Intersection(double t, Vector3D hitPoint, Object3D objectHit) {
        // All normal, collition is true
        this.collitionHappened = true;
        this.t = t;
        this.hitPoint = hitPoint;
        this.objectHit = objectHit;
    }

    // If is FALSE there was a collition
    // because there was no collition, distance and vector for hitPoint are irrelevant, so no given args 
    public Intersection(){
        this.collitionHappened = false; // obviously, there was not a collition, that's whay of the second constructor
        this.t = -1; // -1 means there is no distance between ray origin and collition, because it never happened
        this.hitPoint = null; // collition = false --> hitPoint vector = null, no collition, no coords
    }

    // Getters
    public boolean isCollition_happened() {return collitionHappened;}
    public Vector3D getHitPoint() {return hitPoint;}
    public double getT() {return t;}
    public Object3D getObjectHit() {return objectHit;}
    
}


/*
I was allucinating here, is what no AI involved causes :(

public boolean isHit(Ray distanceT, Ray whereLanded){
        if (collitionHappened == true) {
            getHitPoint();
            return true;
        } else {
            return false;
        }
    }

*/