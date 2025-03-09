import java.io.Serializable;

public class Curve implements Serializable {
    private static final long serialVersionUID = 1L;

    public void SplinePoint(double[] u, int n, int t, double v, Point3D[] control, Point3D output) {
        output.x = (0);
        output.y = (0);
        output.z = (0);

        for (int k = 0; k <= n; k++) {
            double b = SplineBlend(k, t, u, v);
            output.x = (output.x + control[k].x * b);
            output.y = (output.y + control[k].y * b);
            output.z = (output.z + control[k].z * b);
        }
    }

    public double SplineBlend(int k, int t, double[] u, double v) {
        double value;

        if (t == 1) {
            if (u[k] <= v && v < u[k + 1]) {
                value = 1;
            } else {
                value = 0;
            }
        } else {
            if ((u[k + t - 1] == u[k]) && (u[k + t] == u[k + 1])) {
                value = 0;
            } else if (u[k + t - 1] == u[k]) {
                value = (u[k + t] - v) / (u[k + t] - u[k + 1]) * SplineBlend(k + 1, t - 1, u, v);
            } else if (u[k + t] == u[k + 1]) {
                value = (v - u[k]) / (u[k + t - 1] - u[k]) * SplineBlend(k, t - 1, u, v);
            } else {
                value = (v - u[k]) / (u[k + t - 1] - u[k]) * SplineBlend(k, t - 1, u, v) +
                        (u[k + t] - v) / (u[k + t] - u[k + 1]) * SplineBlend(k + 1, t - 1, u, v);
            }
        }

        return value;
    }

    public void SplineKnots(double[] u, int n, int t) {
        for (int j = 0; j <= n + t; j++) {
            if (j < t) {
                u[j] = 0;
            } else if (j <= n) {
                u[j] = j - t + 1;
            } else {
                u[j] = n - t + 2;
            }
        }
    }

    public void SplineCurve(Point3D[] inp, int n, double[] knots, int t, Point3D[] outp, int res) {
        int i;
        double interval;
        double increment;

        for (i = 0; i < res; i++) {
            outp[i] = new Point3D(0, 0, 0);
        }

        interval = 0;
        increment = (double) (n - t + 2) / (res - 1);

        for (i = 0; i < res - 1; i++) {
            SplinePoint(knots, n, t, interval, inp, outp[i]);
            interval += increment;
        }
        outp[res - 1] = new Point3D(inp[n].x, inp[n].y, inp[n].z);
    }
}
