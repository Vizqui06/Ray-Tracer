// Camera is an entity 
// Field of View (horizontal and vertical)
// Resolution (width, height)
// Recommended not declare it as an mere object. Due to its nature, it is better to make it independent.

public class Camera{


    // NOTES FOR UPDATE (V.02):
    // Camera will also define the range where objects in the scene will be rendered or not, for avoiding useless operations.
    // There will be two vertical barriers, two matrices of the lenght of width, height and 1 pixel in the Z axis.
    // If an object IS NOT BETWEEN THE FRUSTUM (the two barriers), it WILL NOT BE RENDERED.
    // If an object is touching in any mean one of the barriers (no matter its form or inclination), its part out of the frustum will not be rendered.

    // The teacher made a vertical grid with the size (90, 90, 1) pixels, that is dependant to the camera position.
    // This grid can be divided by 4 quadrantes (45*45 pixels each). The rays go from the camera and first touch a pixel of the grid.
    // Then, they get a slope that, with that slop, go through the scenario and, may or may not, intersect with an object.

    // Now, with the addition of the frustum, it will only matters if the ray impact an object within the parameters of the frustum
    // Again: ANY pixel of ANY object will not be rendered if they are not within the frustum area, even if the ray would have intersected it. 


    private Vector3D positionWorld; // where in the scene is the camera
    private double width, height, rotation; // width and height of the resolution
    private double nearPlane, farPlane; // near and far plane of the frustum, objects with t < near or t > far will not be rendered

    public Camera(Vector3D positionWorld, double width, double height, double nearPlane, double farPlane, double rotation) {
        this.positionWorld = positionWorld.rotateY(rotation);
        this.width = width;
        this.height = height;
        this.nearPlane = 1e-6;
        this.farPlane = 1000;
        this.rotation = rotation;

    }
    // Getters
    public Vector3D getPositionWorld() {return positionWorld;}
    public double getWidth() {return width;}
    public double getHeight() {return height;}
    public double getNearPlane() {return nearPlane;}
    public double getFarPlane() {return farPlane;}
    public double getRotation() {return rotation;}

    // Setters
    public void setPositionWorld(Vector3D positionWorld) {this.positionWorld = positionWorld;}
    public void setWidth(double width) {this.width = width;}
    public void setHeight(double height) {this.height = height;}
    public void setNearPlane(double nearPlane) {this.nearPlane = nearPlane;}
    public void setFarPlane(double farPlane) {this.farPlane = farPlane;}
    public void setRotation(double rotation) {this.rotation = rotation;}


    // Function to verify if an intersection with an object was within the visible volume, the frustum
    public boolean isInFrustum(double t){
        return t>nearPlane && t<farPlane; // t is the distance from origin to an intersection
    }

    // Camera has to convert pixels (x,y) into a Ray. Good thing it has no perspective, yet
    // Pixels range are limited: X goes from 0 to width -1; Y goes from 0 to height -1
    // But rays need centered coords coords around 0: from -1 to +1 (like the dot product)
    // getRay function can manage the comunication between pixels and vectors

    public Ray getRay(int x, int y){ // Managementent of the rays in the ortographic projection
        // After testing, I forgot that projecting a square space onto a rectangular image makes everything stretch horizontally
        // Multiplying the screenX with the aspectRatio should resolve the problem
        double aspectRatio = width / height;

        // First, is needed to normaliza (range 0 to 1) the pixels so can be easier to target
        // It is normalized dividing the position of X/Y (in terms of width/height) by its width/height
        // And because is in range 0 to 1, add 0.5 to hit the middel of the pixel
        // It is guaranteed (by the 0.5) that it never gets out of bonds in any case
        double normalizedPixelX = (x + 0.5) / width;
        double normalizedPixelY = (y + 0.5) / height;

        // pixel 0:  (0 + 0.5) / 800 = 0.000625  ← almost in the left bound
        // pixel 400: (400 + 0.5) / 800 = 0.500625 ← almost in the exact center
        // pixel 799: (799 + 0.5) / 800 = 0.999375 almost in the right bound

        // Rays can only manage distances between -1 to +1; with the normalized pixels, only a half is available
        // To screenX is pretty easy to set the parameters from (0-1) to (-1,1):
        // normalizedPixelX = 0.0 -->  screenX = -1.0  (left bound)
        // normalizedPixelX = 0.5 -->  screenX = 0.0  (rigth in center)
        // normalizedPixelX = 1.0 -->  screenX = 1.0  (right bound)

        double screenX = ((2* normalizedPixelX) - 1) * aspectRatio;

        // But in the width, above y=0 is out of bounds, so it cannot work as well as X
        // The values must respect these rules:

        // normalizedPixelY = 0.0 -->  screenY = 1.0  (pixel above bound) --> y goes up to down, 1 is positive, height of pixel is max
        // normalizedPixelY = 0.5 -->  screenY = 0.0  (pixel center) --> as the normal increase, the height of pixel decreases
        // normalizedPixelY = 1.0 -->  screenY = -1.0  (pixel below bound) --> before goes to next pixel below, height of pixel is min

        double screenY = 1 - (2* normalizedPixelY);

        // The ray's origin travels according to the pixel
        // Because is a ortographic projection, Z is not needed directly
        // Therefore, only X and Y changes along the pixels across the width and height

        // In ortographic projection, all rays are paralels, in 2D it does not change the direction, changes where the ray came from
        // All rays come from the same plane of the camera (in this case, its 2D)
        Vector3D rayOrigin = new Vector3D(positionWorld.getX() + screenX, positionWorld.getY() + screenY, positionWorld.getZ());

        // Ortographic projection means ALL rays aim to the -Z axis
        // The +Z axis would mean the rays would renderrr ME, not the objects
        Vector3D rayDirection = new Vector3D(0,0,-1);

        // The return makes a new ray with the normalized and ortographic origin and the direction to aim the objects in -Z axis
        return new Ray(rayOrigin, rayDirection);
    }

}