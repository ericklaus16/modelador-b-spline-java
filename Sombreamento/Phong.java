
package Sombreamento;
import java.awt.Color;

import Geometria.*;
import Surface.*;
import luminosidade.*;
public class Phong {
        public static Color calculatePhongColor(Point3D vertex, Surface superficie) {
            Point3D normal = Point3D.getNormalizedVector(vertex);
            Point3D viewDir = Point3D.getNormalizedVector(Point3D.subtract(superficie.settings.cameraPos, vertex));

            double r = Lightning.Illuminate(vertex, normal,
                    superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.ilr,
                    superficie.settings.kar, superficie.settings.kdr, superficie.settings.ksr, viewDir);

            double g = Lightning.Illuminate(vertex, normal,
                    superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.ilg,
                    superficie.settings.kag, superficie.settings.kdg, superficie.settings.ksg, viewDir);

            double b = Lightning.Illuminate(vertex, normal,
                    superficie.settings.ila, superficie.settings.lampada.pos, superficie.settings.lampada.ilb,
                    superficie.settings.kab, superficie.settings.kdb, superficie.settings.ksb, viewDir);

            return new Color((float) r / 255, (float) g / 255, (float) b / 255);
        }
        
}
