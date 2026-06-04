// This class will be the template for the 3D objects inside the scene
// Object3D only duty is to declare that SOMETHING with a color can be hit

import java.awt.Color; // import Color as 

public abstract class Object3D {
    private final Color color; // For the RGB colors when rendering
    private Texture texture; // null means no texture --> flat color
    private Texture normalMap; // null means no normal map -> smooth/flat shading stays as-is
    private double refLEctivity = 0.0; // 0.0 = no reflection, 1.0 = perfect mirror
    private double refRActivity = 0.0; // // 0.0 = opaque, 1.0 = fully transparent (for V.09)
    private double refractiveIndex = 1.0; // index of refraction: air=1.0, glass=1.5, water=1.33

    // Every object in a 3D space has a center and color (for rendering)
    public Object3D(Color color, double refLEctivity, double refRActivity, double refractiveIndex) {
        this.color = color;
        this.refLEctivity = refLEctivity;
        this.refRActivity = refRActivity;
        this.refractiveIndex = refractiveIndex;
    }

    // Getter and setter of textures and color
    public Texture getTexture() {return texture;}
    public void setTexture(Texture texture) {this.texture = texture;}

    // Getter and setter of the normal map
    public Texture getNormalMap() {return normalMap;}
    public void setNormalMap(Texture normalMap) {this.normalMap = normalMap;}

    // A getter to know what color does the object has
    public Color getColor() {return color;} 

    // Getter and Setters for reflection and refraction
    public double getRefLEctivity() {return refLEctivity;}
    public double getRefRActivity() {return refRActivity;}
    public double getRefractiveIndex() {return refractiveIndex;}

    public void setRefLEctivity(double refLEctivity) {this.refLEctivity = refLEctivity;}
    public void setRefRActivity(double refRActivity) {this.refRActivity = refRActivity;}
    public void setRefractiveIndex(double refractiveIndex) {this.refractiveIndex = refractiveIndex;}

    // An abstract method so every child can be hit properly
    public abstract Intersection intersect (Ray ray); 

    // Helpful method for RayTracer to use it:
    public Color rightColor(double u, double v) {
        // If there is a texture, it will return the color of the texture at the uv coords of the texture image (PNG/JPG)
        if (texture != null) return texture.sample(u, v);
        return getColor(); // If there is NO texture, use default flat color, like always
    }
}