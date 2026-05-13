public class Intersection{

    private final boolean collitionHappened; // boolean that says if there were a collition
    private final double t; // distance t that will be used in class Ray
    private final Vector3D hitPoint; // where does the ray hit
    private final Vector3D normal; // the normal of the hitten object
    private final Object3D objectHit; // the object that was hit
    private final double hitU, hitV; // the UV coordinates at the hit point, used for texturing

    // Thinking deeply, creating 2 constructors, one for collitionHappened is true and one if is not

    // If is TRUE there was a collition
    // If there was a collition, is useful to know where was the collition and what distance the ray traveled
    // NOTE: For V.04, I added the normal as a parameter to light actually work
    public Intersection(double t, Vector3D hitPoint, Object3D objectHit, Vector3D normal, double hitU, double hitV){
        // All normal, collition is true
        this.collitionHappened = true;
        this.t = t;
        this.hitPoint = hitPoint;
        this.objectHit = objectHit;
        // the normal comes from whoever created the Intersection (Sphere or Triangle)
        this.normal = normal;
        // the uv coords for texturing that come from the TriangleIntersection class
        this.hitU = hitU;
        this.hitV = hitV;
    }

    // If is FALSE there was a collition
    // because there was no collition, distance and vector for hitPoint are irrelevant, so no given args 
    public Intersection(){
        this.collitionHappened = false; // obviously, there was not a collition, that's whay of the second constructor
        this.t = -1; // -1 means there is no distance between ray origin and collition, because it never happened
        this.hitPoint = null; // collition = false --> hitPoint vector = null, no collition, no coords
        this.objectHit = null; // collition = false --> objectHit = null, no collition, no object hit
        this.normal = null; // no collition = no normal of the hitten object (there is non)
        this.hitU = 0; // no collition, no need for textures
        this.hitV = 0; // no collition, no need for textures
    }

    // Getters
    public boolean isCollition_happened() {return collitionHappened;}
    public Vector3D getHitPoint() {return hitPoint;}
    public double getT() {return t;}
    public Object3D getObjectHit() {return objectHit;}
    public Vector3D getNormal() {return normal;}
    public double getHitU() {return hitU;}
    public double getHitV() {return hitV;}
}