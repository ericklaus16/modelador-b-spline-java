import java.io.Serializable;

public class Lightning implements Serializable {
    private static final long serialVersionUID = 1L;
    public static double Illuminate(Point3D centroide, Point3D normal, double ila, Point3D l, double il, double ka, double kd, double ks, Point3D s) {
        double iA = Ambient(ka, ila);
//        System.out.println("Ambient: " + iA);

        Point3D l1 = Point3D.subtract(l, centroide);
        Point3D n1 = Point3D.getNormalizedVector(l1);
        double iD = Diffuse(normal, il, kd, n1);
        double iT;

        if (iD > 0) {
//            System.out.println("Diffuse: " + iD);
            double iS = Specular(normal, ks, il, s, n1);
//            System.out.println("Specular: " + iS);
            iT = iA + iD + iS;
        } else {
            iT = iA;
        }

//        System.out.println("Total: " + iT);
        if (iT > 255){
            iT = 255;
        }
        return iT;
    }

    public static double Ambient(double ka, double ila) {
        return ila * ka;
    }

    public static double Diffuse(Point3D normal, double il, double kd, Point3D l) {
        double illumination = Point3D.dotProduct(normal, l);
        return illumination > 0 ? il * kd * illumination : 0;
    }

    public static double Specular(Point3D normal, double ks, double il, Point3D s, Point3D l) {
        double rmultp = Point3D.dotProduct(new Point3D(l.x * 2, l.y * 2, l.z * 2), normal);
        Point3D rmultp2 = Point3D.multiply(normal, rmultp);
        Point3D r = Point3D.subtract(rmultp2, l);
//        System.out.println("R: " + r);

        Point3D r1 = Point3D.getNormalizedVector(r);
//        System.out.println("R Normalized: " + r1);

        double spec = Point3D.dotProduct(r1, s);
//        System.out.println("Spec: " + spec);

        return (spec > 0 && Point3D.dotProduct(normal, l) > 0) ? il * ks * Math.pow(spec, ks) : 0;
    }
}
