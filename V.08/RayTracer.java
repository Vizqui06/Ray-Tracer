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
                    // Gets the color from the object hitted, now implementing the textures with the UVs
                    Color color = hit.getObjectHit().rightColor(hit.getHitU(), hit.getHitV()); 
                    Vector3D normal = hit.getNormal(); // N: normal of the collition point
                    double red=0, green=0, blue=0; // RGB accumulators to add the contribution of ALL lights

                    for (Light light : scene.getLights()){ // For every light enlisted in the scene

                        // Shadow implementation for V.07:
                        // Vector and distance to ask the scene if the hit point is blocked from this light
                        // So can be known if it is a shadow or it is lighted before accumulating light for pixels
                        Vector3D directionToLight = light.getDirectionToLight(hit.getHitPoint());
                        double distanceToLight = light.getDistanceToLight(hit.getHitPoint());

                        // If the hitten point of the object is in a shadow, skips the "contribution" color entirely (down below method)
                        // Continue means the light contributes 0 to the hitten pixel (turns black)
                        if(scene.IsInShadow(hit.getHitPoint(), directionToLight, distanceToLight)){continue;}

                        // If is not in shadow, same process than before:

                        
                        // The logic was replaced for the Color function in Light
                        // Can be applied to Lambertian and Phong
                        Color contribution = light.makeColor(normal, hit.getHitPoint(), color, camera);
                        red   += contribution.getRed() / 255.0;
                        green += contribution.getGreen() / 255.0;
                        blue  += contribution.getBlue() / 255.0;
                    }

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