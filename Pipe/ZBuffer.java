package Pipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Geometria.*;

public class ZBuffer {
    public static List<List<Double>> varrerArestas(List<Aresta> arestas, List<List<Double>> colorsSurface) {
        Optional<Aresta> arestaMenorY = arestas.stream()
            .min((a1, a2) -> Integer.compare(a1.yMin, a2.yMin));
        Optional<Aresta> arestaMaiorY = arestas.stream()
            .max((a1, a2) -> Integer.compare(a1.yMax, a2.yMax));

        int yMin = arestaMenorY.get().yMin;
        int yMax = arestaMaiorY.get().yMax;
        int height = yMax - yMin + 1;

        arestas = arestas.stream()
        .sorted((a1, a2) -> {
            int cmpY = Integer.compare(a1.yMin, a2.yMin);
            if (cmpY == 0) {  // Se yMin for igual, desempate pelo x inicial
                int cmpX = Double.compare(a1.origem.x, a2.origem.x);
                if (cmpX == 0) {  // Se x for igual, desempate pelo Tx (menor Tx vem primeiro)
                    return Double.compare(a1.tx, a2.tx);
                }
                return cmpX;
            }
            return cmpY;
        })
        .toList();

        // Se após ordenado tiverem arestas com o mesmo x, ordenar só elas para que a que tiver o menor tX fique na frente (menor ao maior)

        List<List<Double>> scanlines = new ArrayList<>();
        List<List<Double>> zBuffer = new ArrayList<>();
        List<List<Double>> colors = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            scanlines.add(new ArrayList<>());
            zBuffer.add(new ArrayList<>());
            colors.add(new ArrayList<>());
        }
        
        for (int i = yMin; i < yMax; i++) {
            final int currentY = i;
            
            List<Aresta> arestasAtivas = arestas.stream()
            .filter(a -> a.yMin <= currentY && a.yMax > currentY)
            .toList();
            
            double currentX1 = (arestasAtivas.get(0).x);
            double currentX2 = (arestasAtivas.get(1).x);
            double currentZ1 = arestasAtivas.get(0).z;
            double currentZ2 = arestasAtivas.get(1).z;

            double numeradorTZ = currentZ2 - currentZ1;
            double denominadorTZ = currentX2 - currentX1;

            double TIt1 = (arestasAtivas.get(0).it);
            double TIt2 = (arestasAtivas.get(1).it);

            double numeradorTIt = TIt1 - TIt2;
            double denominadorTit = currentX1 - currentX2;

            double tz;
            double tit;

            if (denominadorTZ == 0 || numeradorTZ == 0) {
                tz = 0;
            } else {
                tz = numeradorTZ / denominadorTZ;
            }

            if (denominadorTit == 0 || numeradorTIt == 0){
                tit = 0;
            } else {
                tit = numeradorTIt / denominadorTit;
            }

            // System.out.println(arestasAtivas.get(0).x + " " + arestasAtivas.get(1).x);
            // System.out.println(arestasAtivas.get(0).z + " " + arestasAtivas.get(1).z);
            
            // System.out.println("Xi: " + currentX1 + " Xf: " + currentX2 + " Tz: " + tz);
            int xi = (int) Math.ceil(currentX1);
            int xf = (int) Math.floor(currentX2);

            zBuffer.get(i - yMin).add(currentZ1 + (Math.ceil(currentX1) - arestasAtivas.get(0).x) * tz);
            colors.get(i - yMin).add(arestasAtivas.get(1).it + (Math.ceil(currentX1) - arestasAtivas.get(1).x) * tit);

            for (int j = 0; j < Math.floor(currentX2) - Math.ceil(currentX1); j++) {
                double ultimoZ = zBuffer.get(i - yMin).get(zBuffer.get(i - yMin).size() - 1);
                zBuffer.get(i - yMin).add(ultimoZ + tz);

                double ultimoC = colors.get(i - yMin).get(colors.get(i - yMin).size() - 1);
                colors.get(i - yMin).add(ultimoC + tit);
                // System.out.println(ultimo + tz);
            }
            // System.out.println(currentZ1 + " + " + (Math.ceil(currentX1) - arestasAtivas.get(0).x) + " * " + tz);
            System.out.println(zBuffer.get(i - yMin));
            System.out.println(colors.get(i - yMin));
            System.out.println("");

            arestasAtivas.get(0).x += arestasAtivas.get(0).tx;
            arestasAtivas.get(0).z += arestasAtivas.get(0).tz;
            arestasAtivas.get(0).it += arestasAtivas.get(0).tit;

            arestasAtivas.get(1).x += arestasAtivas.get(1).tx;
            arestasAtivas.get(1).z += arestasAtivas.get(1).tz;
            arestasAtivas.get(1).it += arestasAtivas.get(1).tit;
        }

        colorsSurface = colors;
        return zBuffer;
    }
}