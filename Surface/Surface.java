package Surface;
import Main.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Geometria.*;

import javax.swing.*;

public class Surface implements Serializable {
    private static final long serialVersionUID = 1L;
    public Integer id;
    public String name;
    public int m;
    public int n;
    public int RESOLUTIONI;
    public int RESOLUTIONJ;
    public Point3D[][] inp;
    private Point3D[][] originalInp;
    public Point3D[][] outp;
    public Color[][] colorBuffer;
    public double[][] zBuffer;
    public Point3D[][] inpScaleReference;
    public Point3D[][] outpScaleReference;
    public List<Face> faces = new ArrayList<>();
    public Settings settings = new Settings();
    public Map<Point3D, Color> vertexColors = new HashMap<>();
    public double z;
    boolean adicionou3PC = false;

    public Surface(int m, int n) {
        this.name = "";
        this.m = m;
        this.n = n;
        this.inp = new Point3D[m + 1][n + 1];
        this.outp = new Point3D[RESOLUTIONI][RESOLUTIONJ];
        this.RESOLUTIONI = settings.resolutionI;
        this.RESOLUTIONJ = settings.resolutionJ;
        this.faces = new ArrayList<>();

        this.GerarSuperficie();
    }

    public void GerarSuperficie() {
        for(int i = 0; i <= this.m; i++) {
            for(int j = 0; j <= this.n; j++) {
                this.inp[i][j] = new Point3D(0, 0, 0);
            }
        }

        this.outp = new Point3D[this.RESOLUTIONI][this.RESOLUTIONJ];

        for(int i = 0; i < this.RESOLUTIONI; i++) {
            for(int j = 0; j < this.RESOLUTIONJ; j++) {
                this.outp[i][j] = new Point3D(0, 0, 0);
            }
        }

        for (int i = 0; i <= this.m; i++) {
            for (int j = 0; j <= this.n; j++) {
                this.inp[i][j].x = i; // i
                this.inp[i][j].y = j;
                this.inp[i][j].z = (Math.floor(Math.random() * 10000)) / 5000 - 1;
            }
        }

        UpdateSurfaceOutput();
    }

    public Face createFace(int i, int j) {
        Point3D A = this.outp[i][j];
        Point3D B = this.outp[i][j + 1];
        Point3D C = this.outp[i + 1][j + 1];
        Point3D D = this.outp[i + 1][j];
    
        Point3D centroide = new Point3D(
            (A.x + B.x + C.x + D.x) / 4,
            (A.y + B.y + C.y + D.y) / 4,
            (A.z + B.z + C.z + D.z) / 4,
            1
        );
    
        double h = Math.abs(centroide.x) / Math.cos(Math.atan(Math.abs(centroide.y) / Math.abs(centroide.x)));
        double d = Math.abs(centroide.z) / Math.cos(Math.atan(h / Math.abs(centroide.z)));

//        double d = Math.sqrt(
//            Math.pow(this.settings.cameraPos.x - centroide.x, 2) +
//            Math.pow(this.settings.cameraPos.y - centroide.y, 2) +
//            Math.pow(this.settings.cameraPos.z - centroide.z, 2)
//        );

        Aresta AB = new Aresta(A, B);
        Aresta BC = new Aresta(B, C);
        Aresta CD = new Aresta(C, D);
        Aresta DA = new Aresta(D, A);
    
        // if(d < this.settings.near || d > this.settings.far) {
        //     return null;
        // }

        Face face = new Face(A, B, C, D, d, i, j, this.settings.cameraPos);
        face.arestas.add(AB);
        face.arestas.add(BC);
        face.arestas.add(CD);
        face.arestas.add(DA);

        return face;
    }

    // Método adicional que pode ser útil na classe Surface
    public void processFaces() {
        // System.out.println("Processing faces");
        this.faces.clear();
        
        for (int i = 0; i < this.RESOLUTIONI - 1; i++) {
            for (int j = 0; j < this.RESOLUTIONJ - 1; j++) {
                Face face = createFace(i, j);
                if (face != null) {
                    this.faces.add(face);
                }
            }
        }
        z = (faces.get(faces.size()/2).d);
    }

    private void updateReferences() {
		// if(this.outp[0][0].x == 0 && this.outp[0][0].y == 0 && this.outp[0][0].z == 0) return;

        this.inpScaleReference = new Point3D[this.inp.length][this.inp[0].length];
        this.outpScaleReference = new Point3D[this.outp.length][this.outp[0].length];
        
        for (int i = 0; i < this.inp.length; i++) {
            for (int j = 0; j < this.inp[i].length; j++) {
                this.inpScaleReference[i][j] = new Point3D(this.inp[i][j].x, this.inp[i][j].y, this.inp[i][j].z);
            }
        }
        
        for (int i = 0; i < this.outp.length; i++) {
            for (int j = 0; j < this.outp[i].length; j++) {
                this.outpScaleReference[i][j] = new Point3D(this.outp[i][j].x, this.outp[i][j].y, this.outp[i][j].z);
            }
        }
        
        processFaces();
	}

