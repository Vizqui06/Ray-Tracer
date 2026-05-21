import java.awt.Color;
import java.util.List;

public class RenderForties {
        public static void renderForties(String[] args) {

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
                        0.0, 0.55, 6.0), // position
                        //800.0, // temp low resolution for tests
                        //600.0, // temp low resolution for tests
                        4096.0, // width (in 4k)
                        2160.0, //height (in 4k)
                        0.001, //near plane
                        100.0, //far plane
                        0.0, // Y rotation (negative values to look at left)
                        -3.0 // X rotation (negative values to look down)
                ); 

                // Scene with 2 spheres
                Scene scene = new Scene(camera); // Instances a new scene
                // Note to self and users: because I parametrize the camera pixels, the vector can only manage from -1 to +1
                // Values beyond these limits will make the objects seen by half or dissappear from the scene


                // OBJECTS SETTINGS:

                // Sphere(radio, center, color)

                // radio: sphere size in scenario/world units
                //   Useful range: 0.1 to 2.0 to fit on screen
                // center Vector3D(x, y, z):
                //   X: horizontal position (-1=left, 0=center, 1=right)
                //   Y: vertical position (-1=down, 0=center, 1=up)
                //   Z: depth. It should be between nearPlane and farPlane of the camera
                // With camera at Z=5 and objects at Z=0, t around 5 --> the sphere is inside the frustum

                //scene.addObject(new Sphere(0.22, new Vector3D(0.35, 0.35, -2.3), Color.RED, 0.4, 0.4, 0.6)); // add a red sphere with its parameters

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
                
                Vector3D NLLV = new Vector3D(-2.0, -0.5, 8.0); // Near Lower Left
                Vector3D NLRV = new Vector3D(2.0, -0.5, 8.0); // Near Lower Right
                Vector3D NULV = new Vector3D(-2.0, 1.8, 8.0); // Near Upper Left
                Vector3D NURV = new Vector3D(2.0, 1.8, 8.0); // Near Upper Right
                Vector3D FLLV = new Vector3D(-1.7, -0.8, -4.0); // Far Lower Left
                Vector3D FLRV = new Vector3D(1.7, -0.8, -4.0); // Far Lower Right
                Vector3D FULV = new Vector3D(-1.7, 1.0, -2.0); // Far Upper Left
                Vector3D FURV = new Vector3D(1.7, 1.0, -2.0); // Far Upper Right


                // Textures for the floor, walls and roof
                Texture textFloor = new Texture("Textures/Forties/Ambient/textFloor.png");
                Texture textBricks = new Texture("Textures/Forties/Ambient/textBricks.jpg");
                Texture textMetal = new Texture("Textures/Forties/Ambient/textMetal.jpg");

                // UV corners reused across all planes
                Vector3D uvBL = new Vector3D(0.0, 0.0, 0.0); // bottom-left of texture
                Vector3D uvBR = new Vector3D(1.0, 0.0, 0.0); // bottom-right of texture
                Vector3D uvTL = new Vector3D(0.0, 1.0, 0.0); // top-left of texture
                Vector3D uvTR = new Vector3D(1.0, 1.0, 0.0); // top-right of texture

                // FLOOR: FLLV=far-left, NLLV=near-left, NLRV=near-right, FLRV=far-right
                TriangleIntersection floorLeft = new TriangleIntersection(NLLV, NLRV, FLLV, Color.WHITE, 0.05, 0.0, 0.1, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection floorRight = new TriangleIntersection(FLLV, NLRV, FLRV, Color.WHITE, 0.05, 0.2, 0.1, null, null, null, uvTL, uvBR, uvTR);
                floorLeft.setTexture(textFloor);
                floorRight.setTexture(textFloor);
                scene.addObject(floorLeft);
                scene.addObject(floorRight);


                // WALLS TO COVER THE SCENE:
                // RIGHT WALL: FURV=far-up, FLRV=far-low, NLRV=near-low, NURV=near-up
                TriangleIntersection rightWallA = new TriangleIntersection(FURV, FLRV, NLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection rightWallB = new TriangleIntersection(FURV, NLRV, NURV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvTL, uvBR);
                rightWallA.setTexture(textBricks);
                rightWallB.setTexture(textBricks);
                scene.addObject(rightWallA);
                scene.addObject(rightWallB);


                // LEFT WALL: FULV=far-up, NLLV=near-low, FLLV=far-low, NULV=near-up
                TriangleIntersection leftWallA = new TriangleIntersection(NULV, NLLV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBL, uvBR);
                TriangleIntersection leftWallB = new TriangleIntersection(FULV, NULV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvTR);
                leftWallA.setTexture(textBricks);
                leftWallB.setTexture(textBricks);
                scene.addObject(leftWallA);
                scene.addObject(leftWallB);
                
                // Mirror
                scene.addObject(new TriangleIntersection(new Vector3D(-1.95, 1.23, 3.5), new Vector3D(-1.95, -0.45, 3.5), new Vector3D(-1.90, -0.72, -3.9), Color.DARK_GRAY, 1.0, 0.0, 0.0));
                scene.addObject(new TriangleIntersection(new Vector3D(-1.80, 0.85, -3.9), new Vector3D(-1.95, 1.23, 3.5), new Vector3D(-1.90, -0.72, -3.9), Color.DARK_GRAY, 1.0, 0.0, 0.0));

                // BACK WALL: FULV=upper-left, FLLV=lower-left, FLRV=lower-right, FURV=upper-right
                TriangleIntersection backWallA = new TriangleIntersection(FULV, FLLV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvBL);
                TriangleIntersection backWallB = new TriangleIntersection(FURV, FULV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBR, uvTL);
                backWallA.setTexture(textBricks);
                backWallB.setTexture(textBricks);
                scene.addObject(backWallA);
                scene.addObject(backWallB);


                // TOP: FULV=far-left, NURV=near-right, NULV=near-left, FURV=far-right
                // Unlike the floor, use inverted normals to see the roof:
                TriangleIntersection topA = new TriangleIntersection(FULV, NURV, NULV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection topB = new TriangleIntersection(FULV, FURV, NURV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvTR);
                topA.setTexture(textMetal);
                topB.setTexture(textMetal);
                scene.addObject(topA);
                scene.addObject(topB);

        // OBJ MODELS SETTINGS
        // load(PathFile, color)
        //   PathFile: relative path from where the program is running to the .obj file you want to load
        //      for example: "Models/teapot.obj" or just "teapot.obj" (if the .obj is in the same folder as the program)
        //      color: flat color for all triangles in the model

        // The method automatically triangulates faces with more than 3 vertices
        // Useful models to try: any simple internet .obj 

        // PD: Have mercy with your computer by the number of polygons

                // Loading of textures:

                Texture textBicycle = new Texture("Textures/Forties/Objects/textBicycle.jpg");
                Texture textBoots = new Texture("Textures/Forties/Objects/textBoots.png");
                Texture textPistol = new Texture("Textures/Forties/Objects/textPistol.jpg");
                Texture textRadio = new Texture("Textures/Forties/Objects/textRadio.jpg");
                Texture textUniform = new Texture("Textures/Forties/Objects/textUniform.png");

                // Just load the object (at this point, it is centered at the origin and its scale is 1.0x)

                List<TriangleIntersection> defaultBicycle = ObjReader.load("Models/Forties/Bicycle.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultBoots = ObjReader.load("Models/Forties/Boots.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultRadio = ObjReader.load("Models/Forties/Radio.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultPistol = ObjReader.load("Models/Forties/Pistol.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultUniform = ObjReader.load("Models/Forties/Uniform.obj", Color.DARK_GRAY);


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
        // 0° = default object orientation (can be looking who knows where)
        // 90° = turned 90° to the right
        // 180° = it depends, but hopefully, looking towards the camera (-Z)
        // 270° = turned 90° to the left

        Model3D modelBicycle = new Model3D(new Vector3D(-0.65, -0.24, -2.6), 0.046, -90.0, -90.0, 0.0, defaultBicycle, 0.0, 0.0, 0.0);
        Model3D modelBoots = new Model3D(new Vector3D(-0.85, -0.62, -2.2), 0.018, -90.0, 25.0, -2.0, defaultBoots, 0.08, 0.2, 0.3);
        Model3D modelPistol = new Model3D(new Vector3D(0.35, -0.52, -1.25), 0.0030, -20.0, 250.0, 170.0, defaultPistol, 0.1, 0.2, 0.3);
        Model3D modelRadio = new Model3D(new Vector3D(0.75, -0.427, -1.6), 0.0018, 4.0, -25.0, 2.8, defaultRadio, 0.07, 0.2, 0.3);
        Model3D modelUniform = new Model3D(new Vector3D(0.78, -0.05, -2.3), 0.032, -91.5, -210.0, 0.0, defaultUniform, 0.02, 0.0, 0.0);
                
                // Requirements: 
                // List of triangles (the default model of ObjReader)
                // Position in the world, scale and Y-axis-rotation (where to look)

                modelBicycle.addToScene(scene);
                modelBoots.addToScene(scene);
                modelPistol.addToScene(scene);
                modelRadio.addToScene(scene);
                modelUniform.addToScene(scene);

                // Application of textures:
                for (TriangleIntersection tri : modelBicycle.getTriangles()) {tri.setTexture(textBicycle);}
                for (TriangleIntersection tri : modelBoots.getTriangles()) {tri.setTexture(textBoots);}
                for (TriangleIntersection tri : modelPistol.getTriangles()) {tri.setTexture(textPistol);}
                for (TriangleIntersection tri : modelRadio.getTriangles()) {tri.setTexture(textRadio);}
                for (TriangleIntersection tri : modelUniform.getTriangles()) {tri.setTexture(textUniform);}

        // Directional light settings:
        // Light(direction, color, intensity)
        // direction: Vector3D(x, y, z) from where the light is coming
        // color: Color of the light (white, red, blue, etc)
        // intensity: how strong the light is (0.0 = total darkness, 1.0 = maximum brightness of color)
        // Useful range for direction: any vector, it will be normalized internally. For example, (0, 0, 1) means that the light 
        // comes from the front of the scene (from user to the far away)
        // Useful range for intensity: 0.0 to 1.0

                /* 
                scene.addLight(new LightPhong(new Vector3D(0.0, 0.6, -1.0), Color.WHITE, 0.7, 20.0));
                scene.addLight(new LightPoint(null, Color.WHITE, 2.0, new Vector3D(0.0, 1.7, 2.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(-1.6, 1.7, 0.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(1.6, 0.8, 0.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(-1.6, 0.8, 4.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(1.6, 0.8, 4.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.2, new Vector3D(0.0, 1.2, -3.5)));
                */

                scene.addLight(new LightPhong(new Vector3D(0.0, 0.4, -1.0), Color.WHITE, 0.7, 20.0));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(-0.5, 0.5, 1.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(0.9, 0.3, -1.5)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.2, new Vector3D(-0.9, 0.3, -3.0)));


                // The direction of the light will be normalized, the values mean that the light comes from the upper front of the scene 
                // (from user to the far away, and from up to down)
                // The color of the light is WHITE, its intensity is at max (drastic difference in lights/shadows)                


                // Builds the Bounding Volume Hierarchy with ALL the triangles that are already added
                // If this method is NOT ADDED to Main, all triangles WILL NOT be rendered, this because all triangles migrated
                // to another list where are treated alike the non-triangle objects.
                scene.buildTheBoundingVolumeHierarchyTree();


                // Render with white background (doesn't matter the color of the background)
                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK); // Change manually the color of background
                raytracer.render("Renders/Ray-Tracer_V10_Forties.png");
        }
}