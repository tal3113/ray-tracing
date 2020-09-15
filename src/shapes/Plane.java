package shapes;

import rayTracer.Material;
import rayTracer.*;
import rayTracer.Point;

import java.awt.*;

/**
 * Created by uriamor on 26/05/2017.
 */
public class Plane extends Shape {

    Vector normal;
    float offset;



    public Plane(Material mat){
        super(mat);
    };
    public Plane(rayTracer.Material mat, float offset, Vector normal) {
        super(mat);
        this.offset = -offset;
        this.normal = normal.normalize();
    }




    @Override
    public Point[] intersect(Ray ray) {
        Point[] points = new Point[1];
        if (ray.direction.dot(this.normal)>=0)
            return null; // there is no intersection

        double t = -(this.normal.dot(ray.origin.toVector())+offset)/(ray.direction.dot(this.normal));
        if(t < 0){
            return null;
        }
        points[0]=ray.origin.toVector().add(ray.direction.mul((float)t)).toPoint();
        return points;
    }

    @Override
    protected Vector getNormal(Point p) {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "mat=" + mat +
                ", normal=" + normal +
                ", offset=" + offset +
                '}';
    }
}
