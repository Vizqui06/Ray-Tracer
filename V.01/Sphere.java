// Sphere, as the many other figures, is the one that will tell if a ray hit it or not (implement 3D now to later be easy)
import java.awt.Color;

public class Sphere extends Object3D{
    private double radius;
    private Vector3D center;
    
    public Sphere (double radius, Vector3D center, Color color){
        super(color);
        this. radius = radius;
        this.center = center;
    }
    // Because is a child class, it must includ all inherit methods of father's class
    // Because it was only defined in Object3D, it must be override to be useful in sphere
    @Override
    // It is not possible to make methods inside methods, so I had to rebuild the whole override again
    public Intersection intersect(Ray ray){
        // Implementation of Geometrical Solution:

        // 1. Distance L = center - ray origin
        // A vector L equals the substraction of the actual sphere center minus the origin of the ray
        Vector3D L = center.vectorSubstraction(ray.getOrigin());


        // 2. tca = dotproduct(L * direction of the ray)
        // If tca < 0: center of sphere is behind ray --> return a new intersection without hit
        double tca = L.productPoint(ray.getDirection());
        if(tca < 0){return new Intersection();} // "Intersection()" is the constructor WITHOUT hit
        

        // 3. d^2 = L*L - tca*tca --> Perpendicular square distance between radius and center 
        // If d^2 >= radius^2: the ray passes outside the sphere or hits the circumference, none of them matters

        // double d2 = Math.pow(L,2) - Math.pow(tca, 2); --> This was the first idea, but I have to make L a number with dot product
        double d2 = L.productPoint(L) - Math.pow(tca, 2);
        if(d2 >= radius*radius){return new Intersection();} // There is no hit


        // 4. thc = sqrt((radius2 - D2))
        // thc = half-length of the string inside the sphere

        double thc = Math.sqrt((radius*radius)-d2);

        // 5. Two intersection points to the sphere if all past no-hit cases were succesfully avoided:
        // Because the tangent ray was discarded, all valid hits has two intersections, no exceptions
        double t0 = tca - thc;
        double t1 = tca + thc;
        double t; // t is the nearest intersection point, don't know yet who (t0 or t1) is the nearest

        if(t0 > 0){t=t0;} // if t0 is the nearest positive intersection, t = t0
        if(t1 > 0){t=t1;} // if t1 is the nearest positive intersection, t = t1
        else{return new Intersection();} // this is if both are negative numbers: the sphere is behind the camera

        

        // 6. calculate the exact hit point using ray
        Vector3D hitPoint = ray.calculatePoint(t);
        // 7. return the intersection (recently added the object3D "this")
        return new Intersection(t, hitPoint, this); // "this" is the sphere itself

    }
}
