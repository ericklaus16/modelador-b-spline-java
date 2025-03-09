import java.io.Serializable;

public class Point3D implements Serializable {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;
    public double h;

    public Point3D(double x, double y, double z) {
        this(x, y, z, 1);
    }

    public Point3D(double x, double y, double z, double h) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.h = h;
    }

    public static Point3D subtract(Point3D a, Point3D b) {
        return new Point3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Point3D add(Point3D a, Point3D b) {
        return new Point3D(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Point3D getNormalizedVector(Point3D vector) {
        double magnitude = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
        return new Point3D(vector.x / magnitude, vector.y / magnitude, vector.z / magnitude);
    }

    public static double dotProduct(Point3D a, Point3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Point3D multiply(Point3D a, double b) {
        return new Point3D(a.x * b, a.y * b, a.z * b);
    }

    public static Point3D vetorialProduct(Point3D a, Point3D b) {
        return new Point3D(
            a.y * b.z - a.z * b.y,
            a.z * b.x - a.x * b.z,
            a.x * b.y - a.y * b.x
        );
    }
}
