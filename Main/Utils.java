package Main;
import Geometria.*;

import java.text.DecimalFormat;

public class Utils {
    private Utils() {}

    public static double[][] multiplicarMatriz(double[][] M1, double[][] M2) {
        int rows = M1.length;
        int cols = M2[0].length;
        int common = M1[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;
                for (int k = 0; k < common; k++) {
                    sum += M1[i][k] * M2[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    public static Point3D multiplicarMatrizPorPonto(double[][] M, Point3D p) {
        double x = M[0][0] * p.x + M[0][1] * p.y + M[0][2] * p.z + M[0][3] * p.h;
        double y = M[1][0] * p.x + M[1][1] * p.y + M[1][2] * p.z + M[1][3] * p.h;
        double z = M[2][0] * p.x + M[2][1] * p.y + M[2][2] * p.z + M[2][3] * p.h;
        double h = M[3][0] * p.x + M[3][1] * p.y + M[3][2] * p.z + M[3][3] * p.h;

        return new Point3D(x, y, z, h);
    }

    public static double cos(double angle) {
        double angulo = Math.toRadians(angle);
        return Math.cos(angulo);
    }

    public static double sin(double angle) {
        double angulo = Math.toRadians(angle);
        return Math.sin(angulo);
    }
}
