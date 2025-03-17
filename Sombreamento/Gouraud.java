package Sombreamento;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import Geometria.*;
import Surface.*;
import Processos.*;
import luminosidade.*;

public class Gouraud {
    public static void applyGouraudShader(Surface superficie) {
    // Determinar vértices visíveis em uma única passagem
        List<Point3D> verticesVisiveis = Collections.synchronizedList(new ArrayList<>());
        
        // Criar mapa de vértices -> normais médias usando stream paralelo
        superficie.faces.parallelStream()
            .filter(face -> face.visibilidade > 0)
            .forEach(face -> {
                synchronized(verticesVisiveis) {
                    Operacoes.addUniqueVertex(verticesVisiveis, face.A);
                    Operacoes.addUniqueVertex(verticesVisiveis, face.B);
                    Operacoes.addUniqueVertex(verticesVisiveis, face.C);
                    Operacoes.addUniqueVertex(verticesVisiveis, face.D);
                }
            });
        
        // Calcular normais médias para cada vértice (paralelizado)
        verticesVisiveis.parallelStream().forEach(vertex -> {
            Point3D normalMedia = new Point3D(0, 0, 0);
            AtomicInteger faceCount = new AtomicInteger(0);
            
            superficie.faces.stream()
                .filter(face -> face.visibilidade > 0)
                .filter(face -> Operacoes.isVertexInFace(vertex, face))
                .forEach(face -> {
                    synchronized(normalMedia) {
                        normalMedia.x += face.normal.x;
                        normalMedia.y += face.normal.y;
                        normalMedia.z += face.normal.z;
                        faceCount.incrementAndGet();
                    }
                });
            
            // Normalizar o vetor médio
            if (faceCount.get() > 0) {
                Point3D normalizado = Point3D.getNormalizedVector(normalMedia);
                
                // Calcular iluminação para este vértice
                Point3D s = Point3D.subtract(superficie.settings.cameraPos, vertex);
                s = Point3D.getNormalizedVector(s);
                
                double r = Lightning.Illuminate(vertex, normalizado,
                        superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.ilr,
                        superficie.settings.kar, superficie.settings.kdr, superficie.settings.ksr, s);
                
                double gr = Lightning.Illuminate(vertex, normalizado,
                        superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.ilg,
                        superficie.settings.kag, superficie.settings.kdg, superficie.settings.ksg, s);
                
                double b = Lightning.Illuminate(vertex, normalizado,
                        superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.ilb,
                        superficie.settings.kab, superficie.settings.kdb, superficie.settings.ksb, s);
                
                Color cor = new Color((float) r / 255, (float) gr / 255, (float) b / 255);
                superficie.vertexColors.put(vertex, cor);
            }
        });
    }
    
    public static void polyFillGouraud(Graphics g, List<Point2D> pontos, List<Color> cores) {
        int yMin = (int) Math.floor(pontos.stream().mapToDouble(p -> p.y).min().orElse(0));
        int yMax = (int) Math.ceil(pontos.stream().mapToDouble(p -> p.y).max().orElse(0));

        List<List<Double>> scanlines = new ArrayList<>();
        List<List<Color>> scanlineColors = new ArrayList<>();

        for (int i = 0; i < (yMax - yMin + 1); i++) {
            scanlines.add(new ArrayList<>());
            scanlineColors.add(new ArrayList<>());
        }

        // Construção da tabela de arestas ativas (Active Edge Table)
        for (int i = 0; i < pontos.size(); i++) {
            Point2D p1 = pontos.get(i);
            Point2D p2 = pontos.get((i + 1) % pontos.size());
            Color c1 = cores.get(i);
            Color c2 = cores.get((i + 1) % cores.size());

            if (p1.y == p2.y) continue; // Ignorar arestas horizontais

            // Garantir que p1 tenha menor y
            if (p1.y > p2.y) {
                Point2D tempP = p1;
                p1 = p2;
                p2 = tempP;

                Color tempC = c1;
                c1 = c2;
                c2 = tempC;
            }

            double deltaX = (p2.x - p1.x) / (p2.y - p1.y);
            double xInterseccao = p1.x;

            float r1 = c1.getRed(), g1 = c1.getGreen(), b1 = c1.getBlue();
            float r2 = c2.getRed(), g2 = c2.getGreen(), b2 = c2.getBlue();

            for (int y = (int) Math.ceil(p1.y); y < Math.ceil(p2.y); y++) {
                int scanlineIndex = y - yMin;
                float fator = (float) (y - p1.y) / (float) (p2.y - p1.y);

                // Interpolação de cores
                int r = (int) ((1 - fator) * r1 + fator * r2);
                int gC = (int) ((1 - fator) * g1 + fator * g2);
                int b = (int) ((1 - fator) * b1 + fator * b2);

                scanlines.get(scanlineIndex).add(xInterseccao);
                scanlineColors.get(scanlineIndex).add(new Color(r, gC, b));
                xInterseccao += deltaX;
            }
        }

        // Preenchendo os pixels com cores interpoladas
        for (int y = yMin; y <= yMax; y++) {
            List<Double> intersecoes = scanlines.get(y - yMin);
            List<Color> intersecoesCores = scanlineColors.get(y - yMin);

            intersecoes.sort(Double::compare);
            intersecoesCores.sort((c1, c2) -> Double.compare(intersecoes.get(intersecoesCores.indexOf(c1)), intersecoes.get(intersecoesCores.indexOf(c2))));

            for (int i = 0; i < intersecoes.size() - 1; i += 2) {
                int xIni = (int) Math.ceil(intersecoes.get(i));
                int xFim = (int) Math.floor(intersecoes.get(i + 1));

                Color cIni = intersecoesCores.get(i);
                Color cFim = intersecoesCores.get(i + 1);

                for (int x = xIni; x <= xFim; x++) {
                    float fator = (float) (x - xIni) / (xFim - xIni);

                    int r = (int) ((1 - fator) * cIni.getRed() + fator * cFim.getRed());
                    int gC = (int) ((1 - fator) * cIni.getGreen() + fator * cFim.getGreen());
                    int b = (int) ((1 - fator) * cIni.getBlue() + fator * cFim.getBlue());

                    g.setColor(new Color(r, gC, b));
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
    }
}

