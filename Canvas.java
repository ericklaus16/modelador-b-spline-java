import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Canvas extends JPanel {
    private BufferedImage image;
    JFrame frame;
    private List<Point2D> pontos;
    private static boolean configOpened = false;
    private Settings settings;
    private Surface superficie;

    public Canvas(int width, int height, Settings settings, Surface superficie) {
        this.settings = settings;
        this.superficie = superficie;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        fillWhite();  // Garante que a tela comece branca

        // Garantir que a janela de configurações seja aberta apenas uma vez
        if (!configOpened) {
            configOpened = true;
            SwingUtilities.invokeLater(this::openConfigurations);
        }
    }

    private void fillWhite() {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, settings.width, settings.height);
        g2d.dispose();
    }

    private void repaintPoints() {
        fillWhite();
        if (pontos != null) {
            for (Point2D point : pontos) {
                int px = (int) Math.round(point.x);
                int py = (int) Math.round(point.y);
                if (px >= 0 && px < settings.width && py >= 0 && py < settings.height) {
                    image.setRGB(px, py, Color.BLACK.getRGB());
                }
            }
        }
    }

    public void updatePoints(List<Point2D> newPoints) {
        this.pontos = newPoints;
        repaintPoints();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void openConfigurations() {
        JFrame configFrame = new JFrame("Configurações");
        configFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        configFrame.setSize(400, 600);
        configFrame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Seção Tela
        mainPanel.add(new JLabel("Tela"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createIntegerInputRow("W:", settings.width, "H:", settings.height,
                newWidth -> settings.width = newWidth,
                newHeight -> settings.height = newHeight), gbc);

        // Seção Viewport
        gbc.gridy++;
        mainPanel.add(new JLabel("Viewport"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createIntegerInputRow("W:", settings.widthViewport, "H:", settings.heightViewport,
                newWidth -> settings.widthViewport = newWidth,
                newHeight -> settings.heightViewport = newHeight), gbc);


        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("Câmera:", settings.cameraPos.x, settings.cameraPos.y, settings.cameraPos.z,
                newX -> settings.cameraPos.x = newX,
                newY -> settings.cameraPos.y = newY,
                newZ -> settings.cameraPos.z = newZ
        ), gbc);

        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("Ponto Focal:", settings.pontoFocal.x, settings.pontoFocal.y, settings.pontoFocal.z,
                newX -> settings.pontoFocal.x = newX,
                newY -> settings.pontoFocal.y = newY,
                newZ -> settings.pontoFocal.z = newZ
        ), gbc);

        // Cores das Arestas
        gbc.gridy++;
        mainPanel.add(new JLabel("Cor das Arestas"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createColorSelectionRow("Visíveis", "Não visíveis", settings.visibleEdgeColor, settings.notVisibleEdgeColor), gbc);

        // Matriz de Pontos de Controle
        gbc.gridy++;
        mainPanel.add(new JLabel("Matriz de Pontos de Controle"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createIntegerInputRow("m:", settings.m, "n:", settings.n,
                newM -> settings.m = newM,
                newN -> settings.n = newN), gbc);

        // Superfície
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createRadioButtonGroup("Superfície", 
        newType -> settings.type = SurfaceType.valueOf(newType),
        "Aberta", "Fechada"), gbc);

        // Transformações
        gbc.gridy++;
//        mainPanel.add(new JLabel("Rotação"), gbc);
        mainPanel.add(InterfaceInputs.createTripleInputRow("Rotação:", settings.rotation.x, settings.rotation.y, settings.rotation.z,
                newX -> settings.rotation.x = newX,
                newY -> settings.rotation.y = newY,
                newZ -> settings.rotation.z = newZ
        ), gbc);
        gbc.gridy++;

        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("Translação:", settings.transform.x, settings.transform.y, settings.transform.z,
                newX -> settings.transform.x = newX,
                newY -> settings.transform.y = newY,
                newZ -> settings.transform.z = newZ
        ), gbc);
        gbc.gridy++;

        // Escala
        gbc.gridy++;
        mainPanel.add(new JLabel("Escala (1x)"), gbc);
        gbc.gridy++;
        JSlider scaleSlider = new JSlider(1, 2, 1);
        mainPanel.add(scaleSlider, gbc);

        // Wireframe
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createRadioButtonGroup("Sombreamento", 
            newShading -> settings.shader = Shader.valueOf(newShading),    
    "Wireframe", "Constante", "Gouraud", "Phong"), gbc);

        // Cor de Pintura
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createColorSelectionRow("Cor de Pintura", "", settings.paintColor, null), gbc);

        // Material
        gbc.gridy++;
        mainPanel.add(new JLabel("Material"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("Ka (ambiente):", settings.kar, settings.kag, settings.kab,
            newR -> settings.kar = newR,
            newG -> settings.kag = newG,
            newB -> settings.kab = newB
        ), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("Kd (difusa):", settings.kdr, settings.kdg, settings.kdb,
            newR -> settings.kdr = newR,
            newG -> settings.kdg = newG,
            newB -> settings.kdb = newB
        ), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("Ks (especular):", settings.ksr, settings.ksg, settings.ksb,
            newR -> settings.ksr = newR,
            newG -> settings.ksg = newG,
            newB -> settings.ksb = newB
        ), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createInputRow("N", settings.kn,
                newKn -> settings.kn = newKn
        ), gbc);

        // Botão de salvar
        gbc.gridy++;
        JButton saveButton = new JButton("Atualizar Superfície");
        mainPanel.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                if(settings.m != superficie.m || settings.n != superficie.n) { // A superficie mudou os pontos de controle
                    int resposta = JOptionPane.showConfirmDialog(configFrame, "Cuidado! Você alterou a matriz de pontos de controle! Isso gerará uma nova superfície.");

                    if (resposta != JOptionPane.YES_OPTION) return;

                    superficie = new Surface(settings.m, settings.n, 30, 40);
                }

                superficie.settings = settings;
                superficie.Translate(settings.transform.x, settings.transform.y, settings.transform.z);
                superficie.Rotate(settings.rotation.x, settings.rotation.y, settings.rotation.z);
                superficie.Scale(settings.scale);

                List<Point2D> pontos2D = new ArrayList<Point2D>();
                Viewport vp = new Viewport(settings.width, settings.height, settings.widthViewport, settings.heightViewport);

                for(int i = 0; i < superficie.outp.length; i++){
                    for(int j = 0; j < superficie.outp[i].length; j++){
                        Point2D ponto2D = Pipeline.mapearPonto(superficie.outp[i][j], settings.pontoFocal, settings.cameraPos, vp);
                        pontos2D.add(ponto2D);
                    }
                }

                Show(pontos2D, superficie.outp, settings, superficie.faces);
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(configFrame, "Puta que pariu");
            }
        });

        // Adiciona o painel à janela
        configFrame.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        configFrame.setVisible(true);
    }

    public void Show(List<Point2D> pontos, Point3D[][] pontos3D, Settings settings, List<Face> faces) {
        if(frame == null){
            frame = new JFrame("Modelador de Superfícies B-Spline");
        }
        repaint();

        this.updatePoints(pontos);
        Graphics g = this.image.getGraphics();
        Pintor.pintor(g, pontos3D, pontos, settings, settings.cameraPos, faces);

        frame.add(this);
        frame.setSize(settings.width, settings.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}