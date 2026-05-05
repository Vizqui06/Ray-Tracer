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
        return new Vector3D(this.y*other_vector.z - this.z*other_vector.y, // pos in x
                            this.z*other_vector.x - this.x*other_vector.z, // pos in y
                            this.x*other_vector.y - this.y*other_vector.x  // pos in z
        );}

    public double magnitude(){
        double magni = Math.sqrt(x*x + y*y + z*z);
        if(magni == 0){
            throw new ArithmeticException("The magnitud cannot be zero."); // Mathematically, shouldn't be zero
        }
        return magni;
        // return Math.sqrt(productPoint(this));
    }

    public Vector3D normalization(){
        return new Vector3D(x/magnitude(), y/magnitude(), z/magnitude()); // divides the vector by its magnitude to normalize it (=1)
    }

    public void rotateY(double commonAngle){
        double radians = Math.toRadians(commonAngle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        double rotatedX = x*cos + z*sin;
        double rotatedY = y;
        double rotatedZ = -x*sin + z*cos;
        this.x = rotatedX;
        this.y = rotatedY;
        this.z = rotatedZ;
    }
}

/*
Sumar 2 vectores:
public Vector3D vectorAddition
this.x + other_vectorX // suma las posiciones del vector actual con el otro en la posición X
this.y + other_vectorY // suma las posiciones del vector actual con el otro en la posición Y
this.z + other_vectorZ // suma las posiciones del vector actual con el otro en la posición Z

public Vector vectorSubstract
this.x - other_vectorX // resta las posiciones del vector actual con el otro en la posición X
this.y - other_vectorY // resta las posiciones del vector actual con el otro en la posición Y
this.z - other_vectorZ // resta las posiciones del vector actual con el otro en la posición Z

public Vector saclarIt
// Un escalar multiplica cada posición del vector por el mismo escalar
this.x * sclalar 
this.y * sclalar
this.z * sclalar

public double productPoint
this.x + other_vectorX // suma las posiciones del vector actual con el otro en la posición X
this.y + other_vectorY // suma las posiciones del vector actual con el otro en la posición Y
this.z + other_vectorZ // suma las posiciones del vector actual con el otro en la posición Z

public Vector3D product cross
A x B = ( AyBz - AzBy, AzBx - AxBz, AxBy - AyBx )
crossResultX = this.y*other_vectorZ - this.z*other_vectorY
crossResultY = this.z*other_vectorX - this.x*other_vectorZ
crossResultZ = this.x*other_vectorY - this.y*other_vectorX

Magnitud
Normalización

*/ 
