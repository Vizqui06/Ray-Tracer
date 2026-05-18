// This class is the most difficult one, becasue it merges all the other classes

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

        // Iterate over each pixel of the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // 1. Camera generates the ray for this pixel
                Ray ray = camera.getRay(x, y);

                // 2.Scene looks for the nearest intersection
                Intersection hit = scene.intersect(ray);

                // 3. Paint according the results
                if (hit.isCollition_happened()) {
                    // Put the logic of the color given due the collition with the object in scene
                    // Now, I put a method to calls the function from Scene, adding reflection with max 3 bounces
                    // renderedPixelColor handles a ray, 0-3 bounces and the backgroundColor if a ray didn't intersect an object
                    Color renderedPixelColor = scene.bounceRay(ray, 0, backgroundColor);
                    // Sets the renderedPixelColor in the coords the ray hit the object
                    image.setRGB(x, y, renderedPixelColor.getRGB());

                } else {
                    image.setRGB(x, y, backgroundColor.getRGB()); // leaves it blank/background
                }
            }
        }

        // Save the image
        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("\n Image saved in: " + outputPath);
        } catch (IOException e) {
            System.out.println("\n Error saving image: " + e.getMessage());
        }
    }
}