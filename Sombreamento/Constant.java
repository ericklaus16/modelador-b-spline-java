package Sombreamento;
import java.awt.Color;

import Geometria.*;
import Processos.*;
import Surface.*;

public class Constant {
    public static void applyConstantShader(Surface superficie) {
        superficie.faces.parallelStream()
            .filter(face -> face.visibilidade > 0)
            .forEach(face -> {
                Point3D o = face.o;
                Settings settings = superficie.settings;
                
                // Calcular iluminação uma vez só por face
                double[] rgbValues = Operacoes.calculateLightingForFace(face, settings, o);
                
                face.corConstante = new Color(
                    (float) rgbValues[0] / 255, 
                    (float) rgbValues[1] / 255, 
                    (float) rgbValues[2] / 255
                );
            });
    }
}
