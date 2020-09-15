package rayTracer;

public class Vector {
    public double x, y, z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Point p) {
        this(p.x, p.y, p.z);
    }

    private double getSquaredNorm() {
        return this.dot(this);
    }

    public Vector normalize() {
        if (0 == this.getSquaredNorm()) {
            return this.times(0);
        }
        return this.times(Math.sqrt(1 / this.getSquaredNorm()));

    }
    public Vector pointToPointProduct(Vector v){
        return new Vector(this.x*v.x,this.y*v.y,this.z*v.z);
    }

    private Vector times(double f) {
        double new_x = this.x * f;
        double new_y = this.y * f;
        double new_z = this.z * f;
        return new Vector(new Point(new_x, new_y, new_z));
    }

    public Vector cross(Vector v) {
        return (new Vector(((y * v.z) - (z * v.y)),
                ((z * v.x) - (x * v.z)),
                ((x * v.y) - (y * v.x)))).mul(-1);
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x
                , this.y + other.y
                , this.z + other.z);
    }

    public Vector mul(float scalar) {
        return new Vector(scalar * this.x
                , scalar * this.y
                , scalar * this.z);
    }

    public Point toPoint(){
        return new Point(this.x,this.y,this.z);
    }

    private double boundValue(double val, double minVal, double maxVal){
        if(val < minVal){
            val=minVal;
        }
        if(val > maxVal){
            val=maxVal;
        }
        return val;
    }

    public Vector boundBy(double minVal, double maxVal){
        double newX = boundValue(this.x, minVal, maxVal);
        double newY = boundValue(this.y, minVal, maxVal);
        double newZ = boundValue(this.z, minVal, maxVal);
        return new Vector(newX, newY,newZ);
    }


    public double dot(Vector v) {
        return (x * v.x) + (y * v.y) + (z * v.z);
    }

    public Vector findPerpVector(Point p){
        double gamma = -this.dot(p.toVector());
        double newZ = 1, newX=1,newY=1;

        // arbitrary assignment
        if(this.x!=0){
            newX  = (this.y+this.z + gamma)/this.x;
        }
        else if(this.y!=0){
            newY  = (this.x+this.z + gamma)/this.y;
        }
        else if(this.z!=0){
            newZ  = (this.x+this.y + gamma)/this.z;
        }
        return new Vector(newX,newY,newZ).normalize();
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
