// This class will be in charge of applying the texture to the objects (.obj)
// This will be done by uploading the texture image (PNG/JPG), and return the belonging color to every coord.
// The TriangleIntersection class will be helpful with its sample(u, v) method to sample the color

import java.awt.Color;
import java.awt.image.BufferedImage; // Color to the belonging coord in the object
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture {
    private final BufferedImage textureImage; // The PNG/JPG texture image
    private final int width, height; // width and height of the texture image

    public Texture(String filePath){ // Recieves WHERE is the texture image
        BufferedImage loadedImage = null; // To read the texture image with ImageIO
        try {
            loadedImage = ImageIO.read(new File(filePath)); // Try reading the texture image with the given direction
        } catch (IOException e) {
            System.err.println("\nThere was an error by uploading the texture image. This is the path: " + filePath);
        }
         // Take the loaded image as the new texture image to work with
        this.textureImage = loadedImage;
        // If the textureImage is NOT null, get the width and the height of the new texture image with getWidth() and getHeight() methods, 
        // otherwise, set it to 1 (to avoid errors in case of null texture)
        this.width = (textureImage != null) ? textureImage.getWidth() : 1;
        this.height = (textureImage != null) ? textureImage.getHeight() : 1;
    }

    // The "u" and "v" are normalized (0.0 to 1.0)
    // U = 0.0 means it is the left border of the obj; U = 1.0 means it is the right border of the obj
    // V = 0.0 means it is the down border of the obj; V = 1.0 means it is the upper border of the obj

    public Color sample(double u, double v){ //From u & v, returns the color it is supposed to be in the given coord (same u,v)
        if (textureImage == null) return Color.MAGENTA; // If the texture image is null, paints it with magenta to visualize better the errors
        
        // "Wrap" method: If either u or v get out of normalized values (if it results possible), the textures are repeated -> "tiling"

        // Math.floor returns the largest (closest to positive infinity) double value
        // lesser than or equal to the argument and is equal to a mathematical integer. 
        // Special cases:
            // If the argument value is already equal to a mathematical integer, then the result is the same as the argument.
            // If the argument is NaN or an infinity or positive zero or negative zero, then the result is the same as the argument.
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        // pointX (an Integer due to Math.floor) is the horizontal coordinate of the pixel in the texture image that corresponds to the given u value.
        // pointY (an Integer due to Math.floor) is the vertical coordinate of the pixel in the texture image that corresponds to the given v value.
        // In the image, the Y axis is inverted with respect to the obj --> 1.0 - V fixes it

        int pointX = (int) Math.min(u * width, width -1);
        int pointY = (int) Math.min((1.0 - v) * height, height - 1);

        // Returns the Color based on the uv coords of the texture color
        return new Color(textureImage.getRGB(pointX, pointY));
    }
}
