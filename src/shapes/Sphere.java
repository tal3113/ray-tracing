package shapes;

import rayTracer.*;

/**
 * Created by uriamor on 27/05/2017.
 */
public class Sphere extends Shape {

    float rad;
    Point center;

    public Sphere(Material mat, float rad, Point center) {
        super(mat);
        this.rad = rad;
        this.center = center;
    }


    @Override
    public Point[] intersect(Ray ray) {
        Point[] points = new Point[2];
        Vector L = center.toVector().add(ray.origin.toVector().mul(-1));
        double tca =L.dot(ray.direction);
        if(tca<0)
            return null ; //there is no intersection
        double dSquare=L.dot(L)-Math.pow(tca,2);
        double radiusSquare=Math.pow(rad,2);
        if(dSquare>radiusSquare)
            return null; //there is no intersection
        double thc=Math.sqrt(radiusSquare-dSquare);
        double t1 = tca-thc;
        double t2=tca+thc;

        points[0]=ray.origin.toVector().add(ray.direction.mul((float)t1)).toPoint();
        points[1]=ray.origin.toVector().add(ray.direction.mul((float)t2)).toPoint();

        return points;
    }

    @Override
    protected Vector getNormal(Point p) {
        return new Vector(p).add(center.toVector().mul(-1)).normalize();
    }

    @Override
    public String toString() {
        return "Sphere{" +
                "rad=" + rad +
                ", mat=" + mat +
                ", center=" + center +
                '}';
    }
}
