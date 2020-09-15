package shapes;

import rayTracer.*;

/**
 * Created by uriamor on 27/05/2017.
 */
public class Triangle extends Plane {
    Point pointA;
    Point pointB;
    Vector normal;
    Point pointC;
    float offset;



    public Triangle(Material material, Point pointA, Point pointB, Point pointC) {
        super(material);
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        setNormal();
        super.normal = this.normal;
        this.offset = -(float) (this.normal.dot(pointA.toVector()));
        super.offset = this.offset;

    }

    private void setNormal() {
        Vector A = this.pointA.toVector().add(this.pointB.toVector().mul(-1));
        Vector B = this.pointC.toVector().add(this.pointB.toVector().mul(-1));
        this.normal = A.cross(B).normalize();
    }


    @Override
    public Point[] intersect(Ray ray) {
        Point[] retPoints = new Point[1];
        retPoints[0] = null;
        Point[] points = super.intersect(ray);
        if(points == null){
            return null;
        }
        for(Point P :points) {
            Vector V1 = pointA.toVector().add(ray.origin.toVector().mul(-1));
            Vector V2 = pointB.toVector().add(ray.origin.toVector().mul(-1));
            Vector N1 = V1.cross(V2).normalize();

            if(ray.direction.dot(N1) < 0){
                continue;
            }

//             V1 = pointA.toVector().add(ray.origin.toVector().mul(-1));
             V2 = pointC.toVector().add(ray.origin.toVector().mul(-1));
             N1 = V1.cross(V2).normalize();

            if(ray.direction.mul(-1).dot(N1) < 0){
                continue;
            }

            V1 = pointB.toVector().add(ray.origin.toVector().mul(-1));
//            V2 = pointC.toVector().add(ray.origin.toVector().mul(-1));
            N1 = V1.cross(V2).normalize();

            if(ray.direction.dot(N1) < 0){
                continue;
            }
            retPoints[0] = P;
        }
        if(retPoints[0] != null){
            return retPoints;
        }
        return null;
    }

    @Override
    protected Vector getNormal(Point p) {
        return normal;
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "pointA=" + pointA +
                ", pointB=" + pointB +
                ", normal=" + normal +
                ", pointC=" + pointC +
                ", mat=" + mat +
                '}';
    }
}