    public void UpdateSurfaceOutput() {
        int TI = 3;
        int TJ = 3;
        int i, j, ki, kj;
    
        // CORRIGIDO: Usar this.m para knotsI, não this.m
        double[] knotsI = new double[this.m + TI + 1];
        double[] knotsJ = new double[this.n + TJ + 1];
    
        double intervalI, incrementI;
        double intervalJ, incrementJ;
        double bi, bj;
    
        incrementI = (this.m - TI + 2) / (double) (this.RESOLUTIONI - 1);
        incrementJ = (this.n - TJ + 2) / (double) (this.RESOLUTIONJ - 1);
    
        Curve curve = new Curve();
    
        if(settings.type == SurfaceType.Fechada){
            curve.PeriodicSplineKnots(knotsI, this.m, TI);
            curve.PeriodicSplineKnots(knotsJ, this.n, TJ);
        } else {
            curve.SplineKnots(knotsI, this.m, TI);
            curve.SplineKnots(knotsJ, this.n, TJ);
        }
    
        intervalI = 0;
        for (i = 0; i < this.RESOLUTIONI - 1; i++) {
            intervalJ = 0;
            for (j = 0; j < this.RESOLUTIONJ - 1; j++) {
                this.outp[i][j] = new Point3D(0, 0, 0);
    
                for (ki = 0; ki <= this.m; ki++) { // Usar this.m
                    for (kj = 0; kj <= this.n; kj++) {
                        bi = curve.SplineBlend(ki, TI, knotsI, intervalI);
                        bj = curve.SplineBlend(kj, TJ, knotsJ, intervalJ);
                        this.outp[i][j].x += this.inp[ki][kj].x * bi * bj; // Usar this.inp
                        this.outp[i][j].y += this.inp[ki][kj].y * bi * bj;
                        this.outp[i][j].z += this.inp[ki][kj].z * bi * bj;
                    }
                }
                intervalJ += incrementJ;
            }
            intervalI += incrementI;
        }
    
        // CORRIGIDO: Ajustar também para bordas
        intervalI = 0;

        for (i = 0; i < this.RESOLUTIONI - 1; i++) {
            this.outp[i][this.RESOLUTIONJ - 1] = new Point3D(0, 0, 0);
    
            for (ki = 0; ki <= this.m; ki++) { // Usar this.m
                bi = curve.SplineBlend(ki, TI, knotsI, intervalI);
                this.outp[i][this.RESOLUTIONJ - 1].x += this.inp[ki][this.n].x * bi;
                this.outp[i][this.RESOLUTIONJ - 1].y += this.inp[ki][this.n].y * bi;
                this.outp[i][this.RESOLUTIONJ - 1].z += this.inp[ki][this.n].z * bi;
            }
            intervalI += incrementI;
        }
    
        // CORRIGIDO: Último ponto
        this.outp[i][this.RESOLUTIONJ - 1] = this.inp[this.m][this.n];
        intervalJ = 0;

        for (j = 0; j < this.RESOLUTIONJ - 1; j++) {
            this.outp[this.RESOLUTIONI - 1][j] = new Point3D(0, 0, 0);
    
            for (kj = 0; kj <= this.n; kj++) {
                bj = curve.SplineBlend(kj, TJ, knotsJ, intervalJ);
                this.outp[this.RESOLUTIONI - 1][j].x += this.inp[this.m][kj].x * bj;
                this.outp[this.RESOLUTIONI - 1][j].y += this.inp[this.m][kj].y * bj;
                this.outp[this.RESOLUTIONI - 1][j].z += this.inp[this.m][kj].z * bj;
            }
            intervalJ += incrementJ;
        }
        this.outp[this.RESOLUTIONI - 1][j] = this.inp[this.m][this.n];
    
        this.updateReferences();
    }

    public void OpenCloseSurface(){
        if(settings.type == SurfaceType.Fechada && !adicionou3PC){
            // Guarda uma cópia limpa da superfície aberta original apenas na primeira vez
            if(originalInp == null) {
                originalInp = new Point3D[this.m + 1][this.n + 1];
                for(int i = 0; i <= this.m; i++){
                    for(int j = 0; j <= this.n; j++){
                        originalInp[i][j] = new Point3D(
                            this.inp[i][j].x, 
                            this.inp[i][j].y,
                            this.inp[i][j].z
                        );
                    }
                }
            }
            
            // Adicionar os 3 primeiros pontos de controle no final da superfície
            Point3D[][] newInp = new Point3D[this.m + 4][this.n + 4];
        
            // Inicializa toda a matriz para evitar NullPointerException
            for(int i = 0; i <= this.m + 3; i++){
                for(int j = 0; j <= this.n + 3; j++){
                    newInp[i][j] = new Point3D(0, 0, 0);
                }
            }
            
            // Copia os pontos de controle existentes
            for(int i = 0; i <= this.m; i++){
                for(int j = 0; j <= this.n; j++){
                    newInp[i][j] = this.inp[i][j];
                }
            }
            
            // Adiciona os primeiros 3 pontos de controle no final
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    newInp[this.m + 1 + i][this.n + 1 + j] = this.inp[i][j];
                }
            }
            
