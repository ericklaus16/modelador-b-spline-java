public class Lightning {
    public static double Illuminate(Face face, double ila, Point3D l, double il, double ka, double kd, double ks, Point3D s) {
        double iA = Ambient(ka, ila);
//        System.out.println("Ambient: " + iA);

        Point3D l1 = Point3D.subtract(l, face.centroide);
        Point3D n1 = Point3D.getNormalizedVector(l1);
        double iD = Diffuse(face, il, kd, n1);
        double iT;

        if (iD > 0) {
//            System.out.println("Diffuse: " + iD);
            double iS = Specular(face, ks, il, s, n1);
//            System.out.println("Specular: " + iS);
            iT = iA + iD + iS;
        } else {
            iT = iA;
        }

//        System.out.println("Total: " + iT);
        return iT;
    }

    public static double Ambient(double ka, double ila) {
        return ila * ka;
    }

    public static double Diffuse(Face face, double il, double kd, Point3D l) {
        double illumination = Point3D.dotProduct(face.normal, l);
        return illumination > 0 ? il * kd * illumination : 0;
    }

    public static double Specular(Face face, double ks, double il, Point3D s, Point3D l) {
        double rmultp = Point3D.dotProduct(new Point3D(l.x * 2, l.y * 2, l.z * 2), face.normal);
        Point3D rmultp2 = Point3D.multiply(face.normal, rmultp);
        Point3D r = Point3D.subtract(rmultp2, l);
//        System.out.println("R: " + r);

        Point3D r1 = Point3D.getNormalizedVector(r);
//        System.out.println("R Normalized: " + r1);

        double spec = Point3D.dotProduct(r1, s);
//        System.out.println("Spec: " + spec);

        return (spec > 0 && Point3D.dotProduct(face.normal, l) > 0) ? il * ks * Math.pow(spec, ks) : 0;
    }
}
