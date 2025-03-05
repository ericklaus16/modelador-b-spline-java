import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Pintor {

    public static void clearCanvas(Graphics g, int width, int height) {
        g.clearRect(0, 0, width, height);
    }

    public static void renderLines(Graphics g, List<Point2D> pontos, Surface superficie) {
        int numRows = superficie.outp.length;
        int numCols = superficie.outp[0].length;

        for (int i = 0; i < numRows - 1; i++) {
            for (int j = 0; j < numCols - 1; j++) {
                Point3D A = superficie.outp[i][j];
                Point3D B = superficie.outp[i][j + 1];
                Point3D C = superficie.outp[i + 1][j + 1];
                Point3D D = superficie.outp[i + 1][j];

                Point3D centroide = new Point3D(
                    (A.x + B.x + C.x + D.x) / 4,
                    (A.y + B.y + C.y + D.y) / 4,
                    (A.z + B.z + C.z + D.z) / 4,
                    1
                );

                double h = Math.abs(centroide.x) / Math.cos(Math.atan(Math.abs(centroide.y) / Math.abs(centroide.x)));
                double d = Math.abs(centroide.z) / Math.cos(Math.atan(h / Math.abs(centroide.z)));

                superficie.faces.add(new Face(A, B, C, D, d, i, j, superficie.settings.cameraPos));
                Point3D o = superficie.faces.getLast().o;

                if (superficie.settings.shader == Shader.Constante){
                    Settings settings = superficie.settings;
                    double r = Lightning.Illuminate(superficie.faces.getLast(), settings.ila,
                            settings.lampada.pos, settings.lampada.il, settings.kar,
                            settings.kdr, settings.ksr, o);

                    double gr = Lightning.Illuminate(superficie.faces.getLast(), settings.ila,
                            settings.lampada.pos, settings.lampada.il, settings.kag,
                            settings.kdg, settings.ksg, o);

                    double b = Lightning.Illuminate(superficie.faces.getLast(), settings.ila,
                            settings.lampada.pos, settings.lampada.il, settings.kab,
                            settings.kdb, settings.ksb, o);

                    System.out.println("R: " + r);
                    System.out.println("G: " + gr);
                    System.out.println("B: " + b);

                    // A cor será usada posteriormente para pintura
                    superficie.faces.getLast().corConstante = new Color((float) r / 255, (float) gr / 255, (float) b / 255);
                } else if (superficie.settings.shader == Shader.Gouraud){
                    // Vetores normais médios unitários nos vértices

                } else if (superficie.settings.shader == Shader.Phong){
                    // Vetores normais médios unitários nos vértices
                    // Calcular a iluminação total nos vértices

                } else if (superficie.settings.shader == Shader.Wireframe){
                    // Ordenar faces por profundidade (fundo para frente)
                    superficie.faces.sort((f1, f2) -> Double.compare(f2.d, f1.d));

                    // Desenhar as faces na ordem correta
                    for (Face face : superficie.faces) {
                        Point3D verticeA = face.A;
                        Point3D verticeB = face.B;
                        Point3D verticeC = face.C;
                        Point3D verticeD = face.D;

                        // Calcular visibilidade
                        double visibility = Visibility.VisibilidadeNormal(superficie.settings.cameraPos, verticeD, verticeC, verticeB, verticeA);

                        Color edgeColor = visibility > 0 ? superficie.settings.visibleEdgeColor : superficie.settings.notVisibleEdgeColor;
                        g.setColor(edgeColor);

                        Point2D A2D = pontos.get(face.i * numCols + face.j);
                        Point2D B2D = pontos.get(face.i * numCols + (face.j + 1));
                        Point2D C2D = pontos.get((face.i + 1) * numCols + (face.j + 1));
                        Point2D D2D = pontos.get((face.i + 1) * numCols + face.j);

                        drawLine(g, A2D, B2D);
                        drawLine(g, B2D, C2D);
                        drawLine(g, C2D, D2D);
                        drawLine(g, D2D, A2D);
                    }
                }
            }
        }
    }

    private static void drawLine(Graphics g, Point2D p1, Point2D p2) {
        g.drawLine((int) Math.round(p1.x), (int) Math.round(p1.y), (int) Math.round(p2.x), (int) Math.round(p2.y));
    }

    public static void polyFill(Graphics g, List<Point2D> pontos) {
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

        g.setColor(new Color(1f, 1f, 1f, 0f));
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

    public static void pintor(Graphics g, List<Point2D> pontos, Surface superficie) {
        renderLines(g, pontos, superficie);
        polyFill(g, pontos);
        // Zbuffer
    }
}