            this.inp = newInp;
            this.m += 3;
            this.n += 3;
            settings.m = this.m;
            settings.n = this.n;
            this.adicionou3PC = true;
            
            // Recria completamente a saída
            this.outp = new Point3D[this.RESOLUTIONI][this.RESOLUTIONJ];
            for(int i = 0; i < this.RESOLUTIONI; i++) {
                for(int j = 0; j < this.RESOLUTIONJ; j++) {
                    this.outp[i][j] = new Point3D(0, 0, 0);
                }
            }
            
            UpdateSurfaceOutput();
            this.inpScaleReference = null;
            this.updateReferences();
        } else if(settings.type == SurfaceType.Aberta && adicionou3PC){
            if (originalInp != null) {
                int originalM = originalInp.length - 1; // m original é inp.length - 1
                int originalN = originalInp[0].length - 1;
    
                this.m = originalM;
                this.n = originalN;
                settings.m = originalM;
                settings.n = originalN;
    
                this.inp = new Point3D[originalM + 1][originalN + 1];
                for (int i = 0; i <= originalM; i++) {
                    System.arraycopy(originalInp[i], 0, this.inp[i], 0, originalN + 1);
                }
    
                this.adicionou3PC = false;
                this.RESOLUTIONI = settings.resolutionI;
                this.RESOLUTIONJ = settings.resolutionJ;
                this.outp = new Point3D[RESOLUTIONI][RESOLUTIONJ];
                UpdateSurfaceOutput(); // Forçar recálculo com knots abertos
            }
        }
    }

    public void Translate(double dx, double dy, double dz) {
        double[][] matriz = {
            {1, 0, 0, dx},
            {0, 1, 0, dy},
            {0, 0, 1, dz},
            {0, 0, 0, 1}
        };
        atualizarMatrizes(matriz);
        this.updateReferences();
    }

    public void Rotate(double x, double y, double z) {
        if (x != 0) {
            this.RotateX(x);
        }
        if (y != 0) {
            this.RotateY(y);
        }
        if (z != 0) {
            this.RotateZ(z);
        }
        this.updateReferences();
    }

    public void RotateX(double deg) {
		double[][] matriz = {
                {1, 0, 0, 0},
                {0, Utils.cos(deg), -Utils.sin(deg), 0},
                {0, Utils.sin(deg), Utils.cos(deg), 0},
                {0, 0, 0, 1}
		};

        this.atualizarMatrizes(matriz);
    }

    public void RotateY(double deg) {
        double[][] matriz = {
                {Utils.cos(deg), 0, Utils.sin(deg), 0},
                {0, 1, 0, 0},
                {-Utils.sin(deg), 0, Utils.cos(deg), 0},
                {0, 0, 0, 1}
        };

        this.atualizarMatrizes(matriz);
    }

    public void RotateZ(double deg) {
        double[][] matriz = {
                {Utils.cos(deg), -Utils.sin(deg), 0, 0},
                {Utils.sin(deg), Utils.cos(deg), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };

        this.atualizarMatrizes(matriz);
    }

    public void Scale(double s) {
        if (s == 1 || s == 0) {
            return;
        }

        double[][] matriz = {
                {s, 0, 0, 0},
                {0, s, 0, 0},
                {0, 0, s, 0},
                {0, 0, 0, 1}
        };

        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                this.inp[i][j] = Utils.multiplicarMatrizPorPonto(
                        matriz,
                        this.inpScaleReference[i][j]
                );
            }
        }

        for (int i = 0; i < this.RESOLUTIONI; i++) {
            for (int j = 0; j < this.RESOLUTIONJ; j++) {
                this.outp[i][j] = Utils.multiplicarMatrizPorPonto(
                        matriz,
                        this.outpScaleReference[i][j]
                );
            }
        }

        this.updateReferences();
        // console.log(this.outp[0][0]);
    }

    public void atualizarMatrizes(double[][] matriz) {
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                this.inp[i][j] = Utils.multiplicarMatrizPorPonto(matriz, this.inp[i][j]);
            }
        }
        
        for (int i = 0; i < this.RESOLUTIONI; i++) {
            for (int j = 0; j < this.RESOLUTIONJ; j++) {
                this.outp[i][j] = Utils.multiplicarMatrizPorPonto(matriz, this.outp[i][j]);
            }
        }
    }
}