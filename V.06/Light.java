import java.awt.Color;
// To implement the Phong Light and Smooth Shading, Light must be abstract
public abstract class Light {

    public final Vector3D direction;
    public final Color color;
    public final double intensity;

    public Light(Vector3D direction, Color color, double intensity) {
        // Note: LightPoint sends null as direction, which would cause a NullPointerException
        // Modify the normalization to avoid the program to crash
        // If the direction is null, it keeps it as null (for LightPoint), otherwise it will normalize it (for Lambertian and Phong)
        this.direction = (direction != null) ? direction.normalization() : null;
        this.color = color;
        this.intensity = intensity;
    }

    // Getters
    public Vector3D getDirection() {return direction;}
    public Color getColor() {return color;}
    public double getIntensity() {return intensity;}

    // Each child (Lambertian and Phong) implements how it contributes to the final pixel color.
    // Returns a normalized Color (each channel already multiplied by object color, intensity, etc.)
    // RayTracer just accumulates the RGB values returned here.
    public abstract Color makeColor(Vector3D normal, Vector3D hitPoint, Color objectColor, Camera camera);
}