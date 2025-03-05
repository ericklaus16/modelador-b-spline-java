import java.awt.Color;
import java.util.List;

public class Settings {
    public int width;
    public int height;
    public int widthViewport;
    public int heightViewport;
    public Point3D cameraPos;
    public Point3D pontoFocal;
    public int m;
    public int n;
    public Point3D transform;
    public Point3D rotation;
    public double scale;
    public Color visibleEdgeColor;
    public Color notVisibleEdgeColor;
    public Color paintColor;
    public SurfaceType type;
    public Shader shader;
    public double ila;
    public Lampada lampada;
    public double kar, kag, kab;
    public double kdr, kdg, kdb;
    public double ksr, ksg, ksb;
    public double kn;

    public Settings() {
        this.width = 800;
        this.height = 800;
        this.widthViewport = 800;
        this.heightViewport = 800;
        this.cameraPos = new Point3D(0.25, 1, 1);
        this.pontoFocal = new Point3D(0, 0, 0);
        this.m = 4;
        this.n = 4;
        this.transform = new Point3D(0.0, 0.0, 0.0);
        this.rotation = new Point3D(0.0, 0.0, 0.0);
        this.scale = 1.0;
        this.visibleEdgeColor = Color.BLACK;
        this.notVisibleEdgeColor = Color.RED;
        this.paintColor = Color.GREEN;
        this.type = SurfaceType.Aberta;
        this.shader = Shader.Wireframe;
        this.lampada = new Lampada();
        this.ila = 120;
        this.kar = 0.4;
        this.kag = 0.3;
        this.kab = 0.2;
        this.kdr = 0.7;
        this.kdg = 0.6;
        this.kdb = 0.5;
        this.ksr = 0.5;
        this.ksg = 0.4;
        this.ksb = 0.3;
        this.kn = 2.15;
    }

    // Getters e Setters para cada atributo
}