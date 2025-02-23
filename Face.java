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

    public void setCentroide(Point3D centroide) {
        this.centroide = centroide;
    }

    public void setNormal(Point3D normal) {
        this.normal = normal;
    }
}
