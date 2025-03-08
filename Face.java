import java.awt.*;
import java.util.List;

public class Face {
    public Point3D A;
    public Point3D B;
    public Point3D C;
    public Point3D D;
    public double d;
    public double visibilidade = 0;
    public int i;
    public int j;
    public List<Aresta> arestas;
    public Point3D centroide = new Point3D(0, 0, 0);
    public Point3D normal = new Point3D(0, 0, 0);
    public Point3D o = new Point3D(0, 0, 0);
    public Color corConstante = Color.black;

    // Cores para Gourard
    public Color corVerticeA = Color.black;
    public Color corVerticeB = Color.black;
    public Color corVerticeC = Color.black;
    public Color corVerticeD = Color.black;

    public Face(Point3D A, Point3D B, Point3D C, Point3D D, double d, int i, int j, Point3D vrp) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        this.d = d;
        this.i = i;
        this.j = j;

        setNormal();
        setO(vrp);
        setVisibility(vrp);
    }


    public void setNormal() {
        // Suponha face ABC

        Point3D BC = Point3D.subtract(C, B);
        Point3D BA = Point3D.subtract(A, B);

        Point3D ABC = Point3D.vetorialProduct(BC, BA);
        normal = Point3D.getNormalizedVector(ABC);
    }

    public void setO(Point3D vrp) {
        Point3D O = Point3D.subtract(vrp, centroide);
        o = Point3D.getNormalizedVector(O);
    }

    public void setVisibility(Point3D vrp){

        // Calcular visibilidade
        visibilidade = Visibility.VisibilidadeNormal(vrp, D, C, B, A);

    }

}
