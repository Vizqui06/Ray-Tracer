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
                        0.0, 2.2, 10.0), // position
                        //800.0, // temp low resolution for tests
                        //600.0, // temp low resolution for tests
                        4096.0, // width (in 4k)
                        2160.0, //height (in 4k)
                        0.01, //near plane
                        100.0, //far plane
                        0.0, // Y rotation (negative values to look at left)
                        -10.0 // X rotation (negative values to look down)
                ); 

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

                scene.addObject(new Sphere(0.22, new Vector3D(0.35, 0.35, -2.3), Color.RED, 0.4, 0.4, 0.6)); // add a red sphere with its parameters
                scene.addObject(new Sphere(0.25, new Vector3D(-0.2, 0.55, -2.0), Color.PINK, 0.9, 0.9, 1.5)); // add a pink sphere with its parameters
                scene.addObject(new Sphere(0.3, new Vector3D(-0.85, 0.9, -1.2), Color.GREEN, 0.3, 0.3, 0.4)); // add a green sphere with its parameters


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

                // Vertices for the box to "inbox" the objects inside the box
                // Box: X -6 to +6, Y -4 to +5, Z -4 to +7
                Vector3D NLLV = new Vector3D(-1.3, -0.5, 0.0); // Near Lower Left
                Vector3D NLRV = new Vector3D(1.3, -0.5, 0.0); // Near Lower Right
                Vector3D NULV = new Vector3D(-1.5, 1.8, 0.0); // Near Upper Left
                Vector3D NURV = new Vector3D(1.5, 1.8, 0.0); // Near Upper Right
                Vector3D FLLV = new Vector3D(-1.2, -0.8, -4.0); // Far Lower Left
                Vector3D FLRV = new Vector3D(1.2, -0.8, -4.0); // Far Lower Right
                Vector3D FULV = new Vector3D(-1.2, 1.0, -2.0); // Far Upper Left
                Vector3D FURV = new Vector3D(1.2, 1.0, -2.0); // Far Upper Right


                // SHADOW FLAT SURFACE:
                scene.addObject(new TriangleIntersection(FLLV, NLLV, NLRV, Color.YELLOW, 0.8, 0.6, 0.8)); // Left half flat surface
                scene.addObject(new TriangleIntersection(FLRV, FLLV, NLRV, Color.YELLOW, 0.8, 0.6, 0.8)); // Right half flat surface

                // WALLS TO COVER THE SCENE:
                // RIGHT
                scene.addObject(new TriangleIntersection(FURV, FLRV, NLRV, Color.BLUE, 0.4, 0.5, 0.6));
                scene.addObject(new TriangleIntersection(NURV, FURV, NLRV, Color.BLUE, 0.4, 0.5, 0.6));

                // LEFT
                scene.addObject(new TriangleIntersection(FULV, NLLV, FLLV, Color.BLUE, 0.4, 0.5, 0.6));
                scene.addObject(new TriangleIntersection(NULV, NLLV, FULV, Color.BLUE, 0.4, 0.5, 0.6));

                // BACK:
                scene.addObject(new TriangleIntersection(FULV, FLLV, FLRV, Color.WHITE, 0.6, 0.7, 0.7));
                scene.addObject(new TriangleIntersection(FURV, FULV, FLRV, Color.WHITE, 0.6, 0.7, 0.7));

                // TOP (inverted normals to see the roof):
                scene.addObject(new TriangleIntersection(FULV, NURV, NULV, Color.WHITE, 1.0, 1.0, 1.0));
                scene.addObject(new TriangleIntersection(FURV, NURV, FULV, Color.WHITE, 1.0, 1.0, 1.0));


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

                Model3D modelCotton = new Model3D(new Vector3D(0.95, 0.7, -1.6), 0.9, 85.0, defaultCotton, 0.6, 0.4, 0.50);
                Model3D modelPot = new Model3D(new Vector3D(-0.35, -0.25, -1.8), 0.6, 60.0, defaultPot, 0.0, 0.0, 0.0);
                Model3D modelMonky = new Model3D(new Vector3D(0.40, -0.32, -1.8), 0.5, 25, defaultMonky, 0.0, 0.0, 0.0);

                // Requirements: 
                // List of triangles (the default model of ObjReader)
                // Position in the world, scale and Y-axis-rotation (where to look)
                modelMonky.addToScene(scene);
                modelCotton.addToScene(scene);
                modelPot.addToScene(scene);


                // Application of textures:
                for (TriangleIntersection tri : modelMonky.getTriangles()) {tri.setTexture(texMonky);}

        // Directional light settings:
        // Light(direction, color, intensity)
        // direction: Vector3D(x, y, z) from where the light is coming
        // color: Color of the light (white, red, blue, etc)
        // intensity: how strong the light is (0.0 = total darkness, 1.0 = maximum brightness of color)
        // Useful range for direction: any vector, it will be normalized internally. For example, (0, 0, 1) means that the light 
        // comes from the front of the scene (from user to the far away)
        // Useful range for intensity: 0.0 to 1.0

                /*
                scene.addLight(new LightPhong(new Vector3D(0.0, 0.5, 4.0), Color.WHITE, 1.0, 80.0));
                scene.addLight(new LightPoint(null, Color.WHITE, 2.0, new Vector3D( -0.4, 0.5, -0.8)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(-0.2, 0.8, 2.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D( 0.0, -0.5, 2.8)));
                */

                scene.addLight(new LightPhong(new Vector3D(0.0, 7.0, 0.5), Color.WHITE, 0.5, 128.0));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.0, new Vector3D(0.0, 0.0, 0.5)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.0, new Vector3D(0.0, 0.2, -0.7)));

                // The direction of the light will be normalized, the values mean that the light comes from the upper front of the scene 
                // (from user to the far away, and from up to down)
                // The color of the light is WHITE, its intensity is at max (drastic difference in lights/shadows)                


                // Builds the Bounding Volume Hierarchy with ALL the triangles that are already added
                // If this method is NOT ADDED to Main, all triangles WILL NOT be rendered, this because all triangles migrated
                // to another list where are treated alike the non-triangle objects.
                scene.buildTheBoundingVolumeHierarchyTree();


                // Render with white background (doesn't matter the color of the background)
                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK); // Change manually the color of background
                raytracer.render("Renders/Ray-Tracer_V09_RefRAction.png");
        }
}