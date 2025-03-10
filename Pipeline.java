import java.io.Serializable;

public class Pipeline implements Serializable{
    private static final long serialVersionUID = 1L;
    private Pipeline() {}

    public static Point2D mapearPonto(Point3D vertex, Point3D p, Point3D vrp, Viewport vp) {
        double[][] msrusrc = transformacaoDeCamera(vrp, p);

        double[][] mproj = projecaoParalela();

        double[][] mwindow = windowViewport(vp);
        for(int i = 0; i < mwindow.length; i++) {
            for(int j = 0; j < mwindow[0].length; j++) {
                System.out.print(mwindow[i][j] + " ");
            }
            System.out.println();
        }

        double[][] mult = Utils.multiplicarMatriz(mwindow, mproj);
        double[][] msrusrt = Utils.multiplicarMatriz(mult, msrusrc);

        Point3D novoPonto = Utils.multiplicarMatrizPorPonto(msrusrt, vertex);
        Point2D ponto2D = new Point2D(novoPonto.x, novoPonto.y);

        if (ponto2D.h != 1) {
            ponto2D.x = ponto2D.x / ponto2D.h;
            ponto2D.y = ponto2D.y / ponto2D.h;
            ponto2D.y *= -1;
            ponto2D.h = 1;
        }

        return ponto2D;
    }

    public static double[][] transformacaoDeCamera(Point3D vrp, Point3D p) {
        Point3D N = Point3D.subtract(vrp, p);
        Point3D n = Point3D.getNormalizedVector(N);

        Point3D Y = new Point3D(0, 1, 0);
        double dotProductYn = Point3D.dotProduct(Y, n);
        Point3D y = Point3D.multiply(n, dotProductYn);

        Point3D V = Point3D.subtract(Y, y);
        Point3D v = Point3D.getNormalizedVector(V);

        Point3D U = Point3D.vetorialProduct(V, N);
        Point3D u = Point3D.getNormalizedVector(U);

        return new double[][] {
            {u.x, u.y, u.z, -Point3D.dotProduct(vrp, u)},
            {v.x, v.y, v.z, -Point3D.dotProduct(vrp, v)},
            {n.x, n.y, n.z, -Point3D.dotProduct(vrp, n)},
            {0, 0, 0, 1}
        };
    }

    public static double[][] projecaoPerspectiva(double d) {
        return new double[][] {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 1 / d, 0}
        };
    }

    public static double[][] projecaoParalela() {
        return new double[][] {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }

    public static double[][] windowViewport(Viewport vp) {
        double umin = vp.umin, umax = vp.umax;
        double vmin = vp.vmin, vmax = vp.vmax;
        double xmin = vp.xmin, xmax = vp.xmax;
        double ymin = vp.ymin, ymax = vp.ymax;

        return new double[][] {
            {(umax - umin) / (xmax - xmin), 0, 0, (-xmin * (umax - umin) / (xmax - xmin)) + umin},
            {0, (vmin - vmax) / (ymax - ymin), 0, (ymin * (vmax - vmin) / (ymax - ymin)) + vmax},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }
}
