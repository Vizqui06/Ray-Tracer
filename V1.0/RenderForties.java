import java.awt.Color;
import java.util.List;

public class RenderForties {
        public static void main(String[] args) {renderForties(args);}
        public static void renderForties(String[] args) {

                Camera camera = new Camera(new Vector3D(
                        0.0, 0.6, 40.0), // position
                        //800.0, // temp low resolution for tests
                        //400.0, // temp low resolution for tests
                        4096.0, // width (in 4k)
                        2160.0, //height (in 4k)
                        0.001, //near plane
                        100.0, //far plane
                        0.0, // Y rotation (negative values to look at left)
                        -0.65 // X rotation (negative values to look down)
                ); 

                Scene scene = new Scene(camera); // Instances a new scene

                // Loading of textures:
                Texture textBag = new Texture("Textures/Forties/Objects/textBag.jpeg");
                Texture textBoots = new Texture("Textures/Forties/Objects/textBoots.png");
                Texture textCamera = new Texture("Textures/Forties/Objects/textCamera.jpeg");
                Texture textCrate = new Texture("Textures/Forties/Objects/textCrate.png");
                Texture textDesk = new Texture("Textures/Forties/Objects/textDesk.png");
                Texture textHat = new Texture("Textures/Forties/Objects/textHat.jpeg");
                Texture textRadio = new Texture("Textures/Forties/Objects/textRadio.jpeg");
                Texture textRifle = new Texture("Textures/Forties/Objects/textRifle.png");
                Texture textStatue = new Texture("Textures/Forties/Objects/textStatue.png");
                Texture textUniform = new Texture("Textures/Forties/Objects/textUniform.png");
                Texture textTent = new Texture("Textures/Forties/Objects/textTent.png");

                // Loading of normals:
                Texture normalBag = new Texture("Textures/Forties/Objects/normalBag.png");
                Texture normalCamera = new Texture("Textures/Forties/Objects/normalCamera.jpeg");
                Texture normalCrate = new Texture("Textures/Forties/Objects/normalCrate.png");
                Texture normalDesk = new Texture("Textures/Forties/Objects/normalCrate.png");
                Texture normalHat = new Texture("Textures/Forties/Objects/normalHat.jpeg");
                Texture normalRadio = new Texture("Textures/Forties/Objects/normalRadio.png");
                Texture normalRifle = new Texture("Textures/Forties/Objects/normalRifle.png");

                // Just load the object
                List<TriangleIntersection> defaultBag = ObjReader.load("Models/Forties/Bag.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultBoots = ObjReader.load("Models/Forties/Boots.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultCamera = ObjReader.load("Models/Forties/Camera.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultCrate = ObjReader.load("Models/Forties/Crate.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultDesk = ObjReader.load("Models/Forties/Desk.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultHat = ObjReader.load("Models/Forties/Hat.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultRadio = ObjReader.load("Models/Forties/Radio.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultRifle = ObjReader.load("Models/Forties/Rifle.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultStatue = ObjReader.load("Models/Forties/Statue.obj", Color.RED);
                List<TriangleIntersection> defaultTent = ObjReader.load("Models/Forties/Tent.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultUniform = ObjReader.load("Models/Forties/Uniform.obj", Color.DARK_GRAY);

Model3D modelBag = new Model3D(new Vector3D(0.18, 0.153, -2.0), 0.012, -2.0, 0.0, 0.9, defaultBag, 0.05, 0.002, 0.02);
Model3D modelBoots = new Model3D(new Vector3D(-1.25, -0.64, -2.2), 0.013, -90.0, 0.0, -2.0, defaultBoots, 0.12, 0.2, 0.3);
Model3D modelCamera = new Model3D(new Vector3D(-0.45, 0.415, -2.3), 0.14, 100.0, -25.0, 115.0, defaultCamera, 0.05, 0.0, 0.0);
Model3D modelCrate = new Model3D(new Vector3D(-0.48, -0.57, -2.0), 9.6, 0.0, 0.0, 0.0, defaultCrate, 0.0, 0.0, 0.0);
Model3D modelDesk = new Model3D(new Vector3D(0.1, -0.185, -2.0), 0.57, 0.0, 0.0, 0.0, defaultDesk, 0.25, 0.0, 0.0);
Model3D modelHat = new Model3D(new Vector3D(-1.22, 0.53, -2.2), 0.0014, 3.0, 35.0, 0.0, defaultHat, 0.07, 0.05, 0.05);
Model3D modelRadio = new Model3D(new Vector3D(-0.537, 0.14, -2.0), 0.004, 0.0, 0.0, 0.0, defaultRadio, 0.2, 0.0, 0.0);
Model3D modelRifle = new Model3D(new Vector3D(0.4, -0.31, -1.7), 0.84, 0.0, 80.0, -90.0, defaultRifle, 0.05, 0.0, 0.0);
Model3D modelStatue = new Model3D(new Vector3D(1.0, -0.01, -2.0), 0.055, 0.0, 65.0, 0.0, defaultStatue, 0.3, 0.01, 0.01);
Model3D modelTent = new Model3D(new Vector3D(0.0, 0.13, -2.0), 0.9, 0.0, 0.0, 0.0, defaultTent, 0.0, 0.001, 0.01);
Model3D modelUniform = new Model3D(new Vector3D(-1.25, -0.08, -2.2), 0.032, -91.5, -145.0, 0.0, defaultUniform, 0.06, 0.05, 0.05);

                // Add the objects to the scene
                modelBag.addToScene(scene);
                modelBoots.addToScene(scene);
                modelCamera.addToScene(scene);
                modelCrate.addToScene(scene);
                modelDesk.addToScene(scene);
                modelHat.addToScene(scene);
                modelRadio.addToScene(scene);
                modelRifle.addToScene(scene);
                modelStatue.addToScene(scene);
                modelTent.addToScene(scene);
                modelUniform.addToScene(scene);

                // Application of textures:
                for (TriangleIntersection tri : modelBag.getTriangles()) {tri.setTexture(textBag);}
                for (TriangleIntersection tri : modelBoots.getTriangles()) {tri.setTexture(textBoots);}
                for (TriangleIntersection tri : modelCamera.getTriangles()) {tri.setTexture(textCamera);}
                for (TriangleIntersection tri : modelCrate.getTriangles()) {tri.setTexture(textCrate);}
                for (TriangleIntersection tri : modelDesk.getTriangles()) {tri.setTexture(textDesk);}
                for (TriangleIntersection tri : modelHat.getTriangles()) {tri.setTexture(textHat);}
                for (TriangleIntersection tri : modelRadio.getTriangles()) {tri.setTexture(textRadio);}
                for (TriangleIntersection tri : modelRifle.getTriangles()) {tri.setTexture(textRifle);}
                for (TriangleIntersection tri : modelStatue.getTriangles()) {tri.setTexture(textStatue);}
                for (TriangleIntersection tri : modelTent.getTriangles()) {tri.setTexture(textTent);}
                for (TriangleIntersection tri : modelUniform.getTriangles()) {tri.setTexture(textUniform);}

                // Aplication of normals:
                for (TriangleIntersection tri : modelBag.getTriangles()) {tri.setNormalMap(normalBag);}
                for (TriangleIntersection tri : modelCamera.getTriangles()) {tri.setNormalMap(normalCamera);}
                for (TriangleIntersection tri : modelCrate.getTriangles()) {tri.setNormalMap(normalCrate);}
                for (TriangleIntersection tri : modelDesk.getTriangles()) {tri.setNormalMap(normalDesk);}
                for (TriangleIntersection tri : modelHat.getTriangles()) {tri.setNormalMap(normalHat);}
                for (TriangleIntersection tri : modelRadio.getTriangles()) {tri.setNormalMap(normalRadio);}
                for (TriangleIntersection tri : modelRifle.getTriangles()) {tri.setNormalMap(normalRifle);}

                scene.addLight(new LightPhong(new Vector3D(0.0, 0.235, 1.0), Color.WHITE, 2.0, 100.0)); // outside light
                scene.addLight(new LightPoint(null, Color.WHITE, 2.5, new Vector3D(0.0,  0.5, 1.0))); // roof light (near)
                scene.addLight(new LightPoint(null, Color.WHITE, 1.0, new Vector3D(-1.3,  1.2, 1.5))); // illumination 1
                scene.addLight(new LightPoint(null, Color.WHITE, 1.0, new Vector3D(1.3,  1.2, 1.5))); // illumination 2
                scene.addLight(new LightPoint(null, Color.WHITE, 4.5, new Vector3D(-0.2,  0.5, 3.5))); // outside light

                // Builds the Bounding Volume Hierarchy with ALL the triangles that are already added
                scene.buildTheBoundingVolumeHierarchyTree();


                // Render with white background (doesn't matter the color of the background)
                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK); // Change manually the color of background
                raytracer.render("Renders/Ray-Tracer_V099_Forties.png");
        }
}

/*

The old "cube" scenario is now replaced by an OBJ.
I leave it here just in case

Vector3D NLLV = new Vector3D(-2.5, -0.5, 8.0); // Near Lower Left
Vector3D NLRV = new Vector3D(2.5, -0.5, 8.0); // Near Lower Right
Vector3D NULV = new Vector3D(-2.5, 2.0, 8.0); // Near Upper Left
Vector3D NURV = new Vector3D(2.5, 2.0, 8.0); // Near Upper Right
Vector3D FLLV = new Vector3D(-1.8, -0.8, -4.01); // Far Lower Left
Vector3D FLRV = new Vector3D(1.8, -0.8, -4.01); // Far Lower Right
Vector3D FULV = new Vector3D(-1.8, 1.0, -2.01); // Far Upper Left
Vector3D FURV = new Vector3D(1.8, 1.0, -2.01); // Far Upper Right

// Textures for the floor, walls and roof
Texture textFloor = new Texture("Textures/Forties/Ambient/textFloor.jpg");
Texture textWalls = new Texture("Textures/Forties/Ambient/textWalls.png");
Texture textRoof = new Texture("Textures/Forties/Ambient/textRoof.jpg");

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
rightWallA.setTexture(textWalls);
rightWallB.setTexture(textWalls);
scene.addObject(rightWallA);
scene.addObject(rightWallB);

// LEFT WALL: FULV=far-up, NLLV=near-low, FLLV=far-low, NULV=near-up
TriangleIntersection leftWallA = new TriangleIntersection(NULV, NLLV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBL, uvBR);
TriangleIntersection leftWallB = new TriangleIntersection(FULV, NULV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvTR);
leftWallA.setTexture(textWalls);
leftWallB.setTexture(textWalls);
scene.addObject(leftWallA);
scene.addObject(leftWallB);

// Mirror
scene.addObject(new TriangleIntersection(new Vector3D(-1.95, 1.23, 3.5), new Vector3D(-1.95, -0.45, 3.5), new Vector3D(-1.90, -0.72, -3.9), Color.DARK_GRAY, 1.0, 0.0, 0.0));
scene.addObject(new TriangleIntersection(new Vector3D(-1.80, 0.85, -3.9), new Vector3D(-1.95, 1.23, 3.5), new Vector3D(-1.90, -0.72, -3.9), Color.DARK_GRAY, 1.0, 0.0, 0.0));

// BACK WALL: FULV=upper-left, FLLV=lower-left, FLRV=lower-right, FURV=upper-right
TriangleIntersection backWallA = new TriangleIntersection(FULV, FLLV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvBL);
TriangleIntersection backWallB = new TriangleIntersection(FURV, FULV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBR, uvTL);
backWallA.setTexture(textWalls);
backWallB.setTexture(textWalls);
scene.addObject(backWallA);
scene.addObject(backWallB);

// TOP: FULV=far-left, NURV=near-right, NULV=near-left, FURV=far-right
// Unlike the floor, use inverted normals to see the roof:
TriangleIntersection topA = new TriangleIntersection(FULV, NURV, NULV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvBR);
TriangleIntersection topB = new TriangleIntersection(FULV, FURV, NURV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvTR);
topA.setTexture(textRoof);
topB.setTexture(textRoof);
scene.addObject(topA);
scene.addObject(topB);

*/