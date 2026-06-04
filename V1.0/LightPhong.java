import java.awt.Color;

public class LightPhong extends Light {

    // Visual exponent which depends on the material: 8=matte, 32=plastic, 128=metal, 256+=mirror-like
    private final double shininess; 

    public LightPhong(Vector3D direction, Color color, double intensity, double shininess) {
        super(direction, color, intensity); 
        this.shininess = shininess; 
    }

    // Getter
    public double getShininess() {return shininess;}

    @Override
    public Color makeColor(Vector3D normal, Vector3D hitPoint, Color objectColor, Camera camera) {

        // DIFFUSE (Lambertian)
        // Calculates how much the surface normal faces the light source.
        // Math.max(0.0, ...) ensures that if the light hits from behind the surface (negative value),
        // it contributes 0.0 to the illumination (no diffuse light from behind).
        double nPointProductL = Math.max(0.0, normal.productPoint(direction));
        
        // Base diffuse color channels calculation based on light intensity, object color, and incidence angle (how the ray hit).
        double diffusedRed = (color.getRed() / 255.0) * (objectColor.getRed() / 255.0) * intensity * nPointProductL;
        double diffusedGreen = (color.getGreen() / 255.0) * (objectColor.getGreen() / 255.0) * intensity * nPointProductL;
        double diffusedBlue = (color.getBlue() / 255.0) * (objectColor.getBlue() / 255.0) * intensity * nPointProductL;


        // SPECULAR COMPONENT (Blinn-Phong Optimization):
        // Unlike standard Phong which reflects the light vector, Blinn-Phong calculates a "Halfway" vector.
        // Is computationally cheaper and returns a more realistic highlights, especially at sharp angles.

        // Vector V: Direction from the hit point toward the camera.
        Vector3D V = camera.getPositionWorld().vectorSubstraction(hitPoint).normalization();

        // Vector H (Halfway Vector): The bisector between the light direction and the view direction.
        // It represents the direction the normal would need to face to perfectly reflect light into the camera.
        Vector3D H = direction.vectorAddition(V).normalization();

        // Specular Intensity: The dot product of the Normal (N) and the Halfway vector (H).
        // The closer the surface normal is to the halfway vector, the brighter the highlight.
        double nPointProductH = Math.max(0.0, normal.productPoint(H));
        
        // Elevate the result to the power of 'shininess' to concentrate the highlight.
        double specular = Math.pow(nPointProductH, shininess) * intensity;

        // Apply the calculated specular intensity strictly to the LIGHT's color, not the object's.
        // Highlights reflect the light source's color.
        double specularRed = (color.getRed() / 255.0) * specular;
        double specularGreen = (color.getGreen() / 255.0) * specular;
        double specularBlue = (color.getBlue() / 255.0) * specular;


        // FINAL COLOR BLENDING:
        // Add diffuse and specular components together.
        // Math.min(255, ...) acts as a clamp. If the combination of diffuse and specular exceeds 1.0 (255),
        // it caps the value at maximum brightness, preventing color overflow distortion.
        int red = (int) Math.min(255, (diffusedRed + specularRed) * 255);
        int green = (int) Math.min(255, (diffusedGreen + specularGreen) * 255);
        int blue = (int) Math.min(255, (diffusedBlue + specularBlue) * 255);

        return new Color(red, green, blue);
    }

    @Override
    public Vector3D getDirectionToLight(Vector3D hitPoint) {
        // Directional light: direction is constant, independent of the hit point.
        // The stored direction points FROM the light TOWARD the scene, so it is negated 
        // to point FROM the surface TOWARD the light source.
        return direction.scalarIt(-1.0);
    }

    @Override
    public double getDistanceToLight(Vector3D hitPoint) {
        // Directional lights are infinitely far away, meaning shadow rays should never 
        // stop checking for occlusions until they exit the scene geometry entirely.
        return Double.MAX_VALUE;
    }
}