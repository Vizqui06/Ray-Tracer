import java.awt.Color;

public class LightPhong extends Light { // LightPhong is a type of Light

    private final double shininess; // visual exponent hich depends on the material: 8=matte, 32=plastic, 128=metal, 256+=mirror-like

    public LightPhong(Vector3D direction, Color color, double intensity, double shininess) {
        super(direction, color, intensity); // Same as Lambertian
        this.shininess = shininess; // But adds a shininess factor (shininess is not the same as shiness)
    }

    // Getter
    public double getShininess() {return shininess;}

    @Override
    public Color makeColor(Vector3D normal, Vector3D hitPoint, Color objectColor, Camera camera) {

        // Diffuse (same as Lambertian)
        double nPointProductL = Math.max(0.0, normal.productPoint(direction));
        // It is not needed the CLAMP here, it will be regulated later
        double diffusedRed = (color.getRed() / 255.0) * (objectColor.getRed() / 255.0) * intensity * nPointProductL;
        double diffusedGreen = (color.getGreen() / 255.0) * (objectColor.getGreen() / 255.0) * intensity * nPointProductL;
        double diffusedBlue = (color.getBlue() / 255.0) * (objectColor.getBlue() / 255.0) * intensity * nPointProductL;


        // Visual/Specular exponent
        // After searching in documentation and Reddit:

        // Reflection = 2(N · L)N - L ->  reflection of the light direction around the normal
        Vector3D R = normal.scalarIt(2.0 * nPointProductL).vectorSubstraction(direction).normalization();

        // V = direction from hit point toward the camera (normalized)
        Vector3D V = camera.getPositionWorld().vectorSubstraction(hitPoint).normalization();

        // (R · V)^shininess, clamped to 0 so back-facing specular is ignored
        double rPointProductV = Math.max(0.0, R.productPoint(V));
        double specular = Math.pow(rPointProductV, shininess) * intensity;

        // Specular uses light color only (highlight is the color of the light, not the object)
        double specularRed = (color.getRed()   / 255.0) * specular;
        double specularGreen = (color.getGreen() / 255.0) * specular;
        double specularBlue = (color.getBlue()  / 255.0) * specular;

        // Now, combine and clamp to [0, 255]
        int red = (int) Math.min(255, (diffusedRed + specularRed) * 255);
        int green = (int) Math.min(255, (diffusedGreen + specularGreen) * 255);
        int blue = (int) Math.min(255, (diffusedBlue + specularBlue) * 255);

        return new Color(red, green, blue);
    }
}