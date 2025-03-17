package Processos;
import java.awt.Graphics;
import java.util.List;

import Geometria.*;
import luminosidade.*;
import Surface.*;

public class Operacoes {
    private Operacoes(){}

    public static void drawLine(Graphics g, Point2D p1, Point2D p2) {
        g.drawLine((int) Math.round(p1.x), (int) Math.round(p1.y), (int) Math.round(p2.x), (int) Math.round(p2.y));
    }

    public static void addUniqueVertex(List<Point3D> vertices , Point3D vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
        }
    }
    
    public static boolean isVertexInFace(Point3D vertex, Face face) {
        return vertex == face.A || vertex == face.B || 
               vertex == face.C || vertex == face.D;
    }

    public static double[] calculateLightingForFace(Face face, Settings settings, Point3D o) {
        double[] rgb = new double[3];
        
        rgb[0] = Lightning.Illuminate(
            face.centroide, face.normal,
            settings.ila, settings.lampada.pos, settings.lampada.ilr,
            settings.kar, settings.kdr, settings.ksr, o
        );
        
        rgb[1] = Lightning.Illuminate(
            face.centroide, face.normal,
            settings.ila, settings.lampada.pos, settings.lampada.ilg,
            settings.kag, settings.kdg, settings.ksg, o
        );
        
        rgb[2] = Lightning.Illuminate(
            face.centroide, face.normal,
            settings.ila, settings.lampada.pos, settings.lampada.ilb,
            settings.kab, settings.kdb, settings.ksb, o
        );
        
        return rgb;
    }
}
