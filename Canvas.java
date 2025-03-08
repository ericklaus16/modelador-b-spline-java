import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Canvas extends JPanel {
    private BufferedImage image;
    JFrame frame;
    private List<Point2D> pontos;
    private static boolean configOpened = false;
    private Settings settings;
    private Surface superficie;

    private JFrame surfacesFrame;
    private JPanel surfacesPanel;
    private JFrame configFrame;
    private int surfaceCounter = 1;
    private Map<String, Surface> surfaceMap = new HashMap<>();
    private Map<String, Settings> settingsMap = new HashMap<>();

    public void openSurfaceManager() {
        surfacesFrame = new JFrame("Gerenciador de Superfícies");
        surfacesFrame.setSize(300, 500);
        surfacesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        surfacesPanel = new JPanel();
        surfacesPanel.setLayout(new BoxLayout(surfacesPanel, BoxLayout.Y_AXIS));
        
        // Botão para criar uma nova superfície
        JButton createButton = new JButton("Criar Nova Superfície");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> createNewSurface());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        
        surfacesFrame.add(buttonPanel, BorderLayout.NORTH);
        surfacesFrame.add(new JScrollPane(surfacesPanel), BorderLayout.CENTER);
        surfacesFrame.setVisible(true);
    }

    private void createNewSurface() {
        String surfaceName = "SUPERFICIE " + surfaceCounter++;
        
        // Criar nova configuração e superfície
        Settings newSettings = new Settings();
        Surface newSurface = new Surface(newSettings.m, newSettings.n, 30, 40);
        
        // Salvar nos mapas
        settingsMap.put(surfaceName, newSettings);
        surfaceMap.put(surfaceName, newSurface);
        
        // Criar botão para a superfície
        JButton surfaceButton = new JButton(surfaceName);
        surfaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        surfaceButton.addActionListener(e -> {
            // Atualizar as referências atuais
            settings = settingsMap.get(surfaceName);
            superficie = surfaceMap.get(surfaceName);
            
            // Abrir janela de configurações para esta superfície
            openConfigurationsForSurface(surfaceName);
        });
        
        // Adicionar botão ao painel
        surfacesPanel.add(surfaceButton);
        surfacesPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento
        surfacesPanel.revalidate();
        surfacesPanel.repaint();
        
        // Abrir automaticamente as configurações para a nova superfície
        settings = newSettings;
        superficie = newSurface;
        openConfigurationsForSurface(surfaceName);
    }
    
    private void openConfigurationsForSurface(String surfaceName) {
        if (configFrame != null && configFrame.isVisible()) {
            // Atualizar a janela de configurações existente
            updateConfigurationValues();
        } else {
            // Criar uma nova janela de configurações se não existir
            openConfigurations();
        }
    }

    private void updateConfigurationValues() {
        configFrame.dispose();
        openConfigurations();
    }

    public Canvas(int width, int height, Settings settings, Surface superficie) {
        this.settings = settings;
        this.superficie = superficie;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        fillWhite();  // Garante que a tela comece branca

        // Garantir que a janela de configurações seja aberta apenas uma vez
        if (!configOpened) {
            configOpened = true;
        }
    }

    public void iniciarComGerenciador() {
        SwingUtilities.invokeLater(this::openSurfaceManager);
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
        if (configFrame == null || !configFrame.isVisible()) {
            configFrame = new JFrame("Configurações");
            configFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            configFrame.setSize(400, 600);
            configFrame.setLayout(new BorderLayout());
        } else {
            // Limpar o conteúdo anterior se a janela já existir
            configFrame.getContentPane().removeAll();
        }

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
//        gbc.gridy++;
//        mainPanel.add(new JLabel("Cor das Arestas"), gbc);
//        gbc.gridy++;
//        mainPanel.add(InterfaceInputs.createColorSelectionRow("Visíveis", "Não visíveis", settings.visibleEdgeColor, settings.notVisibleEdgeColor), gbc);

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
//        gbc.gridy++;
//        mainPanel.add(InterfaceInputs.createColorSelectionRow("Cor de Pintura", "", settings.paintColor, null), gbc);

        // Luz Ambiente
        gbc.gridy++;
        mainPanel.add(new JLabel("Propriedade dos Materiais e das Luzes"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createInputRow("Luz Ambiente", settings.ila,
                newIla -> settings.ila = newIla
        ), gbc);

        // Lâmpada
        gbc.gridy++;
        mainPanel.add(new JLabel("Lâmpada"), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createInputRow("Il", settings.lampada.il,
                newIl -> settings.lampada.il = newIl
        ), gbc);
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createTripleInputRow("XYZ", settings.lampada.pos.x, settings.lampada.pos.y, settings.lampada.pos.z,
                newX -> settings.lampada.pos.x = newX,
                newY -> settings.lampada.pos.y = newY,
                newZ -> settings.lampada.pos.z = newZ
        ), gbc);

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
                // Atualizar a superfície atual primeiro
                String surfaceAtualKey = null;
                
                // Encontrar a chave da superfície atual no mapa
                for (Map.Entry<String, Surface> entry : surfaceMap.entrySet()) {
                    if (entry.getValue() == superficie) {
                        surfaceAtualKey = entry.getKey();
                        break;
                    }
                }
                
                if (surfaceAtualKey != null) {
                    if(settings.m != superficie.m || settings.n != superficie.n) {
                        int resposta = JOptionPane.showConfirmDialog(configFrame, 
                            "Cuidado! Você alterou a matriz de pontos de controle! Isso gerará uma nova superfície.");
        
                        if (resposta != JOptionPane.YES_OPTION) return;
        
                        // Criar nova superfície com novos pontos de controle
                        Surface novaSuperf = new Surface(settings.m, settings.n, 30, 40);
                        superficie = novaSuperf;
                        
                        // Atualizar o mapa
                        surfaceMap.put(surfaceAtualKey, novaSuperf);
                    }
                    
                    // Aplicar configurações à superfície atual
                    superficie.settings = settings;
                    settingsMap.put(surfaceAtualKey, settings);
                }
                
                // Preparar o frame para exibição
                if(frame == null){
                    frame = new JFrame("Modelador de Superfícies B-Spline");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(settings.width, settings.height);
                    frame.add(Canvas.this);
                    frame.setVisible(true);
                }
                
                // Limpar o canvas antes de desenhar as superfícies
                fillWhite();
                
                // Renderizar cada superfície individualmente
                for (Map.Entry<String, Surface> entry : surfaceMap.entrySet()) {
                    Surface superf = entry.getValue();
                    Settings config = settingsMap.get(entry.getKey());
                    
                    // Criar viewport com as configurações atuais
                    Viewport vp = new Viewport(settings.width, settings.height, 
                                              settings.widthViewport, settings.heightViewport);
                    
                    // Aplicar transformações para a superfície atual
                    superf.Translate(config.transform.x, config.transform.y, config.transform.z);
                    superf.Rotate(config.rotation.x, config.rotation.y, config.rotation.z);
                    superf.Scale(config.scale);
                    
                    // Mapear pontos 3D para 2D para ESTA superfície específica
                    List<Point2D> pontosDaSuperficie = new ArrayList<>();
                    for(int i = 0; i < superf.outp.length; i++){
                        for(int j = 0; j < superf.outp[i].length; j++){
                            Point2D ponto2D = Pipeline.mapearPonto(superf.outp[i][j], 
                                             config.pontoFocal, config.cameraPos, vp);
                            pontosDaSuperficie.add(ponto2D);
                        }
                    }
                    
                    // Renderizar esta superfície específica
                    Graphics g = image.getGraphics();
                    Pintor.pintor(g, pontosDaSuperficie, superf);
                }
                
                // Atualizar a exibição
                repaint();
                        
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(configFrame, "Erro ao processar valores numéricos");
            }
        });

        // Adiciona o painel à janela
        configFrame.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        configFrame.setVisible(true);
    }

    public void Show(List<Point2D> pontos, Surface superficie) {
        if(frame == null){
            frame = new JFrame("Modelador de Superfícies B-Spline");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(settings.width, settings.height);
            frame.add(this);
            frame.setVisible(true);
        }

        this.updatePoints(pontos);
        Graphics g = this.image.getGraphics();
        Pintor.pintor(g, pontos, superficie);
        repaint();
    }
}