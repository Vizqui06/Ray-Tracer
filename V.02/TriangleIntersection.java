// The Möller and Trumbore algorithm for the caclulation of triangle intersection
// It doesn't matter how close is the intersection to one of the three vertex of the triangle, yet
// It is only needed to know if the ray hit the triangle (for this version).
// There are 3 vertices in a triangle: "a", "b", and "c".
// "a" is the left-down vertex; "b" is the right-down vertex; "c" is the upper vertex.

// To the ray to intersect the triangle, first we have to "set" a cartesian plane with two vectors: 
// One vector will be the "c-a" line, and the "b-a" line, having "a" as the origin of the new cartesian plane.
// Because a plane is infinite extended, if the ray IS NOT paralel to it, it will pass through the plane, eventually.
// If the plane is not perpendicular (zero or infinite solutions), it remains to check if the ray went through the triangle

// To know if the ray touched any part of the triangle, it is needed to calculate the barimetric calculations.
// Doesn't matter now if the ray went through the center or if is closer to any of the vertices, just if it touched the triangle. 

import java.awt.Color;

public class TriangleIntersection extends Object3D{
    // A triangle DOES NOT HAVE A RADIO, it has 3 vertices
    private Vector3D v0; // superior vertex
    private Vector3D v1; // inferior left vertex
    private Vector3D v2; // inferior right vertex
    
    public TriangleIntersection (Vector3D v0, Vector3D v1, Vector3D v2, Vector3D center, Color color){
        super(color, center);
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    // Getters
    public Vector3D getV0() {return v0;}
    public Vector3D getV1() {return v1;}
    public Vector3D getV2() {return v2;}

    // Setters
    public void setV0(Vector3D v0) {this.v0 = v0;}
    public void setV1(Vector3D v1) {this.v1 = v1;}
    public void setV2(Vector3D v2) {this.v2 = v2;}

    // Because is a child class, it must includ all inherit methods of father's class
    // Because it was only defined in Object3D, it must be override to be useful in triangleIntersection
    @Override
    public Intersection intersect(Ray ray) {
        // Tolerance margin to avoid false negatives on edges
        // and for the case in which the ray is almost parallel to the triangle
        double epsilon = 1e-6;

        // Acording to the teachers slides:

        // Step 1: edges from v0
        // v1v0 and v2v0 define the plane of the triangle
        Vector3D v1v0 = v1.vectorSubstraction(v0); // an axis (infinite line) that passes through v1 and v0
        Vector3D v2v0 = v2.vectorSubstraction(v0); // an axis (infinite line) that passes through v2 and v0

        // Step 2: P = D × v1v0 (cross product)
        // P helps calculate the determinant that indicates if the ray
        // is parallel to the triangle and also calculates "u" for later
        Vector3D P = ray.getDirection().productCross(v1v0);

        // Determinant = v2v0 · P (dot product)
        // If it is near to the value of 0, the ray is parallel to the plane of the triangle
        // it means that the ray completely misses the plane of the triangle
        double determinant = v2v0.productPoint(P);

        if (Math.abs(determinant) < epsilon) { // if for any case, the determinant is less of value than epsilon (the tolerance margin)
            return new Intersection(); // the ray is parallel to the triangle, so it is a miss, return an intersection without hit
        }

        // Step 3: calculate u
        // invDet avoids dividing at every step, which makes multiplying cheaper (operationally)
        double invDet = 1.0 / determinant;

        // T = O - v0: vector from vertex v0 to the origin of the ray
        // This "moves" the triangle to the origin mathematically
        Vector3D T = ray.getOrigin().vectorSubstraction(v0);

        // u is the first barycentric coordinate
        double u = invDet * T.productPoint(P); // formula provided by teacher
        // If u < 0 or u > 1, the point is outside the triangle
        if (u < 0 || u > 1) { 
            return new Intersection(); // outside the triangle, no collition
        }

        // Step 4: calculate "v"
        // Q = T × v2v0 (cross product)
        Vector3D Q = T.productCross(v2v0);

        // v is the second barycentric coordinate
        
        double v = invDet * ray.getDirection().productPoint(Q); // formula provided by teacher
        // If v < 0 or (u+v) > 1+epsilon, the point outside the triangle
        if (v < 0 || (u + v) > (1.0 + epsilon)) {
            return new Intersection(); // outside the triangle, no collition
        }

        // Step 5: If it has reached to here (no parallel to the plane AND inside the triangle), 
        // it is time to calculate the distance to the point of impact (t)
        // If t <= 0, the triangle is behind the ray --> misses the impact (a shame)
        double t = invDet * Q.productPoint(v1v0);
        if (t <= 0) {
            return new Intersection(); // can't reach to it, no collition
        }

        // if it weren't behind the triangle, the ray effectively collitioned to the triangle
        // now, is needed to calculate the exact point of impact
        Vector3D hitPoint = ray.calculatePoint(t);
        return new Intersection(t, hitPoint, this);
    }
    
}
