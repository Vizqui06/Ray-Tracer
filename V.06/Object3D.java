// This class will be the template for the 3D objects inside the scene
// Object3D only duty is to declare that SOMETHING with a color can be hit

import java.awt.Color; // import Color as 

public abstract class Object3D {
    private final Color color; // For the RGB colors when rendering

    public Object3D(Color color){ // Every object in a 3D space has a center and color (for rendering)
        this.color = color;
    }

    public Color getColor() {return color;} // a getter to know what color does the object has

    public abstract Intersection intersect (Ray ray); // an abstract method so every child can be hit properly
    // that was weird to write
}

/*
Constructor discarded due to too many attributes
It is easier with the Color import
public Object3D(double object_pos_x, double object_pos_y, double object_pos_z) {
        this.object_pos_x = object_pos_x; // position of the object in X
        this.object_pos_y = object_pos_y; // position of the object in Y
        this.object_pos_z = object_pos_z; // position of the object in Z
    }
*/