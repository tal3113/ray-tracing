package parsers;

import rayTracer.Material;
import rayTracer.Point;
import shapes.Shape;
import shapes.Sphere;

import java.util.List;

/**
 * Created by uriamor on 27/05/2017.
 */
public class SphereParser implements ShapeParser {
    Point center;
    float radius;
    int materialIndex;

    public SphereParser(Point center, float radius, int materialIndex) {
        this.center = center;
        this.radius = radius;
        this.materialIndex = materialIndex -1;
    }

    @Override
    public Shape parseShape(List<Material> materialsList) {

        try {
            return new Sphere(materialsList.get(materialIndex), radius, center);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
