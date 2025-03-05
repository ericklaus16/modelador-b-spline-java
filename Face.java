import java.awt.*;
import java.util.List;

public class Face {
    public Point3D A;
    public Point3D B;
    public Point3D C;
    public Point3D D;
    public double d;
    public int i;
    public int j;
    public List<Aresta> arestas;
    public Point3D centroide = new Point3D(0, 0, 0);
    public Point3D normal = new Point3D(0, 0, 0);
    public Point3D o = new Point3D(0, 0, 0);
    public Color corConstante = new Color(0, 0, 0);

    public Face(List<Aresta> arestas) {
        this.arestas = arestas;
    }

    public Face(Point3D A, Point3D B, Point3D C, Point3D D, double d, int i, int j) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        this.d = d;
        this.i = i;
        this.j = j;
    }


    public Point3D setNormal() {
        // Suponha face ABC

        Point3D BC = Point3D.subtract(C, B);
        Point3D BA = Point3D.subtract(A, B);

        Point3D ABC = Point3D.vetorialProduct(BC, BA);
        normal = Point3D.getNormalizedVector(ABC);

        return normal;
    }

    public Point3D setO(Point3D vrp) {
        Point3D O = Point3D.subtract(vrp, centroide);
        return Point3D.getNormalizedVector(O);
    }

}
