import java.awt.Color;
import java.util.List;

public class RenderSkywalker {
        public static void main(String[] args) {renderSkywalker(args);}
        public static void renderSkywalker(String[] args) {
                Camera camera = new Camera(new Vector3D(
                        0.0, 0.55, 6.0), // position
                        800.0, // temp low resolution for tests
                        600.0, // temp low resolution for tests
                        //4096.0, // width (in 4k)
                        //2160.0, //height (in 4k)
                        0.001, //near plane
                        100.0, //far plane
                        0.0, // Y rotation (negative values to look at left)
                        -3.0 // X rotation (negative values to look down)
                ); 

                Scene scene = new Scene(camera); 
                
                Vector3D NLLV = new Vector3D(-2.0, -0.5, 8.0); // Near Lower Left
                Vector3D NLRV = new Vector3D(2.0, -0.5, 8.0); // Near Lower Right
                Vector3D NULV = new Vector3D(-2.0, 1.8, 8.0); // Near Upper Left
                Vector3D NURV = new Vector3D(2.0, 1.8, 8.0); // Near Upper Right
                Vector3D FLLV = new Vector3D(-1.7, -0.8, -4.0); // Far Lower Left
                Vector3D FLRV = new Vector3D(1.7, -0.8, -4.0); // Far Lower Right
                Vector3D FULV = new Vector3D(-1.7, 1.0, -2.0); // Far Upper Left
                Vector3D FURV = new Vector3D(1.7, 1.0, -2.0); // Far Upper Right


                // Textures for the floor, walls and roof
                Texture textFloor = new Texture("Textures/Skywalker/Ambient/textFloor.png");
                Texture textBricks = new Texture("Textures/Skywalker/Ambient/textBricks.jpg");
                Texture textMetal = new Texture("Textures/Skywalker/Ambient/textMetal.jpg");

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


                Texture textBicycle = new Texture("Textures/Skywalker/Objects/textBicycle.jpg");
                Texture textBoots = new Texture("Textures/Skywalker/Objects/textBoots.png");
                Texture textPistol = new Texture("Textures/Skywalker/Objects/textPistol.jpg");
                Texture textRadio = new Texture("Textures/Skywalker/Objects/textRadio.jpg");
                Texture textUniform = new Texture("Textures/Skywalker/Objects/textUniform.png");

                List<TriangleIntersection> defaultBicycle = ObjReader.load("Models/Skywalker/Bicycle.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultBoots = ObjReader.load("Models/Skywalker/Boots.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultRadio = ObjReader.load("Models/Skywalker/Radio.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultPistol = ObjReader.load("Models/Skywalker/Pistol.obj", Color.DARK_GRAY);
                List<TriangleIntersection> defaultUniform = ObjReader.load("Models/Skywalker/Uniform.obj", Color.DARK_GRAY);

                Model3D modelBicycle = new Model3D(new Vector3D(-0.65, -0.24, -2.6), 0.046, -90.0, -90.0, 0.0, defaultBicycle, 0.0, 0.0, 0.0);
                Model3D modelBoots = new Model3D(new Vector3D(-0.85, -0.62, -2.2), 0.018, -90.0, 25.0, -2.0, defaultBoots, 0.08, 0.2, 0.3);
                Model3D modelPistol = new Model3D(new Vector3D(0.35, -0.52, -1.25), 0.0030, -20.0, 250.0, 170.0, defaultPistol, 0.1, 0.2, 0.3);
                Model3D modelRadio = new Model3D(new Vector3D(0.75, -0.427, -1.6), 0.0018, 4.0, -25.0, 2.8, defaultRadio, 0.07, 0.2, 0.3);
                Model3D modelUniform = new Model3D(new Vector3D(0.78, -0.05, -2.3), 0.032, -91.5, -210.0, 0.0, defaultUniform, 0.02, 0.0, 0.0);

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

                scene.addLight(new LightPhong(new Vector3D(0.0, 0.4, -1.0), Color.WHITE, 0.7, 20.0));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(-0.5, 0.5, 1.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D(0.9, 0.3, -1.5)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.2, new Vector3D(-0.9, 0.3, -3.0)));

                scene.buildTheBoundingVolumeHierarchyTree();

                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK);
                raytracer.render("Renders/Ray-Tracer_V10_Skywalker.png");
        }
}