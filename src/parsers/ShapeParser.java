package parsers;

import rayTracer.Material;
import shapes.Shape;

import java.util.List;

/**
 * Created by uriamor on 27/05/2017.
 */
public interface ShapeParser {
    Shape parseShape(List<Material> materialsList);
}
