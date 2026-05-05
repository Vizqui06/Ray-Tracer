import java.awt.Color;

public class Light {

    // A directional light has no position in the world
    // This type of light only has a fixed direction from which the light (such as the sun) "comes"
    // By teacher's suggestion: (0.0, 0.0, 1.0) means that it comes from +Z to -Z (from user to the far far away)
    private final Vector3D direction; // Direction from where the light is coming (it will be normalized in the constructor to be easy to work with)
    private final Color color; // LC: Light color
    private final double intensity; // LI: light intensity (0.0 = total darkness, 1.0 = maximum brightness of color)

    public Light(Vector3D direction, Color color, double intensity) {
        this.direction = direction.normalization(); // normalized from the beggining due to the dot products
        this.color = color;
        this.intensity = intensity;
    }

    // Getters
    public Vector3D getDirection() {return direction;}
    public Color getColor() {return color;}
    public double getIntensity() {return intensity;}
}