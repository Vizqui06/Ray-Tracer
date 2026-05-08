import java.awt.Color;

public class LightLambertian extends Light {

    public LightLambertian(Vector3D direction, Color color, double intensity) {
        super(direction, color, intensity);
    }

    @Override
    public Color makeColor(Vector3D normal, Vector3D hitPoint, Color objectColor, Camera camera) {
        // N · L: how much the normal faces the light
        // Negative means light comes from behind -> useless -> no contribution
        double nPointProductL = Math.max(0.0, normal.productPoint(direction));

        // Diffuse = LC * OC * LI * (N · L)
        // Restriction (Clamp): not red, green nor blue shall sum over than the value of integer one (1.0)
        // This brcause the LI (Light Intensity) is maxed to 1 (255 in R,G or B) -> (normalized) 
        // Any value beyond shall be considered as just 1.0 (max brightness) :D

        // The logic from Ray-Tracer is now here, to simplify both classes
        int red = (int) Math.min(255, (color.getRed()   / 255.0) * (objectColor.getRed()   / 255.0) * intensity * nPointProductL * 255);
        int green = (int) Math.min(255, (color.getGreen() / 255.0) * (objectColor.getGreen() / 255.0) * intensity * nPointProductL * 255);
        int blue = (int) Math.min(255, (color.getBlue()  / 255.0) * (objectColor.getBlue()  / 255.0) * intensity * nPointProductL * 255);

        return new Color(red, green, blue);
    }
}