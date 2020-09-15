package rayTracer;

import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

//import com.sun.tools.classfile.Opcode;
import parsers.PlaneParser;
import parsers.ShapeParser;
import parsers.SphereParser;
import parsers.TriangleParser;
import shapes.Shape;

import javax.imageio.ImageIO;

/**
 * Main class for ray tracing exercise.
 */
public class RayTracer {

    public int imageWidth;
    public int imageHeight;
    static Camera cam = null;
    static Scene scene = null;
    static List<Light> lights = new ArrayList<>();
    static List<Shape> shapes = new ArrayList<>();
    static List<Material> materials = new ArrayList<>();

    static void printParsedData() {
        System.out.println(cam);
        System.out.println(scene);
        for (Light l : lights
                ) {
            System.out.println(l);
        }
        for (Shape s : shapes
                ) {
            System.out.println(s);
        }
        for (Material m : materials
                ) {
            System.out.println(m);
        }
    }

    /**
     * Runs the ray tracer. Takes scene file, output image file and image size as input.
     */
    public static void main(String[] args) {

        try {

            RayTracer tracer = new RayTracer();

            // Default values:
            tracer.imageWidth = 500;
            tracer.imageHeight = 500;

            if (args.length < 2)
                throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

            String sceneFileName = args[0];
            String outputFileName = args[1];

            if (args.length > 3) {
                tracer.imageWidth = Integer.parseInt(args[2]);
                tracer.imageHeight = Integer.parseInt(args[3]);
            }


            // Parse scene file:
            tracer.parseScene(sceneFileName);
            // Render scene:
            tracer.renderScene(outputFileName);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (RayTracerException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    /**
     * Parses the scene file and creates the scene. Change this function so it generates the required objects.
     */
    public void parseScene(String sceneFileName) throws IOException, RayTracerException {
        List<ShapeParser> parsers = new ArrayList<>();

        FileReader fr = new FileReader(sceneFileName);

        BufferedReader r = new BufferedReader(fr);
        String line = null;
        int lineNum = 0;
        System.out.println("Started parsing scene file " + sceneFileName);


        while ((line = r.readLine()) != null) {
            line = line.trim();
            ++lineNum;

            if (line.isEmpty() || (line.charAt(0) == '#')) {  // This line in the scene file is a comment
                continue;
            } else {
                String code = line.substring(0, 3).toLowerCase();
                // Split according to white space characters:
                String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

                switch (code) {
                    case "cam":
                        if (cam != null) {
                            System.out.println(String.format("cant have more than one camera (line %d)", lineNum));
                        }
                        try {
                            Point position = new Point(Double.parseDouble(params[0])
                                    , Double.parseDouble(params[1])
                                    , Double.parseDouble(params[2]));

                            Point lookAt = new Point(Double.parseDouble(params[3])
                                    , Double.parseDouble(params[4])
                                    , Double.parseDouble(params[5]));

                            Point up = new Point(Double.parseDouble(params[6])
                                    , Double.parseDouble(params[7])
                                    , Double.parseDouble(params[8]));

                            float distance = Float.parseFloat(params[9]);
                            float width = Float.parseFloat(params[10]);
                            cam = new Camera(position, lookAt, up, distance, width);
                            System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }


                        break;
                    case "set":
                        if (scene != null) {
                            System.out.println(String.format("cant have more than one scene (line %d)", lineNum));
                        }

                        try {
                            Color background = new Color(Float.parseFloat(params[0])
                                    , Float.parseFloat(params[1])
                                    , Float.parseFloat(params[2]));

                            int shadowRayRoot = Integer.parseInt(params[3]);

                            int recLevel = Integer.parseInt(params[4]);

                            int ssLevel = Integer.parseInt(params[5]); // we know...
                            scene = new Scene(background, shadowRayRoot, recLevel, ssLevel);
                            System.out.println(String.format("Parsed general settings (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }


                        break;
                    case "mtl":
                        // Add code here to parse material parameters

                        try {
                            Color diffuse = Material.parseColor(Float.parseFloat(params[0])
                                    , Float.parseFloat(params[1])
                                    , Float.parseFloat(params[2]));

                            Color specular = Material.parseColor(Float.parseFloat(params[3])
                                    , Float.parseFloat(params[4])
                                    , Float.parseFloat(params[5]));

                            Vector reflection = new Vector(Double.parseDouble(params[6])
                                    , Double.parseDouble(params[7])
                                    , Double.parseDouble(params[8]));

                            float phong = Float.parseFloat(params[9]);

                            float transparency = Float.parseFloat(params[10]);
                            materials.add(new Material(diffuse, specular, reflection, phong, transparency));
                            System.out.println(String.format("Parsed material (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }
                        break;
                    case "sph": {
                        try {
                            Point center = new Point(Double.parseDouble(params[0])
                                    , Double.parseDouble(params[1])
                                    , Double.parseDouble(params[2]));

                            float radius = Float.parseFloat(params[3]);
                            int matIndex = Integer.parseInt(params[4]);
                            parsers.add(new SphereParser(center, radius, matIndex));

                            System.out.println(String.format("Parsed sphere (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "pln": {
                        try {
                            Vector normal = new Vector(Double.parseDouble(params[0])
                                    , Double.parseDouble(params[1])
                                    , Double.parseDouble(params[2]));
                            float offset = Float.parseFloat(params[3]);
                            int matIndex = Integer.parseInt(params[4]);

                            parsers.add(new PlaneParser(normal, offset, matIndex));

                            System.out.println(String.format("Parsed plane (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "trg": {

                        try {
                            Point pointA = new Point(Double.parseDouble(params[0])
                                    , Double.parseDouble(params[1])
                                    , Double.parseDouble(params[2]));

                            Point pointB = new Point(Double.parseDouble(params[3])
                                    , Double.parseDouble(params[4])
                                    , Double.parseDouble(params[5]));

                            Point pointC = new Point(Double.parseDouble(params[6])
                                    , Double.parseDouble(params[7])
                                    , Double.parseDouble(params[8]));
                            int matIndex = Integer.parseInt(params[9]);
                            parsers.add(new TriangleParser(pointA, pointB, pointC, matIndex));
                            System.out.println(String.format("Parsed triangle (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "lgt":
                        try {
                            Point position = new Point(Double.parseDouble(params[0])
                                    , Double.parseDouble(params[1])
                                    , Double.parseDouble(params[2]));
                            Color lightColor = Material.parseColor(Float.parseFloat(params[3])
                                    , Float.parseFloat(params[4])
                                    , Float.parseFloat(params[5]));
                            float specIntensity = Float.parseFloat(params[6]);
                            float shadowIntensity = Float.parseFloat(params[7]);
                            float lightRadius = Float.parseFloat(params[8]);

                            lights.add(new Light(position, lightColor, specIntensity, shadowIntensity, lightRadius));
                            System.out.println(String.format("Parsed light (line %d)", lineNum));
                        } catch (Exception e) {
                            System.out.println(String.format("Problem at  (line %d)", lineNum));
                            e.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
                        break;
                }
            }
        }

        for (ShapeParser p : parsers
                ) {
            shapes.add(p.parseShape(materials));
        }

        System.out.println("Finished parsing scene file " + sceneFileName);

    }

    /**
     * Renders the loaded scene and saves it to the specified file location.
     */
    public void renderScene(String outputFileName) {
        long startTime = System.currentTimeMillis();

        // Create a byte array to hold the pixel data:
        byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];


        // Put your ray tracing code here!
        Point[][] grid = cam.getGrid(this.imageWidth, this.imageHeight);
        for (int x = 0; x < this.imageWidth; x++) {
            for (int y = 0; y < this.imageHeight; y++) {

                int redIndex = (y * this.imageWidth + x) * 3;
                int greenIndex = redIndex + 1;
                int blueIndex = greenIndex + 1;

                Color tmp_color = new Color(0,0,0);
                Point dest = grid[x][y];

                Ray ray = new Ray(cam.position.toPoint(), dest);

                ray.setIntersections(shapes);
                if(ray.hitMap.size() == 0){
                    setPixelColor(rgbData,redIndex,greenIndex,blueIndex, scene.background);
                    continue;
                }

                List<Point> keyPoints = new ArrayList<Point>(ray.hitMap.keySet());


                keyPoints.sort(new Comparator<Point>() {
                    @Override
                    public int compare(Point o1, Point o2) {
                        return (o1.distance(cam.position.toPoint()) - o2.distance(cam.position.toPoint()) < 0) ? -1:1;
                    }
                });
//                if(keyPoints.size()>1){
//                    System.out.println("fefwfrw");
//                }
                Point p = keyPoints.get(0);
                //Color background_color = scene.background;
                Vector backgroundColor = new Vector(scene.background.getRed(),scene.background.getGreen(),scene.background.getBlue()).mul((1f/255f));
                Vector tmp_color_vector = ray.hitMap.get(p).getColor(p,lights,shapes,ray, 0, scene.maxRec,backgroundColor, scene.shadowRays);

                tmp_color=new Color((float)tmp_color_vector.x,(float)tmp_color_vector.y,(float)tmp_color_vector.z);


                if (tmp_color != null) {
                    setPixelColor(rgbData,redIndex,greenIndex,blueIndex, tmp_color);
                }
                else{
                    System.out.println(String.format("problem parsing color for pixel %d, %d", x, y));
                    break;
                }

            }
        }


        // Write pixel color values in RGB format to rgbData:
        // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
        //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
        //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
        //
        // Each of the red, green and blue components should be a byte, i.e. 0-255


        long endTime = System.currentTimeMillis();
        Long renderTime = endTime - startTime;

        // The time is measured for your own conveniece, rendering speed will not affect your score
        // unless it is exceptionally slow (more than a couple of minutes)
        System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

        // This is already implemented, and should work without adding any code.
        saveImage(this.imageWidth, rgbData, outputFileName);

        System.out.println("Saved file " + outputFileName);

    }

    void setPixelColor(byte[] array, int red, int green, int blue, Color tmp_color){
        array[red] = (byte) tmp_color.getRed();
        array[green] = (byte) tmp_color.getGreen();
        array[blue] = (byte) tmp_color.getBlue();
    }


    //////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

    /*
     * Saves RGB data as an image in png format to the specified location.
     */
    public static void saveImage(int width, byte[] rgbData, String fileName) {
        try {

            BufferedImage image = bytes2RGB(width, rgbData);
            ImageIO.write(image, "png", new File(fileName));

        } catch (IOException e) {
            System.out.println("ERROR SAVING FILE: " + e.getMessage());
        }

    }

    /*
     * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
     */
    public static BufferedImage bytes2RGB(int width, byte[] buffer) {
        int height = buffer.length / width / 3;
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        DataBufferByte db = new DataBufferByte(buffer, width * height);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        BufferedImage result = new BufferedImage(cm, raster, false, null);

        return result;
    }

    public static class RayTracerException extends Exception {
        public RayTracerException(String msg) {
            super(msg);
        }
    }


}
