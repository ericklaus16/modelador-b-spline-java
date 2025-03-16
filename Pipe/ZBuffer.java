package Pipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Geometria.*;

public class ZBuffer {

    public static void buffer(Point3D[][] points3D) {
        List<Face> faces = new ArrayList<>();

        // Pegando aresta de cada face
        for (int i = 0; i < points3D.length - 1; i++) {
            for (int j = 0; j < points3D[0].length - 1; j++) {
                /*
                A B
                D C
                */
                Point3D A = points3D[i][j];
                Point3D B = points3D[i][j + 1];
                Point3D C = points3D[i + 1][j + 1];
                Point3D D = points3D[i + 1][j];
                Point3D centroide = new Point3D(
                    (A.x + B.x + C.x + D.x) / 4, 
                    (A.y + B.y + C.y + D.y) / 4, 
                    (A.z + B.z + C.z + D.z) / 4
                );
                Point3D normal = Visibility.CalcularNormal(D, C, B);

                Aresta AB = new Aresta(A, B);
                Aresta BC = new Aresta(B, C);
                Aresta CD = new Aresta(C, D);
                Aresta DA = new Aresta(D, A);

                Face face = new Face(A, B, C, D, 0, i, j, new Point3D(0, 0, 0));
                face.centroide = (centroide);
                face.normal = (normal);
                faces.add(face);
            }
        }
        
        for (Face face : faces) {
            varrerArestas(face.arestas);
        }
    }

    public static void varrerArestas(List<Aresta> arestas) {
        Optional<Aresta> arestaMenorY = arestas.stream()
            .min((a1, a2) -> Integer.compare(a1.yMin, a2.yMin));
        Optional<Aresta> arestaMaiorY = arestas.stream()
            .max((a1, a2) -> Integer.compare(a1.yMax, a2.yMax));

        int yMin = arestaMenorY.get().yMin;
        int yMax = arestaMaiorY.get().yMax;
        int height = yMax - yMin + 1;

        arestas = arestas.stream()
        .sorted((a1, a2) -> {
            int cmpY = Integer.compare(a1.yMin, a2.yMin);
            if (cmpY == 0) {  // Se yMin for igual, desempate pelo x inicial
                int cmpX = Double.compare(a1.origem.x, a2.origem.x);
                if (cmpX == 0) {  // Se x for igual, desempate pelo Tx (menor Tx vem primeiro)
                    return Double.compare(a1.tx, a2.tx);
                }
                return cmpX;
            }
            return cmpY;
        })
        .toList();

        // Se após ordenado tiverem arestas com o mesmo x, ordenar só elas para que a que tiver o menor tX fique na frente (menor ao maior)

        List<List<Double>> scanlines = new ArrayList<>();
        List<List<Double>> zBuffer = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            scanlines.add(new ArrayList<>());
            zBuffer.add(new ArrayList<>());
        }
        
        for (int i = yMin; i < yMax; i++) {
            final int currentY = i;
            
            List<Aresta> arestasAtivas = arestas.stream()
            .filter(a -> a.yMin <= currentY && a.yMax > currentY)
            .toList();
            
            double currentX1 = (arestasAtivas.get(0).x);
            double currentX2 = (arestasAtivas.get(1).x);
            double currentZ1 = arestasAtivas.get(0).z;
            double currentZ2 = arestasAtivas.get(1).z;

            double denominador = currentX2 - currentX1;
            double numerador = currentZ2 - currentZ1;

            double tz;

            if (denominador == 0 || numerador == 0) {
                tz = 0;
            } else {
                tz = numerador / denominador;
            }

            // System.out.println(arestasAtivas.get(0).x + " " + arestasAtivas.get(1).x);
            // System.out.println(arestasAtivas.get(0).z + " " + arestasAtivas.get(1).z);
            
            // System.out.println("Xi: " + currentX1 + " Xf: " + currentX2 + " Tz: " + tz);
            
            zBuffer.get(i - yMin).add(currentZ1 + (Math.ceil(currentX1) - arestasAtivas.get(0).x) * tz);

            for (int j = 0; j < Math.floor(currentX2) - Math.ceil(currentX1); j++) {
                double ultimo = zBuffer.get(i - yMin).get(zBuffer.get(i - yMin).size() - 1);
                zBuffer.get(i - yMin).add(ultimo + tz);

                // System.out.println(ultimo + tz);
            }
            // System.out.println(currentZ1 + " + " + (Math.ceil(currentX1) - arestasAtivas.get(0).x) + " * " + tz);
            System.out.println(zBuffer.get(i - yMin));
            // System.out.println("");

            arestasAtivas.get(0).x += arestasAtivas.get(0).tx;
            arestasAtivas.get(1).x += arestasAtivas.get(1).tx;

            arestasAtivas.get(0).z += arestasAtivas.get(0).tz;
            arestasAtivas.get(1).z += arestasAtivas.get(1).tz;
        }
    }
}