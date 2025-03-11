import java.util.ArrayList;
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
        double x, y, z;
        double r, g, b;

        public Vertice(double x, double y, double z, double r, double g, double b) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f, %.2f) RGB(%.2f, %.2f, %.2f)",
                    x, y, z, r, g, b);
        }
    }

    public List<Vertice> recortarPoligono(List<Vertice> poligonoEntrada) {
        if (poligonoEntrada == null || poligonoEntrada.size() < 3) {
            System.out.println("Polígono inválido (menos de 3 vértices)");
            return new ArrayList<>();
        }

        // Exibir polígono original
        System.out.println("\n==== Recorte de Polígono ====");
        System.out.println("Polígono original com " + poligonoEntrada.size() + " vértices:");
        for (Vertice v : poligonoEntrada) {
            System.out.println(v);
        }

        // Aplicar recorte sequencialmente para cada borda
        List<Vertice> resultado = recortarPorBordaEsquerda(poligonoEntrada);
        if (resultado.isEmpty()) {
            System.out.println("Polígono completamente fora após recorte à esquerda");
            return resultado;
        }

        resultado = recortarPorBordaDireita(resultado);
        if (resultado.isEmpty()) {
            System.out.println("Polígono completamente fora após recorte à direita");
            return resultado;
        }

        resultado = recortarPorBordaInferior(resultado);
        if (resultado.isEmpty()) {
            System.out.println("Polígono completamente fora após recorte inferior");
            return resultado;
        }

        resultado = recortarPorBordaSuperior(resultado);
        if (resultado.isEmpty()) {
            System.out.println("Polígono completamente fora após recorte superior");
            return resultado;
        }

        // Exibir resultado final
        System.out.println("Polígono recortado com " + resultado.size() + " vértices:");
        for (Vertice v : resultado) {
            System.out.println(v);
        }

        return resultado;
    }

    // Implementação do recorte pela borda esquerda (x = umin)
    private List<Vertice> recortarPorBordaEsquerda(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            // Ponto atual e próximo ponto (circular)
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            // Posição dos pontos em relação à borda (dentro/fora)
            boolean atualDentro = atual.x >= umin;
            boolean proximoDentro = proximo.x >= umin;

            // Implementar regras do algoritmo Sutherland-Hodgman
            if (atualDentro && proximoDentro) {
                // Caso 1: Ambos dentro - adicionar apenas o próximo ponto
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                // Caso 2: Saindo - adicionar ponto de interseção
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
                // Caso 3: Entrando - adicionar ponto de interseção e próximo ponto
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
            // Caso 4: Ambos fora - não adicionar nada
        }

        return resultado;
    }

    // Implementação do recorte pela borda direita (x = umax)
    private List<Vertice> recortarPorBordaDireita(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            boolean atualDentro = atual.x <= umax;
            boolean proximoDentro = proximo.x <= umax;

            if (atualDentro && proximoDentro) {
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                double u = (umax - atual.x) / (proximo.x - atual.x);
                double y = atual.y + u * (proximo.y - atual.y);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(umax, y, z, r, g, b);
                resultado.add(intersecao);
                System.out.println("Interseção com borda direita: " + intersecao);
            } else if (!atualDentro && proximoDentro) {
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

    // Implementação do recorte pela borda inferior (y = vmin)
    private List<Vertice> recortarPorBordaInferior(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            boolean atualDentro = atual.y >= vmin;
            boolean proximoDentro = proximo.y >= vmin;

            if (atualDentro && proximoDentro) {
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                double u = (vmin - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(x, vmin, z, r, g, b);
                resultado.add(intersecao);
                System.out.println("Interseção com borda inferior: " + intersecao);
            } else if (!atualDentro && proximoDentro) {
                double u = (vmin - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(x, vmin, z, r, g, b);
                resultado.add(intersecao);
                resultado.add(proximo);
                System.out.println("Interseção com borda inferior: " + intersecao);
            }
        }

        return resultado;
    }

    // Implementação do recorte pela borda superior (y = vmax)
    private List<Vertice> recortarPorBordaSuperior(List<Vertice> vertices) {
        List<Vertice> resultado = new ArrayList<>();
        int tamanho = vertices.size();

        for (int i = 0; i < tamanho; i++) {
            Vertice atual = vertices.get(i);
            Vertice proximo = vertices.get((i + 1) % tamanho);

            boolean atualDentro = atual.y <= vmax;
            boolean proximoDentro = proximo.y <= vmax;

            if (atualDentro && proximoDentro) {
                resultado.add(proximo);
            } else if (atualDentro && !proximoDentro) {
                double u = (vmax - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(x, vmax, z, r, g, b);
                resultado.add(intersecao);
                System.out.println("Interseção com borda superior: " + intersecao);
            } else if (!atualDentro && proximoDentro) {
                double u = (vmax - atual.y) / (proximo.y - atual.y);
                double x = atual.x + u * (proximo.x - atual.x);
                double z = atual.z + u * (proximo.z - atual.z);
                double r = atual.r + u * (proximo.r - atual.r);
                double g = atual.g + u * (proximo.g - atual.g);
                double b = atual.b + u * (proximo.b - atual.b);

                Vertice intersecao = new Vertice(x, vmax, z, r, g, b);
                resultado.add(intersecao);
                resultado.add(proximo);
                System.out.println("Interseção com borda superior: " + intersecao);
            }
        }

        return resultado;
    }
}