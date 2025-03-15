package Geometria;
import Pipe.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;



public class Face implements Serializable {
    private static final long serialVersionUID = 1L;
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
        Point3D BC = Point3D.subtract(D, C);
        Point3D BA = Point3D.subtract(B, C);

        Point3D ABC = Point3D.vetorialProduct(BA, BC);
        // System.out.println("ABC: " + ABC.x + " " + ABC.y + " " + ABC.z);
        normal = Point3D.getNormalizedVector(ABC);
        // System.out.println("Normal: " + normal.x + " " + normal.y + " " + normal.z);
    }

    public void setO(Point3D vrp) {
        Point3D O = Point3D.subtract(vrp, centroide);
        o = Point3D.getNormalizedVector(O);
        // System.out.println("O: " + o.x + " " + o.y + " " + o.z);
    }

    public void setVisibility(Point3D vrp){
        // Calcular visibilidade
        visibilidade = Visibility.VisibilidadeNormal(vrp, A, B, C, D);
        // System.out.println("Visibilidade: " + visibilidade);
    }

    @Override
    public String toString() {
        return "Face: " + (A.x + " " + A.y + " " + A.z) + " -> " + (B.x + " " + B.y + " " + B.z) + " -> " + (C.x + " " + C.y + " " + C.z) + " -> " + D.x + " " + D.y + " " + D.z;
    }
}
