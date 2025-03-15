import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Pintor {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    
    // Cache para evitar recriação constante de objetos
    private static final ThreadLocal<List<List<Double>>> scanlineCache = 
        ThreadLocal.withInitial(() -> new ArrayList<>());

    public static void clearCanvas(Graphics g, int width, int height) {
        g.clearRect(0, 0, width, height);
    }

    public static void renderLines(Graphics g, List<Point2D> pontos, Surface superficie) {
        int numCols = superficie.outp[0].length;
        
        // Aplicar o shader apropriado
        applyShader(g, pontos, superficie, numCols);
    }
    
    private static void applyShader(Graphics g, List<Point2D> pontos, Surface superficie, int numCols) {
        switch (superficie.settings.shader) {
            case Constante:
                applyConstantShader(superficie);
                break;
            case Gouraud:
                applyGouraudShader(superficie);
                break;
            case Phong:
                // Implementação do shader Phong (placeholder)
                break;
            case Wireframe:
                applyWireframeShader(g, pontos, superficie, numCols);
                break;
        }
    }

    private static void applyConstantShader(Surface superficie) {
        superficie.faces.parallelStream()
            .filter(face -> face.visibilidade > 0)
            .forEach(face -> {
                Point3D o = face.o;
                Settings settings = superficie.settings;
                
                // Calcular iluminação uma vez só por face
                double[] rgbValues = calculateLightingForFace(face, settings, o);
                
                face.corConstante = new Color(
                    (float) rgbValues[0] / 255, 
                    (float) rgbValues[1] / 255, 
                    (float) rgbValues[2] / 255
                );
            });
    }
    
    // Método auxiliar para calcular iluminação
    private static double[] calculateLightingForFace(Face face, Settings settings, Point3D o) {
        double[] rgb = new double[3];
        
        rgb[0] = Lightning.Illuminate(
            face.centroide, face.normal,
            settings.ila, settings.lampada.pos, settings.lampada.il,
            settings.kar, settings.kdr, settings.ksr, o
        );
        
        rgb[1] = Lightning.Illuminate(
            face.centroide, face.normal,
            settings.ila, settings.lampada.pos, settings.lampada.il,
            settings.kag, settings.kdg, settings.ksg, o
        );
        
        rgb[2] = Lightning.Illuminate(
            face.centroide, face.normal,
            settings.ila, settings.lampada.pos, settings.lampada.il,
            settings.kab, settings.kdb, settings.ksb, o
        );
        
        return rgb;
    }
    
    // Shader Gouraud otimizado
    private static void applyGouraudShader(Surface superficie) {
        // Determinar vértices visíveis em uma única passagem
        List<Point3D> verticesVisiveis = Collections.synchronizedList(new ArrayList<>());
        
        // Criar mapa de vértices -> normais médias usando stream paralelo
        superficie.faces.parallelStream()
            .filter(face -> face.visibilidade > 0)
            .forEach(face -> {
                synchronized(verticesVisiveis) {
                    addUniqueVertex(verticesVisiveis, face.A);
                    addUniqueVertex(verticesVisiveis, face.B);
                    addUniqueVertex(verticesVisiveis, face.C);
                    addUniqueVertex(verticesVisiveis, face.D);
                }
            });
        
        // Calcular normais médias para cada vértice (paralelizado)
        verticesVisiveis.parallelStream().forEach(vertex -> {
            Point3D normalMedia = new Point3D(0, 0, 0);
            AtomicInteger faceCount = new AtomicInteger(0);
            
            superficie.faces.stream()
                .filter(face -> face.visibilidade > 0)
                .filter(face -> isVertexInFace(vertex, face))
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
                        superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.il,
                        superficie.settings.kar, superficie.settings.kdr, superficie.settings.ksr, s);
                
                double gr = Lightning.Illuminate(vertex, normalizado,
                        superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.il,
                        superficie.settings.kag, superficie.settings.kdg, superficie.settings.ksg, s);
                
                double b = Lightning.Illuminate(vertex, normalizado,
                        superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.il,
                        superficie.settings.kab, superficie.settings.kdb, superficie.settings.ksb, s);
                
                Color cor = new Color((float) r / 255, (float) gr / 255, (float) b / 255);
                superficie.vertexColors.put(vertex, cor);
            }
        });
    }

    private static void addUniqueVertex(List<Point3D> vertices, Point3D vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
        }
    }

    private static boolean isVertexInFace(Point3D vertex, Face face) {
        return vertex == face.A || vertex == face.B || 
               vertex == face.C || vertex == face.D;
    }

    private static void applyWireframeShader(Graphics g, List<Point2D> pontos, Surface superficie, int numCols) {
        // Ordenar faces por profundidade (usando paralelSort se a lista for grande)
        if (superficie.faces.size() > 1000) {
            superficie.faces.parallelStream().sorted((f1, f2) -> 
                Double.compare(f2.d, f1.d)).forEachOrdered(face -> {
                    drawWireframeFace(g, pontos, face, superficie, numCols);
                });
        } else {
            superficie.faces.sort((f1, f2) -> Double.compare(f2.d, f1.d));
            
            // Desenhar faces em lotes para reduzir chamadas de desenho
            g.setColor(superficie.settings.visibleEdgeColor);
            superficie.faces.stream()
                .filter(face -> face.visibilidade > 0)
                .forEach(face -> drawWireframeFace(g, pontos, face, superficie, numCols));
                
            g.setColor(superficie.settings.notVisibleEdgeColor);
            superficie.faces.stream()
                .filter(face -> face.visibilidade <= 0)
                .forEach(face -> drawWireframeFace(g, pontos, face, superficie, numCols));
        }
    }
    
    private static void drawWireframeFace(Graphics g, List<Point2D> pontos, Face face, Surface superficie, int numCols) {
        Color currentColor = g.getColor();
        if (face.visibilidade > 0) {
            g.setColor(superficie.settings.visibleEdgeColor);
        } else {
            g.setColor(superficie.settings.notVisibleEdgeColor);
        }
        
        Point2D A2D = pontos.get(face.i * numCols + face.j);
        Point2D B2D = pontos.get(face.i * numCols + (face.j + 1));
        Point2D C2D = pontos.get((face.i + 1) * numCols + (face.j + 1));
        Point2D D2D = pontos.get((face.i + 1) * numCols + face.j);
        
        drawLine(g, A2D, B2D);
        drawLine(g, B2D, C2D);
        drawLine(g, C2D, D2D);
        drawLine(g, D2D, A2D);
        
        g.setColor(currentColor);
    }

    private static void drawLine(Graphics g, Point2D p1, Point2D p2) {
        g.drawLine((int) Math.round(p1.x), (int) Math.round(p1.y), (int) Math.round(p2.x), (int) Math.round(p2.y));
    }

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

    public static void pintor(Graphics g, List<Point2D> pontos, Surface superficie) {
        renderLines(g, pontos, superficie);

        if(superficie.settings.shader == Shader.Wireframe){
            // Ordenar as faces por profundidade (da mais distante para a mais próxima)
            superficie.faces.sort((f1, f2) -> Double.compare(f2.d, f1.d));
            
            // Preencher apenas as faces visíveis com branco
            for (Face face : superficie.faces) {
                if (face.visibilidade > 0) {  // Preencher apenas faces visíveis
                    List<Point2D> facePontos = List.of(
                            pontos.get(face.i * superficie.outp[0].length + face.j),
                            pontos.get(face.i * superficie.outp[0].length + (face.j + 1)),
                            pontos.get((face.i + 1) * superficie.outp[0].length + (face.j + 1)),
                            pontos.get((face.i + 1) * superficie.outp[0].length + face.j)
                    );
        
                    polyFill(g, Color.WHITE, facePontos);
                }
            }
            
            // Redesenhar as linhas após o preenchimento
            applyWireframeShader(g, pontos, superficie, superficie.outp[0].length);
        } else if(superficie.settings.shader == Shader.Constante){
            for (Face face : superficie.faces) {
                List<Point2D> facePontos = List.of(
                        pontos.get(face.i * superficie.outp[0].length + face.j),
                        pontos.get(face.i * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + face.j)
                );

                polyFill(g, face.corConstante, facePontos);
            }
        } else if(superficie.settings.shader == Shader.Gouraud){
            for (Face face : superficie.faces) {
                // Obter os pontos 2D projetados
                if(face.visibilidade <= 0) continue;
                List<Point2D> facePontos = List.of(
                        pontos.get(face.i * superficie.outp[0].length + face.j),
                        pontos.get(face.i * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + face.j)
                );

                // Obter as cores dos vértices
                List<Color> faceCores = List.of(
                        superficie.vertexColors.get(face.A),
                        superficie.vertexColors.get(face.B),
                        superficie.vertexColors.get(face.C),
                        superficie.vertexColors.get(face.D)
                );

                // Chamar polyFill com interpolação de cores
                polyFillGouraud(g, facePontos, faceCores);
            }
        }else if(superficie.settings.shader == Shader.Phong){
            for (Face face : superficie.faces) {
                if (face.visibilidade <= 0) continue;
                List<Point2D> facePontos = List.of(
                        pontos.get(face.i * superficie.outp[0].length + face.j),
                        pontos.get(face.i * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + face.j)
                );
    
                // Implementação do shader Phong
                // Obter as cores dos vértices usando iluminação Phong
                List<Color> faceCores = List.of(
                        calculatePhongColor(face.A, superficie),
                        calculatePhongColor(face.B, superficie),
                        calculatePhongColor(face.C, superficie),
                        calculatePhongColor(face.D, superficie)
                );
    
                polyFillGouraud(g, facePontos, faceCores);
            }
        }

        // Zbuffer
    }

    private static Color calculatePhongColor(Point3D vertex, Surface superficie) {
        Point3D normal = Point3D.getNormalizedVector(vertex);
        Point3D viewDir = Point3D.getNormalizedVector(Point3D.subtract(superficie.settings.cameraPos, vertex));

        double r = Lightning.Illuminate(vertex, normal,
                superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.il,
                superficie.settings.kar, superficie.settings.kdr, superficie.settings.ksr, viewDir);

        double g = Lightning.Illuminate(vertex, normal,
                superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.il,
                superficie.settings.kag, superficie.settings.kdg, superficie.settings.ksg, viewDir);

        double b = Lightning.Illuminate(vertex, normal,
                superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.il,
                superficie.settings.kab, superficie.settings.kdb, superficie.settings.ksb, viewDir);

        return new Color((float) r / 255, (float) g / 255, (float) b / 255);
    }
}