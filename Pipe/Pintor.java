package Pipe;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Geometria.*;
import Sombreamento.*;

import Surface.*;

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
                Constant.applyConstantShader(superficie);
                break;
            case Gouraud:
                Gouraud.applyGouraudShader(superficie);
                break;
            case Phong:
                // Implementação do shader Phong (placeholder)
                break;
            case Wireframe:
                Wireframe.applyWireframeShader(g, pontos, superficie, numCols);
                break;
        }
    }

    public static void pintor(Graphics g, List<Point2D> pontos, Surface superficie) {
        //if(superficie.z > superficie.settings.far || superficie.z < superficie.settings.near) return;
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
        
                    Fillpolly.polyFill(g, Color.WHITE, facePontos);
                }
            }
            
            // Redesenhar as linhas após o preenchimento
            Wireframe.applyWireframeShader(g, pontos, superficie, superficie.outp[0].length);
        } else if(superficie.settings.shader == Shader.Constante){
            for (Face face : superficie.faces) {
                List<Point2D> facePontos = List.of(
                        pontos.get(face.i * superficie.outp[0].length + face.j),
                        pontos.get(face.i * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + (face.j + 1)),
                        pontos.get((face.i + 1) * superficie.outp[0].length + face.j)
                );

                Fillpolly.polyFill(g, face.corConstante, facePontos);
            }
        } else if (superficie.settings.shader == Shader.Gouraud) {
            Gouraud.applyGouraudShader(superficie); // Forçar aplicação do shader

            for (Face face : superficie.faces) {
                if (face.visibilidade <= 0) continue;

                // Verificar índices para evitar acesso inválido
                int indexA = face.i * superficie.outp[0].length + face.j;
                int indexB = face.i * superficie.outp[0].length + (face.j + 1);
                int indexC = (face.i + 1) * superficie.outp[0].length + (face.j + 1);
                int indexD = (face.i + 1) * superficie.outp[0].length + face.j;

                // Validar se os índices estão dentro do tamanho da lista 'pontos'
                if (indexA >= pontos.size() || indexB >= pontos.size() || indexC >= pontos.size() || indexD >= pontos.size()) {
                    continue; // Ou tratar o erro adequadamente
                }

                List<Point2D> facePontos = List.of(
                        pontos.get(indexA),
                        pontos.get(indexB),
                        pontos.get(indexC),
                        pontos.get(indexD)
                );

                // Verificar cores dos vértices (evitar null)
                List<Color> faceCores = Arrays.asList(
                        superficie.vertexColors.getOrDefault(face.A, Color.BLACK),
                        superficie.vertexColors.getOrDefault(face.B, Color.BLACK),
                        superficie.vertexColors.getOrDefault(face.C, Color.BLACK),
                        superficie.vertexColors.getOrDefault(face.D, Color.BLACK)
                );

                Gouraud.polyFillGouraud(g, facePontos, faceCores);
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
                        Phong.calculatePhongColor(face.A, superficie),
                        Phong.calculatePhongColor(face.B, superficie),
                        Phong.calculatePhongColor(face.C, superficie),
                        Phong.calculatePhongColor(face.D, superficie)
                );
    
                Gouraud.polyFillGouraud(g, facePontos, faceCores);
            }
        }

        // Zbuffer
    } 
}