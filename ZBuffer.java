import java.util.ArrayList;
import java.util.List;

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
            for (Aresta aresta : face.arestas) {
                varrerAresta(aresta);
            }
        }
    }

    public static List<List<Double>> varrerAresta(Aresta aresta){        
        int yMin = aresta.yMin;
        int yMax = aresta.yMax;
        int height = yMax - yMin + 1;

        List<List<Double>> scanlines = new ArrayList<>();
        List<List<Double>> zBuffer = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            scanlines.add(new ArrayList<>());
            zBuffer.add(new ArrayList<>());
        }

        if (aresta.origem.y == aresta.destino.y) return null;

        double deltaX = (aresta.destino.x - aresta.origem.x) / (aresta.destino.y - aresta.origem.y);
        double xInterseccao = aresta.origem.x;
        double zAnterior = 0;

        for (int y = (int)Math.ceil(aresta.origem.y); y < (int) Math.ceil(aresta.destino.y); y++){
            int scanlineIndex = y - aresta.yMin;
            scanlines.get(scanlineIndex).add(xInterseccao);
            
            if(y == (int)Math.ceil(aresta.origem.y)) {
                zAnterior = aresta.origem.z;
            } else {
                zAnterior = zAnterior + aresta.tz;
            }
           zBuffer.get(scanlineIndex).add(zAnterior);
            xInterseccao += deltaX;
        }

        for(int i = 0; i < scanlines.size(); i++){
            System.out.println(scanlines.get(i));
            System.out.println(zBuffer.get(i));
        }

        return zBuffer;

    }
}