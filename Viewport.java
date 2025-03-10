public class Viewport {
    public double umin, umax, vmin, vmax;
    public double xmin, xmax, ymin, ymax;

    public Viewport(int width, int height, int vpWidth, int vpHeight) {
        double scale = 0.015; // 0.035
        this.umin = 0;
        this.umax = vpWidth - 1;
        this.vmin = 0;
        this.vmax = vpHeight - 1;

        // Ajustando o tamanho do viewport com base na escala
        double adjustedWidth = width * scale;
        double adjustedHeight = height * scale;

        this.xmin = -adjustedWidth / 2;
        this.xmax = adjustedWidth / 2;
        this.ymin = -adjustedHeight / 2;
        this.ymax = adjustedHeight / 2;
    }

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
