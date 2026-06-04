import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class RayTracer { 
    private final Camera camera; // Instances a Camera object, named camera
    private final Scene scene; // Instances a Scene object, named scene
    private final Color backgroundColor; // Instances a Color object, named backgroundColor

    public RayTracer(Camera camera, Scene scene, Color backgroundColor) { // The constructor
        this.camera = camera;
        this.scene = scene;
        this.backgroundColor = backgroundColor;
    }

    public void render(String outputPath) { // function to render the image with given width and heigth
        int width = (int) camera.getWidth();
        int height = (int) camera.getHeight();

        // BufferedImage is where the pixels get its colors
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Multithreading optimization by Claude AI:
        // Previous versions had a render loop that was purely single-threaded.
        // This version uses a fixed thread pool to process rows of pixels in parallel.

        // Variable threads is for the number of threads to use, which is set to the number of available processors for optimal performance.
        // What threads does is it creates a pool of worker threads that can execute tasks concurrently. 
        // Each task is responsible for rendering a single row of pixels.
        // ExecutorService pool is created with a fixed thread pool of the specified number of threads.
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        System.out.println("\nThreads used: " + threads);

        // The outer loop iterates over each row of pixels (from 0 to height-1). For each row, it submits a task to the thread pool.
        for (int row = 0; row < height; row++) {
            final int y = row; // This is necessary because the variable used inside the lambda expression must be effectively final.
            pool.submit(() -> { // The lambda expression defines the task that will be executed by each thread. It processes a single row of pixels.
                // The inner loop iterates over each column of pixels (from 0 to width-1) for the current row. 
                // For each pixel, it generates a ray from the camera through that pixel, checks for intersections with the scene, 
                // and determines the color to set for that pixel.
                for (int x = 0; x < width; x++) {
                    Ray ray = camera.getRay(x, y); // Generates a ray from the camera through the pixel at coordinates (x, y).

                    // Checks for intersections between the ray and objects in the scene. 
                    // It returns an Intersection object that contains information about the hit, such as whether a collision occurred, 
                    // the point of intersection, the normal at that point, etc.
                    Intersection hit = scene.intersect(ray);

                    Color color;
                    if (!hit.isCollition_happened()) {
                        // No hit at all: just paint the background, no further work needed
                        color = backgroundColor;

                    } else {
                        // Skip-bounceRay optimization:
                        // bounceRay is the recursive method that handles reflections and refractions.
                        // It is expensive because it can spawn up to MAX_BOUNCES additional rays per pixel.
                        // Most objects in the scene (walls, floors, plain models) have ZERO reflectivity and refractivity.
                        // For those objects, calling bounceRay recursively is a waste: it will enter shade(), 
                        // check both "if refLEctivity > 0" and "if refRActivity > 0", find both false, and return immediately.
                        // So: if the hit object has no reflectivity AND no refractivity, skip to the max bounce count directly.
                        // This tells bounceRay "don't bother trying to recurse", saving the overhead of the extra method calls.
                        // For objects that DO have reflectivity or refractivity (mirrors, glass, etc.), bounce from 0 as normal.
                        Object3D obj = hit.getObjectHit();
                        boolean needsBouncing = obj.getRefLEctivity() > 0.0 || obj.getRefRActivity() > 0.0;
                        int startBounce = needsBouncing ? 0 : Scene.MAX_BOUNCES;

                        // If a collision occurred, it calls the bounceRay method of the scene to calculate the color 
                        // at that point based on lighting, materials, reflections, refractions, etc.
                        color = scene.bounceRay(ray, startBounce, backgroundColor);
                    }

                    // Set the color of the pixel at (x, y) in the image.
                    image.setRGB(x, y, color.getRGB());
                }
            });
        }
        // Shutdown the thread pool and wait for all tasks to finish before saving the image
        pool.shutdown();

        // Wait for all threads to finish (with a timeout to prevent hanging indefinitely)
        try { pool.awaitTermination(1, TimeUnit.HOURS);}
        // In case of interruption, handle it by re-interrupting the thread and printing a message.
        catch (InterruptedException e) {Thread.currentThread().interrupt();}

        // Save the image
        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("\n Image saved in: " + outputPath);
        } catch (IOException e) {System.out.println("\n Error saving image: " + e.getMessage());}
    }
}