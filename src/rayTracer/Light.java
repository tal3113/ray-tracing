package rayTracer;

import java.awt.*;

/**
 * Created by uriamor on 25/05/2017.
 */
public class Light {
    public Point position;
    public Color lightColor;
    public float specIntensity;
    float shadowIntensity;
    public float lightRadius;

    public Light(Point position, Color lightColor, float specIntensity, float shadowIntensity, float lightRadius) {
        this.position = position;
        this.lightColor = lightColor;
        this.specIntensity = specIntensity;
        this.shadowIntensity = shadowIntensity;
        this.lightRadius = lightRadius;
    }

    @Override
    public String toString() {
        return "Light{" +
                "position=" + position +
                ", lightColor=" + lightColor +
                ", specIntensity=" + specIntensity +
                ", shadowIntensity=" + shadowIntensity +
                ", lightRadius=" + lightRadius +
                '}';
    }
}
