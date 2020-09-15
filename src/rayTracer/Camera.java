package rayTracer;

import rayTracer.*;
import shapes.Plane;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

//import static sun.jvm.hotspot.oops.CellTypeState.bottom;

/**
 * Created by uriamor on 25/05/2017.
 */
public class Camera {
    public Vector position, lookAt, up;
    float distance, width;


    Vector w, u, v;

    public Camera(rayTracer.Point c, rayTracer.Point lookAt, rayTracer.Point up
            , float distance, float width) {
        this.position = new Vector(c);
        this.lookAt = (new Vector(lookAt)).add(this.position.mul(-1));//(new Vector(lookAt)).add(position.mul(-1));

        this.w = this.lookAt.normalize();
        this.up = (new Vector(up));

        this.distance = distance;
        this.width = width;

//        setW(this.position, this.lookAt);
        setU(this.w, this.up);
        setV(this.w, this.u);
    }



    private void setU(Vector w, Vector up) {
        //gram-schmidt
        Vector v = this.w.mul((float)(this.up.dot(this.w)/(this.w.dot(this.w))));
        Vector up_orthogonal=this.up.add(v.mul(-1)).normalize();
        this.u = w.cross(up_orthogonal).normalize();
    }

    private void setV(Vector w, Vector u) {

        this.v = w.cross(u).normalize();
    }

    public Point[][] getGrid(int nWidth, int nHeight) {
        float aspectRatio = (float) nHeight / nWidth;

        float height = this.width * aspectRatio;

        Vector centerPoint = this.position.add(w.mul(distance));



        // find bottom left corner of view plane
        Vector bottomLeft = centerPoint.add(this.v.mul(-0.5f * height)).add(this.u.mul(-0.5f * this.width));

        // divide rectangle into nWidth x nHeight subrectangles
        float vStep = height / nHeight; // (height division)
        float uStep = this.width / nWidth; // (width division)

        // find the center of each subreactangle
        Point[][] pixelArray = new Point[nWidth][nHeight];
        for (int i = 0; i < nWidth; i++) {
            for (int j = 0; j < nHeight; j++) {
                pixelArray[i][j] = bottomLeft.add(this.u.mul((i+0.5f)*uStep)).add(this.v.mul((j+0.5f)*vStep)).toPoint();
            }
        }

        return pixelArray;
    }

    public Vector convertCoords(Vector p) {
        Matrix rT = new Matrix(new double[][]{
                {this.u.x, this.v.x, this.w.x, 0},
                {this.u.y, this.v.y, this.w.y, 0},
                {this.u.z, this.v.z, this.w.z, 0},
                {0, 0, 0, 1}
        });
        Matrix tInv = new Matrix(new double[][]{
                {1, 0, 0, this.position.x},
                {0, 1, 0, this.position.y},
                {0, 0, 1, this.position.z},
                {0, 0, 0, 1}
        });

        Matrix matrix = tInv.times(rT);
        return matrix.times(new Vector(p.x, p.y, p.z));
    }


    @Override
    public String toString() {
        return "Camera{" +
                "c=" + this.position +
                ", lookAt=" + lookAt +
                ", up=" + up +
                ", distance=" + distance +
                ", width=" + width +
                ", w=" + w +
                ", u=" + u +
                ", v=" + v +
                '}';
    }
}
