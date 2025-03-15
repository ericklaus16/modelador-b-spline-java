package Sombreamento;
import java.util.List;

import Geometria.*;

import java.awt.Color;
import java.awt.Graphics;

import Processos.*;
import Surface.*;

public class Wireframe {
    public static void applyWireframeShader(Graphics g, List<Point2D> pontos, Surface superficie, int numCols) {
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

    public static void drawWireframeFace(Graphics g, List<Point2D> pontos, Face face, Surface superficie, int numCols) {
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
    
        Operacoes.drawLine(g, A2D, B2D);
        Operacoes.drawLine(g, B2D, C2D);
        Operacoes.drawLine(g, C2D, D2D);
        Operacoes.drawLine(g, D2D, A2D);
        g.setColor(currentColor);
    }
}