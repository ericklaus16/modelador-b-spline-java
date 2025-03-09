import java.io.Serializable;

public class Lampada implements Serializable {
    private static final long serialVersionUID = 1L;
    public double il;
    public Point3D pos = new Point3D(0, 0, 0);

    public Lampada(){
        this.il = 150;
        this.pos.x = 70;
        this.pos.y = 20;
        this.pos.z = 35;
    }
}
