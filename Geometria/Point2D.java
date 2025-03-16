package Geometria;
import java.io.Serializable;

public class Point2D implements Serializable{
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z = 0;
    public double h;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.h = 1;
    }
}