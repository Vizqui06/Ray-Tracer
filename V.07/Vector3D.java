// This class sets the components of any vector in the Ray Tracer

public class Vector3D{

    private double x, y, z;

    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y; 
        this.z = z;
    }

    // Getters
    public double getX() {return x;}
    public double getY() {return y;}
    public double getZ() {return z;}

    // Setters
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setZ(double z) {this.z = z;}


    public Vector3D vectorAddition(Vector3D other_vector){
        return new Vector3D(this.x + other_vector.x, this.y + other_vector.y, this.z + other_vector.z); // sums 2 vectors
    }

    public Vector3D vectorSubstraction(Vector3D other_vector){
        return new Vector3D(this.x - other_vector.x, this.y - other_vector.y, this.z - other_vector.z); // substract 2 vectors
    }

    public Vector3D scalarIt(double scalar){
        return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar); // multiplies the vector times a scale
    }

    public double productPoint(Vector3D other_vector){
        return (this.x * other_vector.x) + (this.y * other_vector.y) + (this.z * other_vector.z); // returns a scalar, not a vector
    }

    public Vector3D productCross(Vector3D other_vector){ // The product cross generates a perpendicular vector of the two vectors
        return new Vector3D(
            this.y * other_vector.z - this.z * other_vector.y, // pos in x
            this.z * other_vector.x - this.x * other_vector.z, // pos in y
            this.x * other_vector.y - this.y * other_vector.x  // pos in z
        );}

    public double magnitude(){
        double magni = Math.sqrt(x*x + y*y + z*z);
        if(magni == 0){
            throw new ArithmeticException("The magnitud cannot be zero."); // Mathematically, shouldn't be zero
        }
        return magni;
        // return Math.sqrt(productPoint(this));
    }

    // Earlier normalization method was unoptimized. It called magnitude() 3 times and each one of the calls made a Math.sqrt
    // With too many rays it will delay the render
    // Before: return new Vector3D(x/magnitude(), y/magnitude(), z/magnitude());
    // After:
    public Vector3D normalization(){
        double mag = magnitude(); // Instead of calling 3 times magnitude(), a var takes its place to simplify the process
        return new Vector3D(x/mag, y/mag, z/mag); // divides the vector by its magnitude to normalize it (=1)
    }

    public Vector3D rotateX(double commonAngle){
        double radians = Math.toRadians(commonAngle);
        double rotatedX = this.x;
        double rotatedY = this.y * Math.cos(radians) + -1 *this.z * Math.sin(radians);
        double rotatedZ = this.y * Math.sin(radians) + this.z * Math.cos(radians);
        return new Vector3D(rotatedX, rotatedY, rotatedZ);
    }

    public Vector3D rotateY(double commonAngle){
        double radians = Math.toRadians(commonAngle);
        double rotatedX = this.x * Math.cos(radians) + this.z * Math.sin(radians);
        double rotatedY = this.y;
        double rotatedZ = -1 * this.x * Math.sin(radians) + this.z * Math.cos(radians);
        // this.x = rotatedX; this.y = rotatedY; this.z = rotatedZ;
        return new Vector3D(rotatedX, rotatedY, rotatedZ);
    }

    // BoxesTreeNode auxiliary method
    // Returns the component of the vector along the axis: 0=X, 1=Y, 2=Z
    // Useful for generic code that operates on a variable axis
    public double getComponent(int axis) {
        if (axis == 0) return x;
        if (axis == 1) return y;
        return z; // If its not X axis nor Y axis, it is for discard Z axis
    }
}