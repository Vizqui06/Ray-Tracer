import java.awt.Color;
// To implement the Phong Light and Smooth Shading, Light must be abstract
public abstract class Light {

    public final Vector3D direction;
    public final Color color;
    public final double intensity;

    public Light(Vector3D direction, Color color, double intensity) {
        this.direction = direction.normalization();
        this.color     = color;
        this.intensity = intensity;
    }

    // Getters
    public Vector3D getDirection() {return direction;}
    public Color    getColor() {return color;}
    public double   getIntensity() {return intensity;}

    // Each child (Lambertian and Phong) implements how it contributes to the final pixel color.
    // Returns a normalized Color (each channel already multiplied by object color, intensity, etc.)
    // RayTracer just accumulates the RGB values returned here.
    public abstract Color makeColor(Vector3D normal, Vector3D hitPoint, Color objectColor, Camera camera);
}