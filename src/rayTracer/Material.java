package rayTracer;

import java.awt.*;

/**
 * Created by uriamor on 26/05/2017.
 */
public class Material {
    public Color diffuse, spec;
    public Vector reflection;
    public float phong, alpha;

    public Material(Color diffuse, Color spec, Vector reflection, float phong, float alpha) {
        this.diffuse = diffuse;
        this.spec = spec;
        this.reflection = reflection;
        this.phong = phong;
        this.alpha = alpha;
    }

    public static Color parseColor(float r, float g, float b){
        Vector v = (new Vector(r,g,b)).boundBy(0,1);
        return new Color((float) v.x, (float)v.y, (float)v.z);
    }

    @Override
    public String toString() {
        return "Material{" +
                "diffuse=" + diffuse +
                ", spec=" + spec +
                ", reflection=" + reflection +
                ", phong=" + phong +
                ", alpha=" + alpha +
                '}';
    }
}
