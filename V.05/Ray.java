// This class will manage calculating a point along the ray given a t.

public class Ray{
    private Vector3D origin; // origin of the ray
    private Vector3D direction; // where is the ray aiming to

    public Ray(Vector3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction.normalization(); // it is better normalized ASAP
        // I don't want to know anything from the vector but its direction
    }
 
    // Getters
    public Vector3D getOrigin() {return origin;}
    public Vector3D getDirection() {return direction;}

    // Setters
    public void setOrigin(Vector3D origin) {this.origin = origin;}
    public void setdirection(Vector3D direction) {this.direction = direction;}

    public Vector3D calculatePoint(double t){ // calculate the distance "t" of the ray
        // "t" is the distance along the ray to the intersection.
        // using the formula O + tD = Point
        // using the methods already built in Vector3D
        return origin.vectorAddition(direction.scalarIt(t)); 
    }
}
