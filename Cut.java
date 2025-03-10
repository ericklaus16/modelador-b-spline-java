public class Cut {
    public double umin, umax, vmin, vmax;

    static class Vertices {
        double x1, y1, z1, r1, g1, b1;  // Primeiro vértice
        double x2, y2, z2, r2, g2, b2;  // Segundo vértice

        Vertices(double x1, double y1, double z1, double r1, double g1, double b1,
                 double x2, double y2, double z2, double r2, double g2, double b2) {
            this.x1 = x1; this.y1 = y1; this.z1 = z1; this.r1 = r1; this.g1 = g1; this.b1 = b1;
            this.x2 = x2; this.y2 = y2; this.z2 = z2; this.r2 = r2; this.g2 = g2; this.b2 = b2;
        }
    }

    public Cut(double umin, double umax, double vmin, double vmax) {
        this.umin = umin;
        this.umax = umax;
        this.vmin = vmin;
        this.vmax = vmax;
    }

    public Vertices recorteVertice(double x1, double y1, double z1, double r1, double g1, double b1,
                                   double x2, double y2, double z2, double r2, double g2, double b2) {

        System.out.println("\nInício:");
        System.out.printf("Primeiro vertice: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n", x1, y1, z1, r1, g1, b1);
        System.out.printf("Segundo vertice: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n", x2, y2, z2, r2, g2, b2);

        Vertices vertices = new Vertices(x1, y1, z1, r1, g1, b1, x2, y2, z2, r2, g2, b2);

        // Aplicar recorte primeiro na esquerda -> direita -> embaixo -> cima
        vertices = recorteEsquerda(vertices);
        if (vertices == null) return null;

        vertices = recorteDireita(vertices);
        if (vertices == null) return null;

        vertices = recorteBaixo(vertices);
        if (vertices == null) return null;

        vertices = recorteCima(vertices);

        if (vertices != null) {
            System.out.println("Resultado final do recorte:");
            System.out.printf("Primeiro vertice: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    vertices.x1, vertices.y1, vertices.z1, vertices.r1, vertices.g1, vertices.b1);
            System.out.printf("Segundo vertice: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    vertices.x2, vertices.y2, vertices.z2, vertices.r2, vertices.g2, vertices.b2);
        } else {
            System.out.println("Os dois vértices estão fora da viewport - aresta rejeitada");
        }

        return vertices;
    }

    private Vertices recorteEsquerda(Vertices aresta) {
        if (aresta.x1 >= umin && aresta.x2 >= umin) {
            System.out.println("Ambos os pontos estão dentro da borda esquerda");
            return aresta;
        }

        if (aresta.x1 < umin && aresta.x2 < umin) {
            System.out.println("Ambos os pontos estão fora da borda esquerda - aresta rejeitada");
            return null; 
        }

        double u;
        if (aresta.x1 < umin) {
            double x = umin;
            u = (umin - aresta.x1) / (aresta.x2 - aresta.x1);
            double y = aresta.y1 + u * (aresta.y2 - aresta.y1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Primeiro vertice (< umin) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(x, y, z, r, g, b, aresta.x2, aresta.y2, aresta.z2, aresta.r2, aresta.g2, aresta.b2);
        } else {
            double x = umin;
            u = (umin - aresta.x1) / (aresta.x2 - aresta.x1);
            double y = aresta.y1 + u * (aresta.y2 - aresta.y1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Segundo vertice (< umin) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(aresta.x1, aresta.y1, aresta.z1, aresta.r1, aresta.g1, aresta.b1, x, y, z, r, g, b);
        }
    }

    private Vertices recorteDireita(Vertices aresta) {
        if (aresta.x1 <= umax && aresta.x2 <= umax) {
            System.out.println("Ambos os pontos estão dentro da borda direita");
            return aresta; 
        }

        if (aresta.x1 > umax && aresta.x2 > umax) {
            System.out.println("Ambos os pontos estão fora da borda direita - aresta rejeitada");
            return null;
        }

        double u;
        if (aresta.x1 > umax) {
            double x = umax;
            u = (umax - aresta.x1) / (aresta.x2 - aresta.x1);
            double y = aresta.y1 + u * (aresta.y2 - aresta.y1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Primeiro vertice (> umax) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(x, y, z, r, g, b, aresta.x2, aresta.y2, aresta.z2, aresta.r2, aresta.g2, aresta.b2);
        } else {
            double x = umax;
            u = (umax - aresta.x1) / (aresta.x2 - aresta.x1);
            double y = aresta.y1 + u * (aresta.y2 - aresta.y1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Segundo vertice (> umax) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(aresta.x1, aresta.y1, aresta.z1, aresta.r1, aresta.g1, aresta.b1, x, y, z, r, g, b);
        }
    }

    private Vertices recorteBaixo(Vertices aresta) {
        if (aresta.y1 >= vmin && aresta.y2 >= vmin) {
            System.out.println("Ambos os pontos estão dentro da borda inferior");
            return aresta;
        }

        if (aresta.y1 < vmin && aresta.y2 < vmin) {
            System.out.println("Ambos os pontos estão fora da borda inferior - aresta rejeitada");
            return null;
        }

        double u;
        if (aresta.y1 < vmin) {
            double y = vmin;
            u = (vmin - aresta.y1) / (aresta.y2 - aresta.y1);
            double x = aresta.x1 + u * (aresta.x2 - aresta.x1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Primeiro vertice (< vmin) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(x, y, z, r, g, b, aresta.x2, aresta.y2, aresta.z2, aresta.r2, aresta.g2, aresta.b2);
        } else {
            double y = vmin;
            u = (vmin - aresta.y1) / (aresta.y2 - aresta.y1);
            double x = aresta.x1 + u * (aresta.x2 - aresta.x1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Segundo vertice (< vmin) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(aresta.x1, aresta.y1, aresta.z1, aresta.r1, aresta.g1, aresta.b1, x, y, z, r, g, b);
        }
    }

    private Vertices recorteCima(Vertices aresta) {
        if (aresta.y1 <= vmax && aresta.y2 <= vmax) {
            System.out.println("Ambos os pontos estão dentro da borda superior");
            return aresta;
        }

        if (aresta.y1 > vmax && aresta.y2 > vmax) {
            System.out.println("Ambos os pontos estão fora da borda superior - aresta rejeitada");
            return null;
        }

        double u;
        if (aresta.y1 > vmax) {
            double y = vmax;
            u = (vmax - aresta.y1) / (aresta.y2 - aresta.y1);
            double x = aresta.x1 + u * (aresta.x2 - aresta.x1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Primeiro vertice (> vmax) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(x, y, z, r, g, b, aresta.x2, aresta.y2, aresta.z2, aresta.r2, aresta.g2, aresta.b2);
        } else {
            double y = vmax;
            u = (vmax - aresta.y1) / (aresta.y2 - aresta.y1);
            double x = aresta.x1 + u * (aresta.x2 - aresta.x1);
            double z = aresta.z1 + u * (aresta.z2 - aresta.z1);
            double r = aresta.r1 + u * (aresta.r2 - aresta.r1);
            double g = aresta.g1 + u * (aresta.g2 - aresta.g1);
            double b = aresta.b1 + u * (aresta.b2 - aresta.b1);

            System.out.printf("Segundo vertice (> vmax) estava fora, recortado para: (%.3f, %.3f, %.3f, %.3f, %.3f, %.3f)%n",
                    x, y, z, r, g, b);
            return new Vertices(aresta.x1, aresta.y1, aresta.z1, aresta.r1, aresta.g1, aresta.b1, x, y, z, r, g, b);
        }
    }

    public static void main(String[] args) {
        Cut recorte = new Cut(100, 400, 80, 380);

        System.out.println("Inicio dos testes");

        // Exemplo da planilha Recorte 2D p1
        recorte.recorteVertice(0.0, 250.0, -30.0, 200.0, 120.0, 30.0, 
                250.0, 430.0, -65.0, 40.0, 250.0, 100.0); // AB cortando A'

        recorte.recorteVertice(480.0, 0.0, -90.0, 100.0, 10.0, 190.0,
                0.0, 250.0, -30.0, 200.0, 120.0, 30.0); // CA cortando A""

        recorte.recorteVertice(250.0, 430.0, -65.0, 40.0, 250.0, 100.0,
                480.0, 0.0, -90.0, 100.0, 10.0, 190.0); // BC cortando C'

        recorte.recorteVertice(480.0, 0.0, -90.0, 100.0, 10.0, 190.0,
                100.0, 197.917, -42.500, 179.167, 97.083, 63.333); // CA" cortando C"

        recorte.recorteVertice(250.0, 430.0, -65.0, 40.0, 250.0, 100.0,
                400.0, 149.565, -81.304, 79.130, 93.478, 158.696); // BC' cortando B'

        recorte.recorteVertice(100.0, 322.0, -44.0, 136.0, 172.0, 58.0,
                250.0, 430.0, -65.0, 40.0, 250.0, 100.0); // A'B cortando B"

        recorte.recorteVertice(400.0, 149.565, -81.304, 79.130, 93.478, 158.696,
                400.0, 41.667, -80.0, 116.667, 28.333, 163.333); // C'C" cortando C'''

        recorte.recorteVertice(400.0, 41.667, -80.0, 116.667, 28.333, 163.333,
                100.0, 197.917, -42.500, 179.167, 97.083, 63.333); // C"A" cortando C''''
    }
}