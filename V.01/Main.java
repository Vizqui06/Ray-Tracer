import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        // Camera in Z = 5, looking at -Z axis (the scene and not me)
        Camera camera = new Camera(new Vector3D(0, 0, 5), 800, 600);

        // Scene with 2 spheres
        Scene scene = new Scene(); // Instances a new scene
        // Note to self and users: because I parametrize the camera pixels, the vector can only manage from -1 to +1
        // Values beyond these limits will make the objects seen by half or dissappear from the scene

        // radius: (0.0 to 1.0), Vector 3D (-1: left, +1: right; -1: up, +1: down; nothing at the moment). Values from -1 to 1
        scene.addObject(new Sphere(0.5, new Vector3D(0., 0, 0.5), Color.RED)); // add a red sphere with its parameters
        scene.addObject(new Sphere(0.3, new Vector3D(1, 0.3, 0), Color.BLUE)); // add a blue sphere with its parameters
        scene.addObject(new Sphere(0.4, new Vector3D(-0.9, 0.4, 0), Color.GREEN)); // add a green sphere with its parameters

        // Render with white background (doesn't matter the color of the background)
        RayTracer raytracer = new RayTracer(camera, scene, Color.BLACK); // Change manually the color of background
        raytracer.render("Renders/Ray-Tracer_V01.png");
    }
}