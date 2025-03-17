package luminosidade;

import java.io.Serializable;

import Geometria.*;
public class Lampada implements Serializable {
    private static final long serialVersionUID = 1L;
    public double ilr;
    public double ilg;
    public double ilb;
    public Point3D pos = new Point3D(0, 0, 0);

    public Lampada(){
        this.ilr = 150;
        this.ilg = 150;
        this.ilb = 150;
        this.pos.x = 70;
        this.pos.y = 20;
        this.pos.z = 35;
    }
}
