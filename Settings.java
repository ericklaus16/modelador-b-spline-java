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
    public double ka;
    public double kd;
    public double ks;
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
        this.ka = 0;
        this.kd = 0;
        this.ks = 0;
        this.kn = 0;
    }

    // Getters e Setters para cada atributo
}