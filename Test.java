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
        Viewport viewport = new Viewport(0, 319, 0, 239, -8, 8, -6, 6);
		Point3D vertex = new Point3D(21.2, 0.7, 42.3, 1);
		Point2D novoPonto = Pipeline.mapearPonto(vertex, p, vrp, viewport);
		System.out.println(novoPonto);
	}

	public static void VisibilidadePorNormal() {
		System.err.println("=========TESTE VISIBILIDADE POR NORMAL=========");
		Point3D p1 = new Point3D(21.2, 0.7, 42.3);
		Point3D p2 = new Point3D(34.1, 3.4, 27.2);
        Point3D p3 = new Point3D(18.8, 5.6, 14.6);
        Point3D p4 = new Point3D(5.9, 2.9, 29.7);
        Point3D p5 = new Point3D(20, 20.9, 31.6);
        Point3D p6 = new Point3D(0, 0, 0, 0);
		
        Point3D vrp = new Point3D(25, 15, 80);
		Visibility.VisibilidadeNormal(vrp, p1, p4, p3, p2);
	}

	public static void Buffer() {
		Point3D a = new Point3D(93, 251, -22.807);
		Point3D b = new Point3D(125, 107, -21.815);
		Aresta aresta = new Aresta(a, b);
		System.out.println("=========TESTE ARESTA=========");
		System.out.println(aresta);
		System.out.println("=========TESTE BUFFER ARESTA=========");
		ZBuffer.varrerAresta(aresta);
	}

	public static void Lightning(){
        Point3D a = new Point3D(151.914, 340.497, -39.024);
        Point3D b = new Point3D(369.403, 223.801, -52.594);
        Point3D c = new Point3D(149.556, -51.107, -47.924);
        Point3D d = new Point3D(0, 0, 0);
		Face face = new Face(a, b, c, d, 0, 0, 0, new Point3D(25, 15, 80));
		face.centroide = new Point3D(25.100, 8.333, 33.700);
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
}