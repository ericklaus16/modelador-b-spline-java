package Main;


import java.io.Serializable;

public class Window implements Serializable {
    private static final long serialVersionUID = 1L;
    public double xmin, xmax, ymin, ymax;

    public Window(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }
}
