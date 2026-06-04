import java.awt.Color;
import java.util.List;

public class RenderEmpire {
        public static void main(String[] args) {renderEmpire(args);}
        public static void renderEmpire(String[] args) {
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


                // Textures for the floor, walls and roof
                Texture textFloor = new Texture("Textures/Empire/Ambient/textFloor.png");
                Texture textWalls = new Texture("Textures/Empire/Ambient/textJapanWalls.jpg");
                Texture textRoof  = new Texture("Textures/Empire/Ambient/textJapanRoof.jpg");
                Texture textBack  = new Texture("Textures/Empire/Ambient/textJapanBack.jpg");

                // UV corners reused across all planes
                Vector3D uvBL = new Vector3D(0.0, 0.0, 0.0); // bottom-left of texture
                Vector3D uvBR = new Vector3D(1.0, 0.0, 0.0); // bottom-right of texture
                Vector3D uvTL = new Vector3D(0.0, 1.0, 0.0); // top-left of texture
                Vector3D uvTR = new Vector3D(1.0, 1.0, 0.0); // top-right of texture

                // FLOOR: FLLV=far-left, NLLV=near-left, NLRV=near-right, FLRV=far-right
                TriangleIntersection floorLeft = new TriangleIntersection(NLLV, NLRV, FLLV, Color.WHITE, 0.08, 0.03, 0.05, null, null, null, uvTL, uvBL, uvBR);
                TriangleIntersection floorRight = new TriangleIntersection(FLLV, NLRV, FLRV, Color.WHITE, 0.08, 0.03, 0.05, null, null, null, uvTL, uvBR, uvTR);
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

                // BACK WALL: FULV=upper-left, FLLV=lower-left, FLRV=lower-right, FURV=upper-right
                TriangleIntersection backWallA = new TriangleIntersection(FULV, FLLV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTL, uvBR, uvBL);
                TriangleIntersection backWallB = new TriangleIntersection(FURV, FULV, FLRV, Color.WHITE, 0.0, 0.0, 0.0, null, null, null, uvTR, uvBR, uvTL);
                backWallA.setTexture(textBack);
                backWallB.setTexture(textBack);
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

                Texture textCup = new Texture("Textures/Empire/Objects/textCup.jpg");
                Texture textVase = new Texture("Textures/Empire/Objects/textVase.jpg");

                List<TriangleIntersection> defaultCup = ObjReader.load("Models/Empire/Cup.obj", Color.WHITE);
                List<TriangleIntersection> defaultSamurai = ObjReader.load("Models/Empire/Samurai.obj", Color.RED);
                List<TriangleIntersection> defaultScroll = ObjReader.load("Models/Empire/Scroll.obj", Color.LIGHT_GRAY);
                List<TriangleIntersection> defaultTable = ObjReader.load("Models/Empire/Table.obj", Color.WHITE);
                List<TriangleIntersection> defaultVase = ObjReader.load("Models/Empire/Vase.obj", Color.WHITE);
                List<TriangleIntersection> defaultPot = ObjReader.load("Models/Empire/Pot.obj", Color.CYAN);

                Model3D modelCup = new Model3D(new Vector3D(0.24, -0.02, 2.8), 3.3, 90.0, -90.0, 180.0, defaultCup, 0.4, 0.2, 0.15);
                Model3D modelSamurai = new Model3D(new Vector3D(-1.2, 0.145, -2.0), 0.00008, 0.0, 30.0, 0.0, defaultSamurai, 0.1, 0.1, 0.1);
                Model3D modelScroll = new Model3D(new Vector3D(-0.22, -0.11, 2.8), 0.003, 32.0, 184.0, -95.0, defaultScroll, 0.1, 0.05, 0.05);
                Model3D modelTable = new Model3D(new Vector3D(0.0, -0.50, 2.8), 0.07, 1.0, 0.0, 0.0, defaultTable, 0.3, 0.2, 0.05);
                Model3D modelVase = new Model3D(new Vector3D(1.50, -0.313, -1.8), 0.00187, -1.0, -22.0, -1.0, defaultVase, 0.3, 0.0, 0.0);
                Model3D modelPot = new Model3D(new Vector3D(0.6, 0.42, 2.8), 0.40, 0.0, 180.0, 45.0, defaultPot, 0.75, 0.35, 0.5);

                modelCup.addToScene(scene);
                modelSamurai.addToScene(scene);
                modelScroll.addToScene(scene);
                modelTable.addToScene(scene);
                modelVase.addToScene(scene);
                modelPot.addToScene(scene);

                // Application of textures:
                for (TriangleIntersection tri : modelCup.getTriangles()) {tri.setTexture(textCup);}
                for (TriangleIntersection tri : modelVase.getTriangles()) {tri.setTexture(textVase);}

                scene.addLight(new LightPoint(null, Color.WHITE, 2.0, new Vector3D( 0.0,  0.9,  2.8))); // above the table
                scene.addLight(new LightPoint(null, Color.WHITE, 1.8, new Vector3D(-1.0,  0.9, -1.5))); // above the samurai side
                scene.addLight(new LightPoint(null, Color.WHITE, 1.8, new Vector3D( 1.0,  0.9, -1.5))); // above the vase side
                scene.addLight(new LightPoint(null, Color.WHITE, 1.5, new Vector3D( 0.0,  0.9, -3.5))); // near the back wall
                scene.addLight(new LightPhong(new Vector3D(0, -0.2, 1), Color.WHITE, 0.5, 80.0));
                
                scene.buildTheBoundingVolumeHierarchyTree();

                RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK);
                raytracer.render("Renders/Ray-Tracer_V099_Empire.png");
        }
}