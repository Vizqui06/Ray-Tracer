public class Boxes {
    private final Vector3D minEdgeBox; // minimum edge of the box (minEdgeBox_X, minEdgeBox_Y, minEdgeBox_Z)
    private final Vector3D maxEdgeBox; // maximum edge of the box (maxEdgeBox_X, maxEdgeBox_Y, maxEdgeBox_Z)

    public Boxes (Vector3D minEdgeBox, Vector3D maxEdgeBox){
        this.minEdgeBox = minEdgeBox;
        this.maxEdgeBox = maxEdgeBox;
    }

    // Getters
    public Vector3D getMinEdgeBox() {return minEdgeBox;}
    public Vector3D getMaxEdgeBox() {return maxEdgeBox;}

    public static Boxes surrounding(Boxes a, Boxes b) {
        // This method builds a minimum size box that is able to contain two boxes -> it will be used when building the BVH tree
        // The "wrapping" of two boxes is simply the box whose minimums and maximums edges are the minimums and maximums of both boxes combined
        // An example of how the wrapping should work (ignoring Z axis):
        // Box A (0,0) , (1,1); Box B (1,2) , (3,3) --> Box C (0,0) , (3,3). ¿But why (0,0) , (3,3)?

        // X axis:
        // What is the leftmost left edge? --> minimum X value of minEdgeBox between A & B (0,1) = 0 --> (0,_) , (_,_)
        // What is the rightmost right edge? --> maximum X value of maxEdgeBox between A & B (1,3) = 3 --> (0,_) , (3,_)

        // Y axis:
        // What is the bottom edge below? --> minimum Y value of minEdgeBox between A & B (0,2) = 0 --> (0,0) , (3,_)
        // What is the top edge above? --> maximum Y value of maxEdgeBox between A & B (1,3) = 3 --> (0,0) , (3,3)

        // The returned box that wraps box A & B: (0,0) , (3,3) + Z axis
        
        Vector3D newMinEdgeBox = new Vector3D( // the new minimum edge of the box is the minimum of the two boxes' minimum edges
            Math.min(a.minEdgeBox.getX(), b.minEdgeBox.getX()),
            Math.min(a.minEdgeBox.getY(), b.minEdgeBox.getY()),
            Math.min(a.minEdgeBox.getZ(), b.minEdgeBox.getZ())
        );
        Vector3D newMaxEdgeBox = new Vector3D( // the new maximum edge of the box is the maximum of the two boxes' maximum edges
            Math.max(a.maxEdgeBox.getX(), b.maxEdgeBox.getX()),
            Math.max(a.maxEdgeBox.getY(), b.maxEdgeBox.getY()),
            Math.max(a.maxEdgeBox.getZ(), b.maxEdgeBox.getZ())
        );
        // Finally, the new box is created with the new minimum and maximum edges
        return new Boxes(newMinEdgeBox, newMaxEdgeBox);
    }

    // After creating the surrounding boxes method to divide the scene in boxes
    // There must be a method that calculates if the ray passed throug it
    // If a ray went throug a box, it means it didn't went through the surrounding box, so half the space (children boxes of the not-hitten box)
    // will not be calculated, so every time it passes throug a box, half the ray calculations are discarded.
    
    // "Slab" method: To calculate if the ray intersects a box
    // Returns a boolean if there is an intersection within the range [minDistanceT, maxDistanceT]

    // Remider to myself:
    // "t" is the distance along the ray by the formula: Point = Origin + t * Direction
    // That "t = 2" means the intersected point is at 2 units of distance in the direction of the ray from its origin
    // Then, minDistanceT and maxDistanceT define a segment of the ray that is important to check 
    // The ray is infinite, but it is not intended to measure its entire length, only its origin to where it intersects.
    
    // minDistanceT is the Camera's nearPlane, so every lower value than the nearPlane (behind the camera) will not be renderized
    // maxDistanceT is the Camera's farPlane, so every higher value than the farPlane (too far from camera's sight) will not be renderized
    // Between the nearPlane and farPlane (frustrum) is where the Ray-Tracer looks from intersections

    public boolean intersectBox(Ray ray, double minDistanceT, double maxDistanceT) {
        // The idea/strategy of the intersectionBox is that: A box aligned to the axes is the intersection of 3 "slabs". 
        // A slab is the space between two infinite parallel planes. And the box is the volume where the 3 slabs overlap at the same time.
        // For a ray traveling through space, in each slab, the ray has a moment when it enters and a moment when it leaves. 
        // And that's an interval of the distance "t".

        // If when calculating the three intervals [enter, exit] for X, Y, and Z, the ray touches the box if and only if the three intervals overlap.
        // In any other case, it will NOT enter the box or it will rub against the box, but it wouldn't pass through it (it wouldn't count as enter).
        // In conclusion: If tEnter <= tExit --> there is a ray segment inside the box --> there is a hit.

        // NOTE: The inverse direction is intended to avoid doing an if statement to check if the direction is negative or not.



        // X axis intersection calculations:
        double inverseDirectionX = 1.0 / ray.getDirection().getX();
        double distanceT_enterX = (minEdgeBox.getX() - ray.getOrigin().getX()) * inverseDirectionX; // calculates the distance "t" when the ray enters the X slab
        double distanceT_exitX = (maxEdgeBox.getX() - ray.getOrigin().getX()) * inverseDirectionX; // // calculates the distance "t" when the ray exits the X slab
        
        // Updates the minimum distance "t" to be the maximum of the current minimum distance and the enter distance of the X slab
        minDistanceT = Math.max(minDistanceT, Math.min(distanceT_enterX, distanceT_exitX));
        
        // Updates the maximum distance "t" to be the minimum of the current maximum distance and the exit distance of the X slab
        maxDistanceT = Math.min(maxDistanceT, Math.max(distanceT_enterX, distanceT_exitX));

        // If there is no overlap in X, there is no hit, so it exits immediately without calculating Y
        if (maxDistanceT <= minDistanceT) return false; 




        // Y axis intersection calculations:
        double inverseDirectionY = 1.0 / ray.getDirection().getY();
        double distanceT_enterY = (minEdgeBox.getY() - ray.getOrigin().getY()) * inverseDirectionY; // calculates the distance "t" when the ray enters the Y slab
        double distanceT_exitY = (maxEdgeBox.getY() - ray.getOrigin().getY()) * inverseDirectionY; // calculates the distance "t" when the ray exits the Y slab
        
        // Updates the minimum distance "t" to be the maximum of the current minimum distance and the enter distance of the Y slab
        minDistanceT = Math.max(minDistanceT, Math.min(distanceT_enterY, distanceT_exitY));
        
        // Updates the maximum distance "t" to be the minimum of the current maximum distance and the exit distance of the Y slab
        maxDistanceT = Math.min(maxDistanceT, Math.max(distanceT_enterY, distanceT_exitY));

        // If there is no overlap in Y, there is no hit, so it exits immediately without calculating Z
        if (maxDistanceT <= minDistanceT) return false; 




        // Z axis intersection calculations:
        double inverseDirectionZ = 1.0 / ray.getDirection().getZ(); 
        double distanceT_enterZ = (minEdgeBox.getZ() - ray.getOrigin().getZ()) * inverseDirectionZ; // calculates the distance "t" when the ray enters the Z slab
        double distanceT_exitZ = (maxEdgeBox.getZ() - ray.getOrigin().getZ()) * inverseDirectionZ; // calculates the distance "t" when the ray exits the Z slab
        
        // Updates the minimum distance "t" to be the maximum of the current minimum distance and the enter distance of the Z slab
        minDistanceT = Math.max(minDistanceT, Math.min(distanceT_enterZ, distanceT_exitZ)); 
        
        // Updates the maximum distance "t" to be the minimum of the current maximum distance and the exit distance of the Z slab
        maxDistanceT = Math.min(maxDistanceT, Math.max(distanceT_enterZ, distanceT_exitZ)); 

        // If there was an overlap in X axis and Y axis, the ray entering the box will depend on IF there is an overlap in Z axis
        // If there is, it enters the box and returns a TRUE, if not, a FALSE
        return maxDistanceT > minDistanceT; 



        // The magic is that, instead of calculating everything and comparing at the end, you update minDistanceT and maxDistanceT with each axis. 
        // minDistanceT accumulates the maximum of the enter, and maxDistanceT accumulates the minimum of the exit.
        // If on any axis maxDistanceT <= minDistanceT --> there is no overlap and it exits the method immediately.

        // A great detail about the negative direction: 
        // If the ray traveled in the -X axis, then the maxEdgeBox_X plane would touch it before the minEdgeBox_X plane did. 
        // Splitting by inverseDirectionX would automatically reverse the order.
        // And Math.min/max is responsible for correctly assigning which is enter and which is exit, without needing an if statement.
    }
}