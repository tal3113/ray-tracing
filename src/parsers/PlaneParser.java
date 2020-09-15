package parsers;

import rayTracer.Material;
import rayTracer.Vector;
import shapes.Plane;
import shapes.Shape;

import java.util.List;

/**
 * Created by uriamor on 27/05/2017.
 */
public class PlaneParser implements ShapeParser {
    Vector normal;
    float offset;
    int matIndex;

    public PlaneParser(Vector normal, float offset, int matIndex) {
        this.normal = normal;
        this.offset = offset;
        this.matIndex = matIndex -1;
    }

    @Override
    public Shape parseShape(List<Material> materialsList) {
        try {
            return new Plane(materialsList.get(matIndex), offset, normal);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }

}
