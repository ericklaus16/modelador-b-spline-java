package Pipe;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Geometria.*;

public class Fillpolly {
    public static void polyFill(Graphics g, Color cor, List<Point2D> pontos) {
        int yMin = (int) Math.floor(pontos.stream().mapToDouble(p -> p.y).min().orElse(0));
        int yMax = (int) Math.ceil(pontos.stream().mapToDouble(p -> p.y).max().orElse(0));

        List<List<Double>> scanlines = new ArrayList<>();
        for (int i = 0; i < (yMax - yMin + 1); i++) {
            scanlines.add(new ArrayList<>());
        }

        // Construir a tabela de arestas ativas (Active Edge Table)
        for (int i = 0; i < pontos.size(); i++) {
            Point2D pontoAtual = pontos.get(i);
            Point2D proximoPonto = pontos.get((i + 1) % pontos.size());

            // Ignorar arestas horizontais
            if (pontoAtual.y == proximoPonto.y) continue;

            // Garantir que o ponto atual seja o de menor y
            if (pontoAtual.y > proximoPonto.y) {
                Point2D temp = pontoAtual;
                pontoAtual = proximoPonto;
                proximoPonto = temp;
            }

            double deltaX = (proximoPonto.x - pontoAtual.x) / (proximoPonto.y - pontoAtual.y); // Coeficiente angular
            double xInterseccao = pontoAtual.x;

            // Calcular as interseções de ymin até ymax
            for (int y = (int) Math.ceil(pontoAtual.y); y < Math.ceil(proximoPonto.y); y++) {
                int scanlineIndex = y - yMin;
                if (scanlineIndex < 0 || scanlineIndex >= scanlines.size()) {
                    System.err.println("Invalid scanlineIndex: " + scanlineIndex);
                    continue;
                }
                scanlines.get(scanlineIndex).add(xInterseccao);
                xInterseccao += deltaX; // Incrementar a interseção usando Δx
            }
        }

        g.setColor(cor);

        for (int y = yMin; y <= yMax; y++) {
            List<Double> intersecoes = scanlines.get(y - yMin);

            // Ordenar interseções
            intersecoes.sort(Double::compare);

            // Desenhar as linhas horizontais entre pares de interseções
            for (int i = 0; i < intersecoes.size(); i += 2) {
                int xIni = (int) Math.ceil(intersecoes.get(i));
                int xFim = (int) Math.floor(intersecoes.get(i + 1));

                // Desenhar o preenchimento no canvas
                g.fillRect(xIni, y, xFim - xIni, 1);
            }
        }
    }
}
