package rayTracer;

import shapes.Plane;
import shapes.Shape;
import shapes.Sphere;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Created by uriamor on 25/05/2017.
 */
public class Ray {
    private static final float EPSILON = (float) Math.pow(10,-1);

    public Point origin;
    public Vector direction;


    public HashMap<Point, Shape> hitMap = new HashMap<>();

    public Ray(Point origin, Point dest) {
        this.origin = origin;
        this.direction = dest.toVector().add(origin.toVector().mul(-1)).normalize();
    }

    public Ray(Point origin, Vector direction) {
        this.direction = direction.normalize();
        this.origin = origin;
    }

    public Point getEnd(double t) {
        return null;
    }

    public void setIntersections(List<Shape> shapes) {

        this.hitMap = new HashMap<>();
        for (Shape s : shapes) {
            Point[] rayHit = s.intersect(this);
            if (rayHit != null) {
                for (Point p : rayHit) this.hitMap.put(p, s);
            }
        }
    }

    public Point[][] getSSPoints(double radius, int N) {
        Random rand = new Random();
        Vector u = this.direction.findPerpVector(this.origin);
        Vector v = u.cross(this.direction).normalize();
        Vector bottomLeft = this.origin.toVector().add(v.mul((float) -(0.5 * radius))).add(u.mul((float) -(0.5f * radius)));
        float step = (float) radius / N; // (height division)
        Point[][] pixelArray = new Point[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                pixelArray[i][j] = bottomLeft
                        .add(u.mul((i + Math.min(rand.nextFloat() + EPSILON, 1 - EPSILON)) * step))
                        .add(v.mul((j + Math.min(rand.nextFloat() + EPSILON, 1 - EPSILON)) * step)).toPoint();
            }
        }

        return pixelArray;
    }


}
