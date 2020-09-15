package shapes;


import rayTracer.*;

import rayTracer.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import rayTracer.Light;

/**
 * Created by uriamor on 25/05/2017.
 */
public abstract class Shape {
    public Material mat;

    public Shape(Material mat) {
        this.mat = mat;
    }


    public abstract Point[] intersect(Ray ray);

    private Vector singleLightSource(Point p, Light light, Ray viewDirection, List<Shape> shapes, int shadowRaysSqrt) {
//        if (this.getClass().equals(Plane.class) && !this.getClass().equals(Triangle.class)) {
//            System.out.println("Shiiiit");
//        }
        Ray lightRay = new Ray(light.position, p);
        lightRay.setIntersections(shapes);
        if (lightRay.hitMap.isEmpty()) return new Vector(0, 0, 0);
        List<Point> keyPoints = new ArrayList<Point>(lightRay.hitMap.keySet());
        keyPoints.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return (o1.distance(light.position) - o2.distance(light.position) < 0) ? -1 : 1;
            }
        });
        Shape cur=lightRay.hitMap.get(keyPoints.get(0));
        Vector N = this.getNormal(p);
        if (!p.equals(keyPoints.get(0))) {
            double sRed, sGreen, sBlue;
            // Compute plane orthogonal to leghtRay
            // put a rectangle centered at light source with edge length of given value on the plane
            // divide the rectangle to a grid N x N
            // for cell in grid: sum += singleLightSource(Point p, Light light, Ray viewDirection, List<Shape> shapes)
            // sum = sum / N*N
            Vector ssSum = new Vector(0, 0, 0);
            Point[][] ssGrid = lightRay.getSSPoints(light.lightRadius, shadowRaysSqrt);
            for (int x = 0; x < shadowRaysSqrt; x++) {
                for (int y = 0; y < shadowRaysSqrt; y++) {
                    Point ssLightSource = ssGrid[x][y];
                    Ray shadowRay = new Ray(ssLightSource, p);

                    shadowRay.setIntersections(shapes);
                    if (shadowRay.hitMap.isEmpty()) continue;
                    List<Point> ssKeyPoints = new ArrayList<Point>(shadowRay.hitMap.keySet());
                    ssKeyPoints.sort(new Comparator<Point>() {
                        @Override
                        public int compare(Point o1, Point o2) {
                            return (o1.distance(ssLightSource) - o2.distance(ssLightSource) < 0) ? -1 : 1;
                        }
                    });

                    if (!p.equals(ssKeyPoints.get(0))) continue;
                    cur=shadowRay.hitMap.get(ssKeyPoints.get(0));

                    N = (N.dot(shadowRay.direction) <= 1 && N.dot(shadowRay.direction) >= 0) ? N : N.mul(-1);

                    sRed = doPhongModel(light, "r", shadowRay.direction, N, viewDirection,cur);
                    sGreen = doPhongModel(light, "g", shadowRay.direction, N, viewDirection,cur);
                    sBlue = doPhongModel(light, "b", shadowRay.direction, N, viewDirection,cur);
                    ssSum = ssSum.add((new Vector(sRed, sGreen, sBlue)).mul((float) Math.pow(shadowRaysSqrt, -2)));
                }
            }
            return ssSum;
        }


        N = (N.dot(lightRay.direction) <= 1 && N.dot(lightRay.direction) >= 0) ? N : N.mul(-1);

        double red, green, blue;
        red = doPhongModel(light, "r", lightRay.direction, N, viewDirection,cur);
        green = doPhongModel(light, "g", lightRay.direction, N, viewDirection,cur);
        blue = doPhongModel(light, "b", lightRay.direction, N, viewDirection,cur);

        return new Vector(red, green, blue);

    }

    protected abstract Vector getNormal(Point p);

    private double doPhongModel(Light light, String color, Vector L, Vector N, Ray viewDirection,Shape cur) {
        float Idiff, Ispec, Il;
        switch (color) {
            case "r":
                Idiff = cur.mat.diffuse.getRed() / 255f;
                Ispec = cur.mat.spec.getRed() / 255f;
                Il = light.lightColor.getRed() / 255f;
                break;
            case "b":
                Idiff = cur.mat.diffuse.getBlue() / 255f;
                Ispec = cur.mat.spec.getBlue() / 255f;
                Il = light.lightColor.getBlue() / 255f;
                break;
            case "g":
                Idiff = cur.mat.diffuse.getGreen() / 255f;
                Ispec = cur.mat.spec.getGreen() / 255f;
                Il = light.lightColor.getGreen() / 255f;
                break;
            default:
                return -1;

        }


        Vector R = L.add(N.mul(-(float) (2 * N.dot(L))));
        double d=Idiff * N.dot(L) * Il + Ispec * light.specIntensity * Math.pow((double) viewDirection.direction.dot(R), (double) cur.mat.phong) * Il;

        return d;
    }

    ;

    private List<Point> sortIntersectedPoints(Ray ray, List<Shape> shapes) {

        //ray.setIntersections(shapes);
        List<Point> keyPoints = new ArrayList<Point>(ray.hitMap.keySet());

        keyPoints.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return (o1.distance(ray.origin) - o2.distance(ray.origin) < 0) ? -1 : 1;
            }
        });
        return keyPoints;
    }

    public Vector getColor(Point p, List<Light> lightList, List<Shape> shapes, Ray viewDirection
            , int recLevel, int maxRec, Vector backgroundColor, int shadowRaysSqrt) {

        if (recLevel >= maxRec) {
            return backgroundColor;
        }


        Vector sum = new Vector(0, 0, 0);
        Shape current_shape =viewDirection.hitMap.get(p);
        viewDirection.hitMap.remove(p);

        List<Point> keyPoints = sortIntersectedPoints(viewDirection, shapes);


        for (Light light : lightList) {
            sum = sum.add(singleLightSource(p, light, viewDirection, shapes, shadowRaysSqrt));
        }

        sum = sum.mul(1 - current_shape.mat.alpha); //(diffuse+specular)*(1-transparency)
        //transparency
        if(current_shape.mat.alpha>0) {
            if (viewDirection.hitMap.isEmpty()) {
                sum = sum.add(backgroundColor.mul(current_shape.mat.alpha));
            } else {
                sum = sum.add(getColor(keyPoints.get(0), lightList, shapes, viewDirection
                        , recLevel + 1, maxRec, backgroundColor, shadowRaysSqrt).mul(current_shape.mat.alpha)); //sum+background*transparency
            }
        }

        //reflection
        Vector N = this.getNormal(p);
//        N = (N.dot(viewDirection.direction) <= 1 && N.dot(viewDirection.direction) >= 0) ? N : N.mul(-1);
        Vector reflectVector = viewDirection.direction.add(N.mul(-(float) (2 * N.dot(viewDirection.direction))));
        Ray reflectRay = new Ray(p, reflectVector);
        reflectRay.setIntersections(shapes);
        if (!reflectRay.hitMap.isEmpty()) {
            keyPoints = sortIntersectedPoints(reflectRay, shapes);
            //if (keyPoints.isEmpty()) return sum.boundBy(0, 1);
            sum = sum.add(getColor(keyPoints.get(0), lightList, shapes, reflectRay
                    , recLevel + 1, maxRec, backgroundColor, shadowRaysSqrt)
                    .pointToPointProduct(current_shape.mat.reflection)); //sum + reflection
        }

        else
        {
            if(this.mat.reflection.x>0.89 && this.mat.reflection.y>0.89 && this.mat.reflection.y>0.89)
                sum=sum.add(backgroundColor);
        }


//        if(sum.boundBy(0,1).toPoint().equals(new Point(0f,0f,0f))){
//            System.out.println("shit");
//        }
        return sum.boundBy(0, 1);
    }

    public boolean contains(Point p) {
        return false;
    }


}
