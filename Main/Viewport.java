package Main;
import java.io.Serializable;

public class Viewport implements Serializable {
    private static final long serialVersionUID = 1L;
    public double umin, umax, vmin, vmax;
    public double xmin, xmax, ymin, ymax;

    public Viewport (double umin, double umax, double vmin, double vmax, double xmin, double xmax, double ymin, double ymax) {
        this.umin = umin;
        this.umax = umax;
        this.vmin = vmin;
        this.vmax = vmax;
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }
}
