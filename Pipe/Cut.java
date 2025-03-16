package Pipe;
import Geometria.*;
import Main.Viewport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cut {
    public double umin, umax, vmin, vmax;

    public Cut(double umin, double umax, double vmin, double vmax) {
        this.umin = umin;
        this.umax = umax;
        this.vmin = vmin;
        this.vmax = vmax;
    }

    public static class Vertice {
        public double x, y, z;
        double r, g, b;

        public Vertice(double x, double y, double z, double r, double g, double b) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            this.g = g;
            this.b = b;
        }


        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
        public double getR() { return r; }
        public double getG() { return g; }
        public double getB() { return b; }

        @Override
        public String toString() {
            return String.format("(%.3f, %.3f, %.3f) RGB(%.3f, %.3f, %.3f)",
                    x, y, z, r, g, b);
        }
    }

    public static List<Point2D> mapearERecorte(List<Point2D> points2D, Point3D p, Point3D vrp, Viewport vp) {
        List<Vertice> vertices = new ArrayList<>();
        for (Point2D p2d : points2D) {
            vertices.add(new Vertice(p2d.x, p2d.y, p2d.z, 0, 0, 0));
        }

        // Step 3: Create clipper with viewport boundaries
        Cut clipper = new Cut(vp.umin, vp.umax, vp.vmin, vp.vmax);

        // Step 4: Clip the polygon
        List<Vertice> clippedVertices = clipper.recortarPoligono(vertices);

        // Step 5: Convert back to Point2D
        List<Point2D> result = new ArrayList<>();
            for (Vertice v : clippedVertices) {
                Point2D ponto = new Point2D(v.x, v.y);
                ponto.z = v.z;
                result.add(ponto);
            }
            return result;
    }

    public List<Vertice> recortarPoligono(List<Vertice> poligonoEntrada) {
        if (poligonoEntrada == null || poligonoEntrada.size() < 3) {
            System.out.println("Polígono inválido (menos de 3 vértices)");
            return new ArrayList<>();
        }

//        System.out.println("\n==== Recorte de Polígono ====");
//        System.out.println("Polígono original com " + poligonoEntrada.size() + " vértices:");
//        for (Vertice v : poligonoEntrada) {
//            System.out.println(v);
//        }
//        System.out.println("");

        // Aplicar recorte sequencialmente para cada borda
        List<Vertice> resultado = recortarBordaEsq(poligonoEntrada);
//        System.out.println("Polígono após recorte à esquerda:");
//        for (Vertice v : resultado) {
//            System.out.println(v);
//        }
//        System.out.println("");

        if (resultado.isEmpty()) {
            System.out.println("Polígono completamente fora após recorte à esquerda");
            return resultado;
        }

        resultado = recortarBordaDir(resultado);
//        System.out.println("Polígono após recorte à direita:");
//        for (Vertice v : resultado) {
//            System.out.println(v);
//        }
//        System.out.println("");
//        if (resultado.isEmpty()) {
//            System.out.println("Polígono completamente fora após recorte à direita");
//            return resultado;
//        }

        resultado = recortarBordaInf(resultado);
//        System.out.println("Polígono após recorte à inferior:");
//        for (Vertice v : resultado) {
//            System.out.println(v);
//        }
//        System.out.println("");
//        if (resultado.isEmpty()) {
//            System.out.println("Polígono completamente fora após recorte inferior");
//            return resultado;
//        }

        resultado = recortarBordaSup(resultado);
//        System.out.println("Polígono após recorte à superior:");
//        for (Vertice v : resultado) {
//            System.out.println(v);
//        }
//        System.out.println("");
        if (resultado.isEmpty()) {
            System.out.println("Polígono completamente fora após recorte superior");
            return resultado;
        }

//        System.out.println("Polígono recortado com " + resultado.size() + " vértices:");
//        for (Vertice v : resultado) {
//            System.out.println(v);
//        }

        return ordenarVertices(resultado);
    }

    // Implementação do recorte pela borda esquerda (x < umin)
    private List<Vertice> recortarBordaEsq(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            boolean atualDentro = atual.x >= umin;
            boolean proximoDentro = proximo.x >= umin;

            if (atualDentro && proximoDentro) {
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                double u = (umin - atual.x) / (proximo.x - atual.x);
                double y = atual.y + u * (proximo.y - atual.y);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(umin, y, z, r, g, b);
                resultado.add(intersecao);
                System.out.println("Interseção com borda esquerda: " + intersecao);
            } else if (!atualDentro && proximoDentro) {
                double u = (umin - atual.x) / (proximo.x - atual.x);
                double y = atual.y + u * (proximo.y - atual.y);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(umin, y, z, r, g, b);
                resultado.add(intersecao);
                resultado.add(proximo);
                System.out.println("Interseção com borda esquerda: " + intersecao);
            }
        }

        return resultado;
    }

    // Implementação do recorte pela borda direita (x > umax)
    private List<Vertice> recortarBordaDir(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

//        System.out.println("Polígono antes do recorte à direita:");
//        for(Vertice v : vertices){
//            System.out.println(v);
//        }

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            boolean atualDentro = atual.x <= umax;
            boolean proximoDentro = proximo.x <= umax;

            if (atualDentro) {
                if(proximoDentro){
                    resultado.add(proximo);
                } else {
                    double u = (umax - atual.x) / (proximo.x - atual.x);
                    double y = atual.y + u * (proximo.y - atual.y);
                    double z = atual.z + u * (proximo.z - atual.z);
                    double r = atual.r + u * (proximo.r - atual.r);
                    double g = atual.g + u * (proximo.g - atual.g);
                    double b = atual.b + u * (proximo.b - atual.b);

                    Vertice intersecao = new Vertice(umax, y, z, r, g, b);
                    resultado.add(intersecao);
                    System.out.println("Interseção com borda direita: " + intersecao);

                }
            } else if (proximoDentro) {
                System.out.println("pqp2");
                double u = (umax - atual.x) / (proximo.x - atual.x);
                double y = atual.y + u * (proximo.y - atual.y);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(umax, y, z, r, g, b);
                resultado.add(intersecao);
                resultado.add(proximo);
                System.out.println("Interseção com borda direita: " + intersecao);
            }
        }

        return resultado;
    }

    private List<Vertice> ordenarVertices(List<Vertice> vertices) {
        // Ordena primeiro por Y (linhas), depois por X (colunas)
        vertices.sort(Comparator.comparingDouble(Vertice::getY).thenComparingDouble(Vertice::getX));
        return vertices;
    }


    // Implementação do recorte pela borda inferior (y < vmin)
    private List<Vertice> recortarBordaInf(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            // Correção: Borda inferior é y >= vmin (Y aumenta para cima)
            boolean atualDentro = atual.y >= vmin;
            boolean proximoDentro = proximo.y >= vmin;

            if (atualDentro && proximoDentro) {
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                // Cálculo da interseção com vmin (borda inferior)
                double u = (vmin - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                Vertice intersecao = new Vertice(x, vmin, z, 0, 0, 0); // Interpole cores se necessário
                resultado.add(intersecao);
            } else if (!atualDentro && proximoDentro) {
                double u = (vmin - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                Vertice intersecao = new Vertice(x, vmin, z, 0, 0, 0);
                resultado.add(intersecao);
                resultado.add(proximo);
            }
        }
        return resultado;
    }

    // Implementação do recorte pela borda superior (y > vmax)
    private List<Vertice> recortarBordaSup(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            // Correção: Borda superior é y <= vmax (Y aumenta para cima)
            boolean atualDentro = atual.y <= vmax;
            boolean proximoDentro = proximo.y <= vmax;

            if (atualDentro && proximoDentro) {
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                // Cálculo da interseção com vmax (borda superior)
                double u = (vmax - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                Vertice intersecao = new Vertice(x, vmax, z, 0, 0, 0); // Interpole cores se necessário
                resultado.add(intersecao);
            } else if (!atualDentro && proximoDentro) {
                double u = (vmax - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                Vertice intersecao = new Vertice(x, vmax, z, 0, 0, 0);
                resultado.add(intersecao);
                resultado.add(proximo);
            }
        }
        return resultado;
    }
}