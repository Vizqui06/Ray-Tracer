import java.awt.Color;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Instructions on how to use the main parameters (its kinda difficult if you don't have a fresh context):
        // CAMERA SETTINGS:
        // positionWorld: where in the scene is the camera
        //   X: move the left/right camera (negative = left)
        //   Y: move the camera up/down (negative = down)
        //   Z: move the camera farther or closer from the scene (positive = farther)
        //      Useful range: Z between 1.0 and 10.0 for objects at Z=0

        // width / height: image resolution in pixels

        // nearPlane: minimum distance to render.
        //   Objects with t < near are not taken into consideration to render (they are "inside" or behind the camera)
        //   Useful range: 0.01 to 1.0. DO NOT use 0 (it would divide by 0 internally)

        // farPlane: maximum distance to render.
        //   Objects with t > far are not taken into consideration to render (they are too far away from the "sight")
        //   Useful range: 10.0 to 1000.0 depending on scene size
        //   It must ALWAYS BE GREATER THAN THE NEARPLANE


        // Camera in Z = 5, looking at -Z axis (the scene and not me)
        // nearPlane of 0.1 is very close, farPlane of 100 is decently far
        Camera camera = new Camera(new Vector3D(0, 0, 5), 800, 600, 0.1, 100); 
                                                // position,           width,       height,      near plane,     far plane

        // Scene with 2 spheres
        Scene scene = new Scene(camera); // Instances a new scene
        // Note to self and users: because I parametrize the camera pixels, the vector can only manage from -1 to +1
        // Values beyond these limits will make the objects seen by half or dissappear from the scene

 
// OBJECTS SETTINGS:

// Sphere(radio, center, color)

//   radio: sphere size in scenario/world units
//     Useful range: 0.1 to 2.0 to fit on screen
//   center Vector3D(x, y, z):
//     X: horizontal position (-1=left, 0=center, 1=right)
//     Y: vertical position (-1=down, 0=center, 1=up)
//     Z: depth. It should be between nearPlane and farPlane of the camera
//        With camera at Z=5 and objects at Z=0, t around 5 --> the sphere is inside the frustum

        //scene.addObject(new Sphere(0.3, new Vector3D(0.4, -0.6, 0.5), Color.RED)); // add a red sphere with its parameters
        //scene.addObject(new Sphere(0.27, new Vector3D(0.8, 0.4, 0), Color.BLUE)); // add a blue sphere with its parameters
        //scene.addObject(new Sphere(0.2, new Vector3D(-0.4, 0.4, 0), Color.GREEN)); // add a green sphere with its parameters


// Triangle (v0, v1, v2, center, color)

// vertices of the triangle:
// v0: UPPER vertex        Vector3D(x, y, z)
// v1: LOWER LEFT vertex   Vector3D(x, y, z)
// v2: LOWER RIGHT vertex  Vector3D(x, y, z)

// Coordinates of each vertex:
//   X: horizontal position (-1=left, 0=center, 1=right)
//   Y: vertical position   (-1=down, 0=center, 1=up)
//   Z: depth. It should be between nearPlane and farPlane
//      With camera at Z=5 and objects at Z=0, t around 5 --> the sphere is inside the frustum

// Useful size: difference between vertices from 0.5 to 2.0 units
// The triangle is defined in the direction: up -> down-left -> down-right

        //scene.addObject(new TriangleIntersection(
        //new Vector3D(0, 0.5, 0), new Vector3D(-0.3, -0.2, 0), new Vector3D(0.3, -0.2, 0), Color.CYAN));




// OBJ MODELS SETTINGS
// load(PathFile, color)
//   PathFile: relative path from where the program is running to the .obj file you want to load
//      for example: "Models/teapot.obj" or just "teapot.obj" (if the .obj is in the same folder as the program)
//      color: flat color for all triangles in the model

// The method automatically triangulates faces with more than 3 vertices
// Useful models to try: any simple internet .obj 

// PD: Have mercy with your computer by the number of polygons



        List<TriangleIntersection> model = ObjReader.load("Models/PC_model.obj", Color.WHITE);
        for (TriangleIntersection triangle : model) {
            scene.addObject(triangle);
        }



        // Render with white background (doesn't matter the color of the background)
        RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK); // Change manually the color of background
        raytracer.render("Renders/Ray-Tracer_V03.png");
    }
}
