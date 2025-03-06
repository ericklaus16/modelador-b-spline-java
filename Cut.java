public class Cut {
    public double umin, umax, vmin, vmax;

    static final int INSIDE = 0; // 0000
    static final int LEFT = 1;   // 0001
    static final int RIGHT = 2;  // 0010
    static final int BOTTOM = 4; // 0100
    static final int TOP = 8;    // 1000
    
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

    // Método para calcular o código de região
    int computeCode(double x, double y) {
        int code = INSIDE;

        if (x < umin) {
            code |= LEFT;
        } else if (x > umax) {
            code |= RIGHT;
        }
        if (y < vmin) {
            code |= BOTTOM;
        } else if (y > vmax) {
            code |= TOP;
        }

        return code;
    }
    
    public void teste(double x1, double y1, double z1, double r1, double g1, double b1, 
                      double x2, double y2,  double z2,  double r2,  double g2,  double b2) {
        System.out.println("\n");
        int code1 = computeCode(x1, y1);
        int code2 = computeCode(x2, y2);

        if ((code1 == 0) && (code2 == 0)) {
            // Ambos os pontos estão dentro da janela
            System.out.println("Aresta aceita: (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
        } else if ((code1 & code2) != 0) {
            // Ambos os pontos estão fora da janela, na mesma região
            System.out.println("Aresta rejeitada");
        } else {
            // Pelo menos um ponto está fora da janela
            int codeOut = 0;
            double x = 0, y = 0, z = 0, r = 0, g = 0, b = 0;

            // Escolhe o ponto fora da janela
            if (code1 != 0) {
                codeOut = code1;
            }else if (code2 != 0) {
                codeOut = code2;
            }

            if ((codeOut & LEFT) != 0) { // O ponto está à esquerda
                double u = (umin - x1) / (x2 - x1);
                System.out.println("Recorte na coordenada X Esq");
                x = umin;
                y = (y1 + u * (y2 - y1));
                z = (z1 + u * (z2 - z1));
                r = (r1 + u * (r2 - r1));
                g = (g1 + u * (g2 - g1));
                b = (b1 + u * (b2 - b1));
            } else if ((codeOut & RIGHT) != 0) { // O ponto está à direita
                double u = (umax - x1) / (x2 - x1);
                System.out.println("Recorte na coordenada X Dir");
                x = umax;
                y = (y1 + u * (y2 - y1));
                z = (z1 + u * (z2 - z1));
                r = (r1 + u * (r2 - r1));
                g = (g1 + u * (g2 - g1));
                b = (b1 + u * (b2 - b1));
            } else if ((codeOut & BOTTOM) != 0) { // O ponto está abaixo
                double u = (vmin - y1) / (y2 - y1);
                System.out.println("Recorte na coordenada Y Bot");
                y = vmin;
                x = x1 + u * (x2 - x1);
                z = (z1 + u * (z2 - z1));
                r = (r1 + u * (r2 - r1));
                g = (g1 + u * (g2 - g1));
                b = (b1 + u * (b2 - b1));
            } else if ((codeOut & TOP) != 0) { // O ponto está acima
                double u = (vmax - y1) / (y2 - y1);
                System.out.println("Recorte na coordenada Y Top");
                y = vmax;
                x = x1 + u * (x2 - x1);
                z = (z1 + u * (z2 - z1));
                r = (r1 + u * (r2 - r1));
                g = (g1 + u * (g2 - g1));
                b = (b1 + u * (b2 - b1));
            }

            // Atualiza o ponto fora da janela
            if (codeOut == code1) {
                x1 = x;
                y1 = y;
                z1 = z;
                r1 = r;
                g1 = g;
                b1 = b;
                System.out.printf("Aresta aceita: (x1: %.3f, y1: %.3f, z1: %.3f, r1: %.3f, g1: %.3f, b1: %.3f)%n", x1, y1, z1, r1, g1, b1);
            } else {
                x2 = x;
                y2 = y;
                z2 = z;
                r2 = r;
                g2 = g;
                b2 = b;
                System.out.printf("Aresta aceita: (x2: %.3f, y2: %.3f, z2: %.3f, r2: %.3f, g2: %.3f, b2: %.3f)%n", x2, y2, z2, r2, g2, b2);
            }
        }
    }

    public static void main(String[] args) {
        Cut recorte = new Cut(100, 400, 80, 380);

        System.out.print("Inicio dos testes\n");
        recorte.teste(0.0, 250.0, -30.0, 200.0, 120.0, 30.0, 250.0, 430.0, -65.0, 40.0, 250.0, 100.0); // AB cortando A'
        recorte.teste(480.0, 0.0, -90.0, 100.0, 10.0, 190.0, 0.0, 250.0, -30.0, 200.0, 120.0, 30.0); // CA cortando A""
        recorte.teste(250.0, 430.0, -65.0, 40.0, 250.0, 100.0, 480.0, 0.0, -90.0, 100.0, 10.0, 190.0); // BC cortando C'
        recorte.teste(480.0, 0.0, -90.0, 100.0, 10.0, 190.0, 100.0, 197.917, -42.500, 179.167, 97.083, 63.333); // CA" cortando C"
        recorte.teste(250.0, 430.0, -65.0, 40.0, 250.0, 100.0, 400.0, 149.565, -81.304, 79.130, 93.478, 158.696); // BC' cortando B'
        recorte.teste(100.0, 322.0, -44.0, 136.0, 172.0, 58.0, 250.0, 430.0, -65.0, 40.0, 250.0, 100.0); // A'B cortando B"
        recorte.teste(400.0, 149.565, -81.304, 79.130, 93.478, 158.696, 400.0, 41.667, -80.0, 116.667, 28.333, 163.333); // C'C" cortando C'''
        recorte.teste(400.0, 41.667, -80.0, 116.667, 28.333, 163.333, 100.0, 197.917, -42.500, 179.167, 97.083, 63.333); // C"A" cortando C''''
    }
}
