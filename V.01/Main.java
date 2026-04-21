import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        // Camera in Z = 5, looking at -Z axis (the scene and not me)
        Camera camera = new Camera(new Vector3D(0, 0, 5), 800, 600);

        // Scene with 2 spheres
        Scene scene = new Scene(); // Instances a new scene
        scene.addObject(new Sphere(0.5, new Vector3D(0.0, 0, -5), Color.RED)); // add a red sphere with its parameters
        // scene.addObject(new Sphere(0.3, new Vector3D(5, 0, 2), Color.BLUE)); // add a blue sphere with its parameters

        // Render with white background (doesn't matter the color of the background)
        RayTracer raytracer = new RayTracer(camera, scene, Color.WHITE);
        raytracer.render("output.png");
    }
}