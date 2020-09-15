package rayTracer;

/**
 * Created by uriamor on 25/05/2017.
 */
public class Point {
    private static final double EPSILON = Math.pow(10,-5);
    public double x, y, z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector toVector() {
        return new Vector(this);
    }

    public double distance(Point other) {
        double absDiffX = Math.abs(this.x - other.x);
        double absDiffY = Math.abs(this.y - other.y);
        double absDiffZ = Math.abs(this.z - other.z);
        return Math.sqrt(Math.pow(absDiffX, 2) + Math.pow(absDiffY, 2) + Math.pow(absDiffZ, 2));
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }


    public boolean equals(Point other) {
        return this.distance(other)<EPSILON;
    }
}
