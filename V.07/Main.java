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
                Camera camera = new Camera(new Vector3D(
                        0.0, 2.5, 6.0), // position
                        800.0, // width
                        600.0, //height
                        0.01, //near plane
                        100.0, //far plane
                        0, // Y rotation (negative values to look at left)
                        -20.0 // X rotation (negative values to look down)
                ); 

                // Scene with 2 spheres
                Scene scene = new Scene(camera); // Instances a new scene
                // Note to self and users: because I parametrize the camera pixels, the vector can only manage from -1 to +1
                // Values beyond these limits will make the objects seen by half or dissappear from the scene


                // SHADOW FLAT SURFACE:
                // Set a large and flat surface/plane to recieve the shadows; shadows cannot been seen in voids, like previous renders
                // Two triangles forming a rectangle on the Y=-0.5 level (adjust Y to your scene)
                // Made large enough to cover the area visible from the camera
                // The plane will be made out of two rectangle triangles

                // FLAT SURFACE COLOCATION IN PROGRESS... IT IS HARD TO SET A PLANE

                /* 
                scene.addObject(new TriangleIntersection(
                new Vector3D(-4, -1.57,  4), // v0 vertex
                new Vector3D( 4, -1.57,  4), // v1 vertex
                new Vector3D( 4, -1.57, -4), // v2 vertex
                Color.WHITE // color of the triangle
                ));

                scene.addObject(new TriangleIntersection(
                new Vector3D(-4, -1.57,  4), // v0 vertex
                new Vector3D( 4, -1.57, -4), // v1 vertex
                new Vector3D(-4, -1.57, -4), // v2 vertex
                Color.WHITE // color of the triangle
                ));
                */

                // OBJECTS SETTINGS:

                // Sphere(radio, center, color)

                //   radio: sphere size in scenario/world units
                //     Useful range: 0.1 to 2.0 to fit on screen
                //   center Vector3D(x, y, z):
                //     X: horizontal position (-1=left, 0=center, 1=right)
                //     Y: vertical position (-1=down, 0=center, 1=up)
                //     Z: depth. It should be between nearPlane and farPlane of the camera
                //        With camera at Z=5 and objects at Z=0, t around 5 --> the sphere is inside the frustum

                scene.addObject(new Sphere(0.3, new Vector3D(0.3, -0.51, -2.0), Color.RED)); // add a red sphere with its parameters
                scene.addObject(new Sphere(0.3, new Vector3D(0.8, -0.35, -2.0), Color.BLUE)); // add a blue sphere with its parameters
                scene.addObject(new Sphere(0.3, new Vector3D(-0.8, -0.38, -2.0), Color.GREEN)); // add a green sphere with its parameters


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
                //new Vector3D(0, 0.2, 0), new Vector3D(-0.3, -0.2, 0), new Vector3D(0.3, -0.2, 0), Color.CYAN));




        // OBJ MODELS SETTINGS
        // load(PathFile, color)
        //   PathFile: relative path from where the program is running to the .obj file you want to load
        //      for example: "Models/teapot.obj" or just "teapot.obj" (if the .obj is in the same folder as the program)
        //      color: flat color for all triangles in the model

        // The method automatically triangulates faces with more than 3 vertices
        // Useful models to try: any simple internet .obj 

        // PD: Have mercy with your computer by the number of polygons

                // Loading of textures:
                Texture texMonky = new Texture("Textures/textMonke.jpg");

                // Just load the object (at this point, it is centered at the origin and its scale is 1.0x)
                List<TriangleIntersection> defaultMonky = ObjReader.load("Models/Monky.obj", Color.YELLOW);
                List<TriangleIntersection> defaultCotton = ObjReader.load("Models/Cotton.obj", Color.MAGENTA);
                List<TriangleIntersection> defaultPot = ObjReader.load("Models/Pot.obj", Color.CYAN);


        // MODEL3D SETTINGS
        // Model3D(triangles, position, scale, Y-axis-rotation)

        // position is a Vector3D(x, y, z):
        //   X: left/right   Y: up/down   Z: deep (+Z = far; -Z = close)
        //
        // scale:
        //   1.0 = default size of the OBJ (the size the artist set)
        //   0.5 = half its size     2.0 = two times its original size
        //   Trial and error untill its placed where user wants
        //
        // Y-axis-rotation:
        //   0° = default object orientation (can be looking who knows where)
        //  90° = turned 90° to the right
        // 180° = it depends, but hopefully, looking towards the camera (-Z)
        // 270° = turned 90° to the left

                Model3D modelCotton = new Model3D(new Vector3D(0.0, -0.3, -0.6), 0.5, 45.0, defaultCotton);
                Model3D modelPot = new Model3D(new Vector3D(-0.5, -0.3, -1.0), 0.35, 180.0, defaultPot);
                Model3D modelMonky = new Model3D(new Vector3D(0.42, -0.3, -1.0), 0.3, 90, defaultMonky);

                // Requirements: 
                // List of triangles (the default model of ObjReader)
                // Position in the world, scale and Y-axis-rotation (where to look)
                modelMonky.addToScene(scene);
                modelCotton.addToScene(scene);
                modelPot.addToScene(scene);


                // Application of textures:
                for (TriangleIntersection tri : modelMonky.getTriangles()) {
                       tri.setTexture(texMonky);
                }

        // Directional light settings:
        // Light(direction, color, intensity)
        // direction: Vector3D(x, y, z) from where the light is coming
        // color: Color of the light (white, red, blue, etc)
        // intensity: how strong the light is (0.0 = total darkness, 1.0 = maximum brightness of color)
        // Useful range for direction: any vector, it will be normalized internally. For example, (0, 0, 1) means that the light 
        // comes from the front of the scene (from user to the far away)
        // Useful range for intensity: 0.0 to 1.0

                //scene.addLight(new LightLambertian(new Vector3D(0.0, 2.0, 3.0), Color.WHITE, 1.0));
                //scene.addLight(new Light(new Vector3D(3.0, -7.0, 2.0), Color.WHITE, 1.0));

                //scene.addLight(new LightLambertian(new Vector3D(4.0, 5.0, 8.0), Color.WHITE, 1.0));
                //scene.addLight(new LightPhong(new Vector3D(0.0, 3.0, 4.0), Color.WHITE, 1.0, 40.0));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.0, new Vector3D(0.0, 0.0, 0.5)));


                // The direction of the light will be normalized, the values mean that the light comes from the upper front of the scene 
                // (from user to the far away, and from up to down)
                // The color of the light is WHITE, its intensity is at max (drastic difference in lights/shadows)


                


                // Builds the Bounding Volume Hierarchy with ALL the triangles that are already added
                // If this method is NOT ADDED to Main, all triangles WILL NOT be rendered, this because all triangles migrated
                // to another list where are treated alike the non-triangle objects.
                scene.buildTheBoundingVolumeHierarchyTree();


                // Render with white background (doesn't matter the color of the background)
                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK); // Change manually the color of background
                raytracer.render("Renders/Ray-Tracer_V07_Shadows.png");
        }
}