package Geometria;
import java.io.Serializable;

public class Aresta implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Point3D origem;
    public Point3D destino;
    public int yMin;
    public int yMax;
    public double x;
    public double z;
    public double dx;
    public double dy;
    public double dz;
    public double tx;
    public double tz;

    public Aresta(Point3D origem, Point3D destino) {
        this.origem = origem;
        this.destino = destino;

        if (origem.y > destino.y) {
            Point3D temp = this.origem;
            this.origem = this.destino;
            this.destino = temp;
        }

        this.yMin = (int) Math.floor(Math.min(this.origem.y, this.destino.y));
        this.yMax = (int) Math.ceil(Math.max(this.origem.y, this.destino.y));
        this.x = this.origem.x;
        this.z = this.origem.z;
        this.dx = this.destino.x - this.origem.x;
        this.dy = this.destino.y - this.origem.y;
        this.dz = this.destino.z - this.origem.z;
        this.tx = this.dx / this.dy;
        this.tz = this.dz / this.dy;
    }
}
