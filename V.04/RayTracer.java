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
                    // Here is needed to know WHICH object was hit to take its color
                    Color color = hit.getObjectHit().getColor(); // Gets the color from the object hitted -> OC: Color of object
                    Vector3D normal = hit.getNormal(); // N: normal of the collition point
                    double red=0, green=0, blue=0; // RGB accumulators to add the contribution of ALL lights

                    for (Light light : scene.getLights()){ // For every light enlisted in the scene
                        // From the formulas:
                        // L: direction of the light (it is already normalized from Light)
                        // Because it is directional, it is the same for all the points in the scene
                        Vector3D L = light.getDirection();
                        // N . L: how much the normal aims to the light (cos angle)
                        // If it results to be negative, the light comes from behind: it will not illuminate it
                        double nPointProductL = Math.max(0.0, normal.productPoint(L));
                        // The diffuse: Difusse = LC * OC * LI * (N . L)
                        // Diffuse = LightColor * ObjectColor * LightIntensity * Dot Product (Normal, Direction of light)
                        double diffusedRed = (light.getColor().getRed() / 255.0) * (color.getRed() / 255.0) * light.getIntensity() * nPointProductL; 
                        double diffusedGreen = (light.getColor().getGreen() / 255.0) * (color.getGreen() / 255.0) * light.getIntensity() * nPointProductL; 
                        double diffusedBlue = (light.getColor().getBlue() / 255.0) * (color.getBlue() / 255.0) * light.getIntensity() * nPointProductL; 

                        // Add the contribution of this primary colors to the accumulators for then clamp them and set the final color of the pixel
                        red += diffusedRed;
                        green += diffusedGreen;
                        blue += diffusedBlue;

                        // Restriction (Clamp): not red, green nor blue shall sum over than the value of integer one (1.0)
                        // This brcause the LI (Light Intensity) is maxed to 1 (255 in R,G or B) -> (normalized) 
                        // Any value beyond shall be considered as just 1.0 (max brightness) :D
                        red = Math.min(1.0, red);
                        green = Math.min(1.0, green);
                        blue = Math.min(1.0, blue);
                        // Having the values normalized is good to measure how bright/dark the object/face should be
                        // And it is great, but it must be returnes from a scale (0.0 to 1.0) to a valid RGB value:
                        Color trueColor = new Color ( (int)(red * 255), (int)(green * 255), (int)(blue * 255) );
                        image.setRGB(x, y, trueColor.getRGB()); // Set the accurate color to the image
                    }
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