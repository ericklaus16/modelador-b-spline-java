package Main;
import java.io.Serializable;

public class Viewport implements Serializable {
    private static final long serialVersionUID = 1L;
    public int umin, umax, vmin, vmax;

    public Viewport (int umin, int umax, int vmin, int vmax) {
        this.umin = umin;
        this.umax = umax;
        this.vmin = vmin;
        this.vmax = vmax;
    }
}
