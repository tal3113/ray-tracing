package rayTracer;

import java.awt.*;

/**
 * Created by uriamor on 27/05/2017.
 */
public class Scene {
    Color background;
    public int shadowRays, maxRec, ssLevel; // we know...

    public Scene(Color background, int shadowRays, int maxRec, int ssLevel) {
        this.background = background;
        this.shadowRays = shadowRays;
        this.maxRec = maxRec;
        this.ssLevel = ssLevel;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "background=" + background +
                ", shadowRays=" + shadowRays +
                ", maxRec=" + maxRec +
                ", ssLevel=" + ssLevel +
                '}';
    }
}
