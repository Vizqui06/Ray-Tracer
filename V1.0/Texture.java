import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture {
    private final BufferedImage textureImage; // Stores the loaded PNG/JPG texture image
    private final int width, height; // Stores the dimensions of the texture for UV mapping calculations

    public Texture(String filePath) {
        BufferedImage loadedImage = null; 
        try {
            // Attempts to read the image file from the specified path.
            // ImageIO is used as it natively handles standard image formats like PNG and JPG.
            loadedImage = ImageIO.read(new File(filePath)); 
        } catch (IOException e) {
            // If the file is not found or cannot be read, an error is printed in the console.
            System.err.println("\nThere was an error uploading the texture image. Path: " + filePath);
        }
        this.textureImage = loadedImage;
        
        // Safe assignment: If the image failed to load (textureImage is null), 
        // width and height are defaulted to 1. This interacts with the sample() method
        // to prevent DivisionByZero or OutOfBounds exceptions during math operations.
        this.width = (textureImage != null) ? textureImage.getWidth() : 1;
        this.height = (textureImage != null) ? textureImage.getHeight() : 1;
    }

    // u and v are expected to be normalized coordinates (0.0 to 1.0).
    // U = 0.0 (left border), U = 1.0 (right border).
    // V = 0.0 (bottom border), V = 1.0 (top border).
    public Color sample(double u, double v) {
        // If no image was loaded, return MAGENTA immediately to visually flag the missing texture in the render.
        if (textureImage == null) return Color.MAGENTA; 
        
        // If the math engine calculated broken normals or barycentric coordinates due to a degenerate object
        // (like a 3D triangle with 0 area) and passed a NaN (Not a Number), is catched here.
        // This prevents the thread from crashing when casting to an integer later.
        if (Double.isNaN(u) || Double.isNaN(v)) {
            return Color.MAGENTA; 
        }

        // Math.floor returns the closest mathematical integer downwards. 
        // By subtracting this floor from the original value (u - Math.floor(u)),
        // it extracts ONLY the positive fractional part, regardless of whether u/v were negative or > 1.0.
        // This ensures coordinates always wrap smoothly and seamlessly from 0.0 to 1.0 (e.g., -0.2 becomes 0.8).
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        // Multiplies the normalized UV coordinate [0.0, 1.0) by the image dimensions.
        // Math.min is used to guarantee we never try to read exactly the out-of-bounds pixel
        // (since array indexes only go up to width-1 and height-1).
        int pointX = (int) Math.min(u * width, width - 1);
        
        // The Y axis is inverted (1.0 - v) because traditional 3D UV coordinates 
        // start at (0,0) at the bottom-left, but Java images start at (0,0) at the top-left.
        // This interacts with pointY to read the correct pixel without flipping the texture upside down.
        int pointY = (int) Math.min((1.0 - v) * height, height - 1);

        // getRGB fetches the integer containing the ARGB channels from the 2D matrix,
        // which is then encapsulated into a Java Color object and returned to the RayTracer.
        return new Color(textureImage.getRGB(pointX, pointY));
    }


    // Reads a normal map pixel at (u, v) and converts it from RGB color space to a world-space direction.
    // Normal map pixels encode directions as RGB: (0.5, 0.5, 1.0) = straight up, --> (0, 0, 1) in tangent space.
    // The conversion is: direction = (Red * 2 - 1, Green * 2 - 1, Blue * 2 - 1), then normalized.
    public Vector3D sampleNormal(double u, double v) {
        if (textureImage == null) return null; // If its null, stay as before, only texture image but no normals
        // Extracts ONLY the positive fractional part, regardless of whether u/v were negative or > 1.0.
        u = u - Math.floor(u);
        v = v - Math.floor(v);
        // The points mean the pixel coordinates in the texture image that correspond to the UV coordinates.
        int pointX = (int) Math.min(u * width,  width  - 1);
        int pointY = (int) Math.min((1.0 - v) * height, height - 1);
        // Takes the color of the normal map at the specified pixel, which encodes the normal direction in RGB channels.
        Color pixel = new Color(textureImage.getRGB(pointX, pointY));
        // Remap each channel from [0, 255] to [-1.0, 1.0]
        double nx = (pixel.getRed() / 255.0) * 2.0 - 1.0;
        double ny = (pixel.getGreen() / 255.0) * 2.0 - 1.0;
        double nz = (pixel.getBlue() / 255.0) * 2.0 - 1.0;
        // Returns the normal vector in world space, normalized to ensure it has a length of 1.0 for accurate lighting calculations.
        return new Vector3D(nx, ny, nz).normalization();
    }
}