public class Cut {
    public double umin, umax, vmin, vmax;

//    public void Cut(Viewport vp) {
//        double umin = vp.umin;
//        double umax = vp.umax;
//        double vmin = vp.vmin;
//        double vmax = vp.vmax;
//    }

    public Cut(double umin, double umax, double vmin, double vmax) {
        this.umin = umin;
        this.umax = umax;
        this.vmin = vmin;
        this.vmax = vmax;
    }

    private boolean dentro(double x, double y) {
        return x >= umin && x <= umax && y >= vmin && y <= vmax;
    }
    
    private boolean dentroMinX(double x) {
        return !(x >= umin);
    }
    
    private boolean dentroMinY(double y) {
        return !(y >= vmin);
    }
    
    private boolean dentroMaxX(double x) {
        return !(x <= umax);
    }
    
    private boolean dentroMaxY(double y) {
        return !(y <= vmax);
    }

    public void recortar(int x1, int y1, int x2, int y2) {
        if (dentro(x1, y1) && dentro(x2, y2)) {
            System.out.println("Segmento totalmente dentro: (" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")");
        } else {
            System.out.println("Segmento fora ou parcialmente dentro (não implementado o algoritmo completo)");
        }
    }
    
    public void teste(double x1, double y1, double x2, double y2) {
        System.out.println("Teste");
        if (dentroMinX(x1) || dentroMinX(x2)) { 
            double u = (umin - x1) / (x2 - x1);

            System.out.println("Recorte na coordenada X (algum ponto menor que Xmin)");
            if (dentroMinX(x1)) { // x1 < umin
                x1 = umin;
            } else if (dentroMinX(x2)) { // x2 < umin
                x2 = umin;
            }
            double y = (y1 + u * (y2 - y1));
            System.out.println("Ponto de corte: (x1: " + x1 + " x2: " + x2 + ", " + y + ")");
        } else if (dentroMaxX(x1) || dentroMaxX(x2)) {
            double u = (umax - x1) / (x2 - x1);

            System.out.println("Recorte na coordenada X (algum ponto maior que Xmax)");
            if (dentroMaxX(x1)) { // x1 > umax
                x1 = umax;
            } else if (dentroMaxX(x2)) { // x2 > umax
                x2 = umax;
            }
            double y = (y1 + u * (y2 - y1));
            System.out.println("Ponto de corte: (x1: " + x1 + " x2: " + x2 + ", " + y + ")");
        } else if (dentroMinY(y1) || dentroMinY(y2)) {
            double u = (vmin - y1) / (y2 - y1);

            System.out.println("Recorte na coordenada Y (algum ponto menor que Ymin)");
            if (dentroMinY(y1)) { // y1 < vmin
                y1 = vmin;
            } else if (dentroMinY(y2)) { // y2 < vmin
                y2 = vmin;
            }
            double x = x1 + u * (x2 - x1);
            System.out.println("Ponto de corte: (" + x + ", " + " y1: " + y1 + " y2: " + y2 + ")");
        } else if (dentroMaxY(y1) || dentroMaxY(y2)) {
            double u = (vmax - y1) / (y2 - y1);

            System.out.println("Recorte na coordenada Y (algum ponto maior que Ymax)");
            if (dentroMaxY(y1)) { // y1 > vmax
                y1 = vmax;
            } else if (dentroMaxY(y2)) { // y2 > vmax
                y2 = vmax;
            }
            double x = x1 + u * (x2 - x1);
            System.out.println("Ponto de corte: (" + x + ", " + " y1: " + y1 + " y2: " + y2 + ")");
        }
    }

    public static void main(String[] args) {
        Cut recorte = new Cut(100, 400, 80, 380);
//        recorte.recortar(480, 0, 0, 250); // U = 0.791 baseado na planilha Adair
        
        System.out.print("/n");
        recorte.teste(0.0, 250.0, 250.0, 430.0); // AB cortando A'
        recorte.teste(480.0, 0.0, 0.0, 250.0); // CA cortando A""
        recorte.teste(250.0, 430.0, 480.0, 0.0); // BC cortando C'
        recorte.teste(480.0, 0.0, 100.0, 197.917); // CA" cortando C"
        recorte.teste(250.0, 430.0, 400.0, 149.565); // BC' cortando B'
        recorte.teste(100.0, 322.0, 250.0, 430.0); // A'B cortando B"
        recorte.teste(400.0, 149.565, 400.0, 41.667); // C'C" cortando C'''
        recorte.teste(400.0, 41.667, 100.0, 197.917); // C"A" cortando C''''
    }
}
