package Main;
import java.util.ArrayList;
import java.util.List;

import luminosidade.*;
import Pipe.*;
import Geometria.*;

public class Test {
    Test(){};

    public static void main(String[] args) {
		// Test.Pipeline();
        // Test.VisibilidadePorNormal();
        Test.Buffer();
        // Test.Lightning();
    }

	public static void Pipeline() {
		System.out.println("=========TESTE PIPELINE=========");
		Point3D vrp = new Point3D(25, 15, 80);
		Point3D p = new Point3D(20, 10, 25);
        Viewport viewport = new Viewport(0, 319, 0, 239, -20, 20, -15, 15);
		Point3D vertex = new Point3D(21.2, 0.7, 42.3, 1);
		Point2D novoPonto = Pipeline.mapearPonto(vertex, p, vrp, viewport);
		System.out.println(novoPonto.x + " " + novoPonto.y);
	}

	public static void VisibilidadePorNormal() {
		Point3D a = new Point3D(-10, -20, 10);
		Point3D b = new Point3D(10, -20, 10);
		Point3D c = new Point3D(7, 20, 10);
		Point3D d = new Point3D(-7, 20, 10);

		Face face = new Face(d, c, b, a, 1, 0, 0, new Point3D(30, 50, 300));

		System.err.println("=========TESTE VISIBILIDADE POR NORMAL=========");
		Point3D p1 = new Point3D(21.2, 0.7, 42.3);
		Point3D p2 = new Point3D(34.1, 3.4, 27.2);
        Point3D p3 = new Point3D(18.8, 5.6, 14.6);
        Point3D p4 = new Point3D(5.9, 2.9, 29.7);
        Point3D p5 = new Point3D(20, 20.9, 31.6);
        Point3D p6 = new Point3D(0, 0, 0, 0);
		
        Point3D vrp = new Point3D(30, 50, 300);
		Visibility.VisibilidadeNormal(vrp, d, c, b, a);
	}

	public static void Buffer() {
		Point3D a = new Point3D(93, 251, -22.807);
		Point3D b = new Point3D(198, 241, -20.129);
		Point3D d = new Point3D(85, 192, -32.570);
		Point3D e = new Point3D(125, 107, -21.815);

		Point3D bLinha = new Point3D(319.000, 160.774, -51.524);
		bLinha.it = 133.160;

		Point3D bLinhaLinha = new Point3D(190.427, 0.000, -48.792);
		bLinhaLinha.it = 118.411;

		Point3D eLinhaLinha = new Point3D(149.864, 0.000, -46.762);
		eLinhaLinha.it = 105.145;

		Point3D eLinha = new Point3D(151.303, 239.000, -41.331);
		eLinha.it = 65.034;

		Point3D aLinhaLinha = new Point3D(319.000, 239.000, -49.722);
		aLinhaLinha.it = 119.878;

		Aresta aresta = new Aresta(a, b);
		Aresta aresta2 = new Aresta(b, e);
		Aresta aresta3 = new Aresta(e, a);

		Aresta aresta4 = new Aresta(bLinha, bLinhaLinha);
		Aresta aresta5 = new Aresta(eLinhaLinha, eLinha);
		Aresta aresta6 = new Aresta(aLinhaLinha, bLinha);

		System.out.println("=========TESTE BUFFER ARESTA=========");
		List<Aresta> arestas = new ArrayList<>();
		arestas.add(aresta4);
		arestas.add(aresta5);
		arestas.add(aresta6);
		ZBuffer.varrerArestas(arestas);
	}

	public static void Lightning() {
		Point3D a = new Point3D(151.914, 340.497, -39.024);
		Point3D b = new Point3D(369.403, 223.801, -52.594);
		Point3D c = new Point3D(149.556, -51.107, -47.924);
		Point3D d = new Point3D(0, 0, 0);
		Face face = new Face(a, b, c, d, 0, 0, 0, new Point3D(25, 15, 80));
		face.normal = new Point3D(0.669, 0.378, 0.639);

		double ila = 120;
		Point3D l = new Point3D(70, 20, 35);
		double il = 150;
		double ka = 0.4;
		double kd = 0.7;
		double ks = 0.5;
		Point3D s = new Point3D(-0.002, 0.143, 0.990);
		System.out.println("=========TESTE LIGHTNING=========");
		System.out.println(Lightning.Illuminate(face.centroide, face.normal, ila, l, il, ka, kd, ks, s));
	}
	
	public static void Recorte() {
		// Cut viewport = new Cut(100, 400, 80, 380);
		// Cut.Vertice a = new Cut.Vertice(0, 250, -30, 200, 120, 30);
		// Cut.Vertice b = new Cut.Vertice(250, 430, -65, 40, 250, 100);
		// Cut.Vertice c = new Cut.Vertice(480, 0, -90, 100, 10, 190);

		// List<Cut.Vertice> poligono = new ArrayList<>();
		// poligono.add(a);
		// poligono.add(b);
		// poligono.add(c);

		// System.out.println("=========TESTE RECORTE=========");
		// List<Cut.Vertice> resultado = viewport.recortarPoligono(poligono);

		// System.out.println("\nResultado final tem " + resultado.size() + " vértices");
		// for (Cut.Vertice v : resultado) {
		// 	System.out.println(v);
		// }
	}
}