package parsers;

import rayTracer.Material;
import rayTracer.Point;
import shapes.Shape;
import shapes.Triangle;

import java.util.List;

/**
 * Created by uriamor on 27/05/2017.
 */
public class TriangleParser implements ShapeParser {
    Point pointA;
    Point pointB;
    Point pointC;
    int matIndex;

    public TriangleParser(Point pointA, Point pointB, Point pointC, int matIndex) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        this.matIndex = matIndex -1;
    }

    @Override
    public Shape parseShape(List<Material> materialsList) {
        try {
            return new Triangle(materialsList.get(matIndex), pointA, pointB, pointC);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
