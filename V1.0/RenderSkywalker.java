import java.awt.Color;
import java.util.List;

public class RenderSkywalker {
        public static void main(String[] args) {renderSkywalker(args);}
        public static void renderSkywalker(String[] args) {
                Camera camera = new Camera(new Vector3D(
                        0.0, 0.5, 6.5), // position
                        //800.0, // temp low resolution for tests
                        //400.0, // temp low resolution for tests
                        4096.0, // width (in 4k)
                        2160.0, //height (in 4k)
                        0.001, //near plane
                        100.0, //far plane
                        0.0, // Y rotation (negative values to look at left)
                        -2.65 // X rotation (negative values to look down)
                ); 

                Scene scene = new Scene(camera); 
                
                Vector3D NLLV = new Vector3D(-2.5, -0.5, 8.0); // Near Lower Left
                Vector3D NLRV = new Vector3D(2.5, -0.5, 8.0); // Near Lower Right
                Vector3D NULV = new Vector3D(-2.5, 2.0, 8.0); // Near Upper Left
                Vector3D NURV = new Vector3D(2.5, 2.0, 8.0); // Near Upper Right
                Vector3D FLLV = new Vector3D(-1.8, -0.8, -4.01); // Far Lower Left
                Vector3D FLRV = new Vector3D(1.8, -0.8, -4.01); // Far Lower Right
                Vector3D FULV = new Vector3D(-1.8, 1.0, -2.01); // Far Upper Left
                Vector3D FURV = new Vector3D(1.8, 1.0, -2.01); // Far Upper Right


                // Textures for the universe (one single image)
                Texture textsurroundings = new Texture("Textures/Skywalker/Ambient/textUniverse.jpg");

                // UV corners reused across all planes
                Vector3D uvBL = new Vector3D(0.0, 0.0, 0.0); // bottom-left of texture
                Vector3D uvBR = new Vector3D(1.0, 0.0, 0.0); // bottom-right of texture
                Vector3D uvTL = new Vector3D(0.0, 1.0, 0.0); // top-left of texture
                Vector3D uvTR = new Vector3D(1.0, 1.0, 0.0); // top-right of texture

                // FLOOR: FLLV=far-left, NLLV=near-left, NLRV=near-right, FLRV=far-right
                TriangleIntersection floorLeft = new TriangleIntersection(NLLV, NLRV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection floorRight = new TriangleIntersection(FLLV, NLRV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvTR);
                floorLeft.setTexture(textsurroundings);
                floorRight.setTexture(textsurroundings);
                scene.addObject(floorLeft);
                scene.addObject(floorRight);


                // WALLS TO COVER THE SCENE:
                // RIGHT WALL: FURV=far-up, FLRV=far-low, NLRV=near-low, NURV=near-up
                TriangleIntersection rightWallA = new TriangleIntersection(FURV, FLRV, NLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection rightWallB = new TriangleIntersection(FURV, NLRV, NURV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvTL, uvBR);
                rightWallA.setTexture(textsurroundings);
                rightWallB.setTexture(textsurroundings);
                scene.addObject(rightWallA);
                scene.addObject(rightWallB);


                // LEFT WALL: FULV=far-up, NLLV=near-low, FLLV=far-low, NULV=near-up
                TriangleIntersection leftWallA = new TriangleIntersection(NULV, NLLV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBL, uvBR);
                TriangleIntersection leftWallB = new TriangleIntersection(FULV, NULV, FLLV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvTR);
                leftWallA.setTexture(textsurroundings);
                leftWallB.setTexture(textsurroundings);
                scene.addObject(leftWallA);
                scene.addObject(leftWallB);

                // BACK WALL: FULV=upper-left, FLLV=lower-left, FLRV=lower-right, FURV=upper-right
                TriangleIntersection backWallA = new TriangleIntersection(FULV, FLLV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvBL);
                TriangleIntersection backWallB = new TriangleIntersection(FURV, FULV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBR, uvTL);
                backWallA.setTexture(textsurroundings);
                backWallB.setTexture(textsurroundings);
                scene.addObject(backWallA);
                scene.addObject(backWallB);


                // TOP: FULV=far-left, NURV=near-right, NULV=near-left, FURV=far-right
                // Unlike the floor, use inverted normals to see the roof:
                TriangleIntersection topA = new TriangleIntersection(FULV, NURV, NULV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection topB = new TriangleIntersection(FULV, FURV, NURV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvTR);
                topA.setTexture(textsurroundings);
                topB.setTexture(textsurroundings);
                scene.addObject(topA);
                scene.addObject(topB);

                // Loading of textures
                Texture textBanana = new Texture("Textures/Skywalker/Objects/textBanana.jpg");
                Texture textBobaFett = new Texture("Textures/Skywalker/Objects/textBobaFett.png");
                Texture textDarthVader = new Texture("Textures/Skywalker/Objects/textDarthVader.png");
                Texture textGhost = new Texture("Textures/Skywalker/Objects/textGhost.png");
                Texture textKirby = new Texture("Textures/Skywalker/Objects/textKirby.png");
                Texture textReaper = new Texture("Textures/Skywalker/Objects/textReaper.jpg");
                Texture textSilverSurfer = new Texture("Textures/Skywalker/Objects/textSilverSurfer.png");
                
                // Loading of normals:
                Texture normalGhost = new Texture("Textures/Skywalker/Objects/normalGhost.png");
                Texture normalReaper = new Texture("Textures/Skywalker/Objects/normalReaper.png");

                List<TriangleIntersection> defaultBanana = ObjReader.load("Models/Skywalker/Banana.obj", Color.CYAN);
                List<TriangleIntersection> defaultBlackHole = ObjReader.load("Models/Skywalker/BlackHole.obj", Color.BLACK);
                List<TriangleIntersection> defaultBobaFett = ObjReader.load("Models/Skywalker/BobaFett.obj", Color.RED);
                List<TriangleIntersection> defaultBoo = ObjReader.load("Models/Skywalker/Boo.obj", Color.WHITE);
                List<TriangleIntersection> defaultDarthVader = ObjReader.load("Models/Skywalker/DarthVader.obj", Color.LIGHT_GRAY);
                List<TriangleIntersection> defaultGhost = ObjReader.load("Models/Skywalker/Ghost.obj", Color.BLUE);
                List<TriangleIntersection> defaultHomer = ObjReader.load("Models/Skywalker/Homer.obj", Color.YELLOW);
                List<TriangleIntersection> defaultKirby = ObjReader.load("Models/Skywalker/Kirby.obj", Color.WHITE);
                List<TriangleIntersection> defaultReaper = ObjReader.load("Models/Skywalker/Reaper.obj", Color.WHITE);
                List<TriangleIntersection> defaultSilverSurfer = ObjReader.load("Models/Skywalker/SilverSurfer.obj", Color.WHITE);

                Model3D modelBanana = new Model3D(new Vector3D(1.1, 0.19, 4.3), 0.07, -70.0, -90.0, 25.0, defaultBanana, 0.0, 0.0, 0.0);
                Model3D modelBlackHole = new Model3D(new Vector3D(-0.45, 0.12, -1.8), 0.06, 25.0, 40.0, -5.0, defaultBlackHole, 0.17, 0.05, 0.05);
                Model3D modelBobaFett = new Model3D(new Vector3D(0.9, 0.05, 4.3), 0.26, 50.0, 140.0, -35.0, defaultBobaFett, 0.1, 0.0, 0.02);
                Model3D modelBoo = new Model3D(new Vector3D(1.40, 0.32, -0.5), 0.12, 5.0, -15.0, 0.0, defaultBoo, 0.12, 0.0, 0.0);
                Model3D modelDarthVader = new Model3D(new Vector3D(0.1, 0.15, 4.3), 0.26, 30.0, 190.0, 0.0, defaultDarthVader, 0.25, 0.0, 0.02);
                Model3D modelGhost = new Model3D(new Vector3D(0.9, 0.28, 0.8), 1.6, -13.0, 11.0, 10.0, defaultGhost, 0.05, 0.3, 0.4);
                Model3D modelHomer = new Model3D(new Vector3D(-0.8, 0.6, -1.7), 0.2, 8.0, -50.0, 30.0, defaultHomer, 0.0, 0.0, 0.0);
                Model3D modelKirby = new Model3D(new Vector3D(1.65, -0.55, -1.2), 0.06, -10.0, 30.0, 60.0, defaultKirby, 0.0, 0.0, 0.0);
                Model3D modelReaper = new Model3D(new Vector3D(-1.2, -0.30, -0.8), 2.8, 0.0, 18.0, -10.0, defaultReaper, 0.05, 0.0, 0.0);
                Model3D modelSilverSurfer = new Model3D(new Vector3D(-1.2, 0.55, 1.0), 0.27, -10.0, 30.0, -25.0, defaultSilverSurfer, 0.87, 0.12, 0.2);
                
                // Add the objects to the scene
                modelBanana.addToScene(scene);
                modelBlackHole.addToScene(scene);
                modelBobaFett.addToScene(scene);
                modelBoo.addToScene(scene);
                modelDarthVader.addToScene(scene);
                modelGhost.addToScene(scene);
                modelHomer.addToScene(scene);
                modelKirby.addToScene(scene);
                modelReaper.addToScene(scene);
                modelSilverSurfer.addToScene(scene);

                // Application of textures:
                for (TriangleIntersection tri : modelBanana.getTriangles()) {tri.setTexture(textBanana);}
                for (TriangleIntersection tri : modelBobaFett.getTriangles()) {tri.setTexture(textBobaFett);}
                for (TriangleIntersection tri : modelDarthVader.getTriangles()) {tri.setTexture(textDarthVader);}
                for (TriangleIntersection tri : modelGhost.getTriangles()) {tri.setTexture(textGhost);}
                for (TriangleIntersection tri : modelKirby.getTriangles()) {tri.setTexture(textKirby);}
                for (TriangleIntersection tri : modelReaper.getTriangles()) {tri.setTexture(textReaper);}
                for (TriangleIntersection tri : modelSilverSurfer.getTriangles()) {tri.setTexture(textSilverSurfer);}
                
                // Aplication of normals:
                for (TriangleIntersection tri : modelGhost.getTriangles()) {tri.setNormalMap(normalGhost);}
                for (TriangleIntersection tri : modelReaper.getTriangles()) {tri.setNormalMap(normalReaper);}

                scene.addLight(new LightPoint(null, Color.YELLOW, 2.3, new Vector3D(-1.0,  0.5, -1.5)));
                scene.addLight(new LightPoint(null, Color.WHITE, 2.2, new Vector3D( 0.7,  0.6, 4.5)));
                scene.addLight(new LightPoint(null, Color.WHITE, 2.0, new Vector3D( 1.0,  0.1, 0.5)));
                scene.addLight(new LightPoint(null, Color.BLUE, 2.3, new Vector3D( -1.18, 0.28, 0.5)));
                scene.addLight(new LightPoint(null, Color.MAGENTA, 1.6, new Vector3D(0.3, 0.0, 1.0)));
                scene.addLight(new LightPoint(null, Color.WHITE, 1.8, new Vector3D(-1.75, -0.65, -3.6)));
                scene.addLight(new LightPhong(new Vector3D(0, -0.2, 1), Color.WHITE, 1.2, 80.0));
                scene.addLight(new LightPhong(new Vector3D(0, 0.3, 1), Color.WHITE, 1.3, 80.0));

                scene.buildTheBoundingVolumeHierarchyTree();

                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK);
                raytracer.render("Renders/Ray-Tracer_V099_Skywalker.png");
        }
}