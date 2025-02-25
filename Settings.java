import java.awt.Color;

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
        this.type = SurfaceType.ABERTA;
        this.shader = Shader.WIREFRAME;
        this.kar = 0;
        this.kag = 0;
        this.kab = 0;
        this.kdr = 0;
        this.kdg = 0;
        this.kdb = 0;
        this.ksr = 0;
        this.ksg = 0;
        this.ksb = 0;
        this.kn = 0;
    }

    // Getters e Setters para cada atributo
}