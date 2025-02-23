import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Canvas extends JPanel {
    private BufferedImage image;
    JFrame frame;
    private int width;
    private int height;
    private List<Point2D> pontos;
    private static boolean configOpened = false; // Agora é static para abrir só uma vez globalmente
    private Settings settings;
    private Surface superficie;

    public Canvas(int width, int height, Settings settings, Surface superficie) {
        this.width = width;
        this.height = height;
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
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
    }

    private void repaintPoints() {
        fillWhite();
        if (pontos != null) {
            for (Point2D point : pontos) {
                int px = (int) Math.round(point.x);
                int py = (int) Math.round(point.y);
                if (px >= 0 && px < width && py >= 0 && py < height) {
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
        mainPanel.add(createIntegerInputRow("W:", settings.width, "H:", settings.height,
                newWidth -> settings.width = newWidth,
                newHeight -> settings.height = newHeight), gbc);

        // Seção Viewport
        gbc.gridy++;
        mainPanel.add(new JLabel("Viewport"), gbc);
        gbc.gridy++;
        mainPanel.add(createIntegerInputRow("W:", settings.widthViewport, "H:", settings.heightViewport,
                newWidth -> settings.widthViewport = newWidth,
                newHeight -> settings.heightViewport = newHeight), gbc);


        gbc.gridy++;
        mainPanel.add(createTripleInputRow("Câmera:", settings.cameraPos.x, settings.cameraPos.y, settings.cameraPos.z,
                newX -> settings.cameraPos.x = newX,
                newY -> settings.cameraPos.y = newY,
                newZ -> settings.cameraPos.z = newZ
        ), gbc);

        gbc.gridy++;
        mainPanel.add(createTripleInputRow("Ponto Focal:", settings.pontoFocal.x, settings.pontoFocal.y, settings.pontoFocal.z,
                newX -> settings.pontoFocal.x = newX,
                newY -> settings.pontoFocal.y = newY,
                newZ -> settings.pontoFocal.z = newZ
        ), gbc);

        // Cores das Arestas
        gbc.gridy++;
        mainPanel.add(new JLabel("Cor das Arestas"), gbc);
        gbc.gridy++;
        mainPanel.add(createColorSelectionRow("Visíveis", "Não visíveis", settings.visibleEdgeColor, settings.notVisibleEdgeColor), gbc);

        // Matriz de Pontos de Controle
        gbc.gridy++;
        mainPanel.add(new JLabel("Matriz de Pontos de Controle"), gbc);
        gbc.gridy++;
        mainPanel.add(createIntegerInputRow("m:", settings.m, "n:", settings.n,
                newM -> settings.m = newM,
                newN -> settings.n = newN), gbc);

        // Superfície
        gbc.gridy++;
        mainPanel.add(createRadioButtonGroup("Superfície", "Aberta", "Fechada"), gbc);

        // Transformações
        gbc.gridy++;
//        mainPanel.add(new JLabel("Rotação"), gbc);
        mainPanel.add(createTripleInputRow("Rotação:", settings.rotation.x, settings.rotation.y, settings.rotation.z,
                newX -> settings.rotation.x = newX,
                newY -> settings.rotation.y = newY,
                newZ -> settings.rotation.z = newZ
        ), gbc);
        gbc.gridy++;

        gbc.gridy++;
        mainPanel.add(createTripleInputRow("Translação:", settings.transform.x, settings.transform.y, settings.transform.z,
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
        mainPanel.add(createRadioButtonGroup("Sombreamento", "Wireframe", "Constante", "Gouraud", "Phong"), gbc);

        // Cor de Pintura
        gbc.gridy++;
        mainPanel.add(createColorSelectionRow("Cor de Pintura", "", settings.paintColor, null), gbc);

        // Material
        gbc.gridy++;
        mainPanel.add(new JLabel("Material"), gbc);
        gbc.gridy++;mainPanel.add(createInputRow("Ka (ambiente)", settings.ka, "Kd (difuso)", settings.kd,
                newKa -> settings.ka = newKa,
                newKd -> settings.kd = newKd
        ), gbc);
        gbc.gridy++;
        gbc.gridy++;mainPanel.add(createInputRow("Ks (especular)", settings.ks, "N", settings.kn,
                newKs -> settings.ks = newKs,
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

                Show(pontos2D, superficie.outp, settings);
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(configFrame, "Puta que pariu");
            }
        });

        // Adiciona o painel à janela
        configFrame.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        configFrame.setVisible(true);
    }

    // Cria um painel com dois inputs lado a lado e listeners para atualizar valores dinamicamente
    private static JPanel createInputRow(String label1, Double value1, String label2, Double value2,
                                         Consumer<Double> updateValue1, Consumer<Double> updateValue2) {
        JPanel panel = new JPanel(new GridLayout(1, 4));

        panel.add(new JLabel(label1));

        JTextField textField1 = new JTextField(value1.toString(), 5);
        panel.add(textField1);

        panel.add(new JLabel(label2));

        JTextField textField2 = new JTextField(value2.toString(), 5);
        panel.add(textField2);

        // Listeners para atualizar os valores de settings
        textField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue1.accept(Double.parseDouble(textField1.getText()));
                } catch (NumberFormatException ignored) {}
            }
        });

        textField2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue2.accept(Double.parseDouble(textField2.getText()));
                } catch (NumberFormatException ignored) {}
            }
        });

        return panel;
    }

    private static JPanel createIntegerInputRow(String label1, Integer value1, String label2, Integer value2, Consumer<Integer> updateValue1, Consumer<Integer> updateValue2) {
        JPanel panel = new JPanel(new GridLayout(1, 4));

        panel.add(new JLabel(label1));

        JTextField textField1 = new JTextField(value1.toString(), 5);
        panel.add(textField1);

        panel.add(new JLabel(label2));

        JTextField textField2 = new JTextField(value2.toString(), 5);
        panel.add(textField2);

        // Listeners para atualizar os valores de settings
        textField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue1.accept(Integer.parseInt(textField1.getText()));
                } catch (NumberFormatException ignored) {} // Evita erro enquanto digita
            }
        });

        textField2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue2.accept(Integer.parseInt(textField2.getText()));
                } catch (NumberFormatException ignored) {}
            }
        });

        return panel;
    }

    private static JPanel createTripleInputRow(String label1, Double value1, Double value2, Double value3,
                                               Consumer<Double> updateValue1, Consumer<Double> updateValue2, Consumer<Double> updateValue3) {
        JPanel panel = new JPanel(new GridLayout(1, 6));

        panel.add(new JLabel(label1));

        JTextField textField1 = new JTextField(value1.toString(), 5);
        panel.add(textField1);

        JTextField textField2 = new JTextField(value2.toString(), 5);
        panel.add(textField2);

        JTextField textField3 = new JTextField(value3.toString(), 5);
        panel.add(textField3);

        // Listeners para atualizar os valores de settings
        textField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue1.accept(Double.parseDouble(textField1.getText()));
                } catch (NumberFormatException ignored) {}
            }
        });

        textField2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue2.accept(Double.parseDouble(textField2.getText()));
                } catch (NumberFormatException ignored) {}
            }
        });

        textField3.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    updateValue3.accept(Double.parseDouble(textField3.getText()));
                } catch (NumberFormatException ignored) {}
            }
        });

        return panel;
    }

    // Cria um painel para seleção de cores
    private static JPanel createColorSelectionRow(String label1, String label2, Color color1, Color color2) {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.add(new JLabel(label1));
        JButton colorButton1 = new JButton();
        colorButton1.setBackground(color1);
        panel.add(colorButton1);

        if (label2 != null && color2 != null) {
            panel.add(new JLabel(label2));
            JButton colorButton2 = new JButton();
            colorButton2.setBackground(color2);
            panel.add(colorButton2);
        }
        return panel;
    }

    // Cria botões de rádio
    private static JPanel createRadioButtonGroup(String title, String... options) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        ButtonGroup group = new ButtonGroup();
        for (String option : options) {
            JRadioButton radioButton = new JRadioButton(option);
            group.add(radioButton);
            panel.add(radioButton);
        }
        return panel;
    }

    public void Show(List<Point2D> pontos, Point3D[][] pontos3D, Settings settings) {
        if(frame == null){
            frame = new JFrame("Pixel Canvas");
        }
        repaint();

        this.updatePoints(pontos);
        Graphics g = this.image.getGraphics();
        Pintor.pintor(g, pontos3D, pontos, settings, settings.cameraPos);

        frame.add(this);
        frame.setSize(settings.width, settings.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}