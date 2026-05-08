import java.awt.Color;

public class LightPoint extends Light{
    private final Vector3D centerPointLight; // Point in the space where the point light is placed
    
    
    public LightPoint(Vector3D direction, Color color, double intensity, Vector3D centerPointLight) {
        super(direction, color, intensity); // Same parameters as Lambertian and Phong
        this.centerPointLight = centerPointLight; // Plus its diferentiation
    }

    public Vector3D getCenterPointLight() {return centerPointLight;}

    @Override
    public Color makeColor(Vector3D normal, Vector3D hitPoint, Color objectColor, Camera camera) {
        // Vector from hit point to the light position
        Vector3D directionPointLight = centerPointLight.vectorSubstraction(hitPoint);
        
        // Distance BEFORE normalizing (used for attenuation)
        double distanceLight = directionPointLight.magnitude();

        // Normalize to get direction
        Vector3D L = directionPointLight.normalization();

        // N · L: how much the normal faces the light
        double nProductPointL = Math.max(0.0, normal.productPoint(L));

        // Attenuation: light fades with the square of the distance (physically accurate)
        // 1 / (1 + k * d^2) -> where k controls how fast it fades; k=0 means no attenuation
        double factorAtenuation = 0.14; // Can be modified
        double attenuation = 1.0 / (1.0 + factorAtenuation * Math.pow(distanceLight, 2));

        // Diffuse = LC * OC * LI * (N · L) * attenuation
        int red = (int) Math.min(255, (color.getRed() / 255.0) * (objectColor.getRed() / 255.0) * intensity * nProductPointL * attenuation * 255);
        int green = (int) Math.min(255, (color.getGreen() / 255.0) * (objectColor.getGreen() / 255.0) * intensity * nProductPointL * attenuation * 255);
        int blue = (int) Math.min(255, (color.getBlue() / 255.0) * (objectColor.getBlue() / 255.0) * intensity * nProductPointL * attenuation * 255);

        return new Color(red, green, blue);
    }
}
