// This class will be the template for the 3D objects inside the scene
// Object3D only duty is to declare that SOMETHING with a color can be hit

import java.awt.Color; // import Color as 

public abstract class Object3D {
    private final Color color; // For the RGB colors when rendering
    private Texture texture; // null means no texture --> flat color

    // Every object in a 3D space has a center and color (for rendering)
    public Object3D(Color color){ this.color = color;}

    // Getter and setter of textures and color
    public Texture getTexture() {return texture;}
    public void setTexture(Texture texture){this.texture = texture;}

    // A getter to know what color does the object has
    public Color getColor() {return color;} 

    public abstract Intersection intersect (Ray ray); // an abstract method so every child can be hit properly
    

    // Helpful method for RayTracer to use it:
    public Color rightColor(double u, double v) {
        // If there is a texture, it will return the color of the texture at the uv coords of the texture image (PNG/JPG)
        if (texture != null) return texture.sample(u, v);
        return getColor(); // If there is NO texture, use default flat color, like always
    }
}