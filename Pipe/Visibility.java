package Pipe;
import Geometria.*;
public class Visibility {
    public Visibility() {}

    public static double VisibilidadeNormal(Point3D vrp, Point3D v1, Point3D v2, Point3D v3, Point3D v4) {


        Point3D normal = CalcularNormal(v4, v3, v2);
        // System.out.println("Normal: " + normal.x + " " + normal.y + " " + normal.z);
        Point3D pontoMedio = CalcularPontoMedioQuadrilatero(v1, v2, v3, v4);
        // System.out.println("Ponto Médio: " + pontoMedio.x + " " + pontoMedio.y + " " + pontoMedio.z);
        Point3D o = CalcularO(vrp, pontoMedio);
        return CalcularVisibilidade(o, normal);
    }

    public static double CalcularVisibilidade(Point3D o, Point3D n) {
        double visibilidade = Point3D.dotProduct(o, n);
        System.out.println("Visibilidade: " + visibilidade);
        return visibilidade;
    }

    public static Point3D CalcularO(Point3D vrp, Point3D pontoMedio) {
        Point3D vetorRes = Point3D.subtract(vrp, pontoMedio);
        return Point3D.getNormalizedVector(vetorRes);
    }

    public static Point3D CalcularNormal(Point3D v1, Point3D v2, Point3D v3) {
        // A, B, C e D
        Point3D p1 = Point3D.subtract(v1, v2);
        Point3D p2 = Point3D.subtract(v3, v2);
        Point3D vetor = Point3D.vetorialProduct(p2, p1);
        return Point3D.getNormalizedVector(vetor);
    }

    public static Point3D CalcularPontoMedioQuadrilatero(Point3D v1, Point3D v2, Point3D v3, Point3D v4) {
        double x = (v1.x + v2.x + v3.x + v4.x) / 4.0;
        double y = (v1.y + v2.y + v3.y + v4.y) / 4.0;
        double z = (v1.z + v2.z + v3.z + v4.z) / 4.0;
        return new Point3D(x, y, z);
    }
}
