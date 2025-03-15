import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        surfacesFrame.setSize(400, 500);
        surfacesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        surfacesPanel = new JPanel();
        surfacesPanel.setLayout(new BoxLayout(surfacesPanel, BoxLayout.Y_AXIS));
        
        // Botão para criar uma nova superfície
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    
        // Botão para criar uma nova superfície
        JButton createButton = new JButton("Criar Nova Superfície");
        createButton.addActionListener(e -> createNewSurface());
        
        // Botão para carregar uma superfície existente
        JButton loadButton = new JButton("Carregar Superfície");
        loadButton.addActionListener(e -> loadSurfaceFromFile());
        
        // Adicionar os botões ao painel
        buttonPanel.add(createButton);
        buttonPanel.add(loadButton);
        
        surfacesFrame.add(buttonPanel, BorderLayout.NORTH);
        surfacesFrame.add(new JScrollPane(surfacesPanel), BorderLayout.CENTER);
        surfacesFrame.setVisible(true);
    }

    private void loadSurfaceFromFile() {
        try {
            // Criar seletor de arquivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Carregar Superfície");
            
            // Adicionar filtro para arquivos .surf
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Arquivos de Superfície (*.surf)", "surf");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(surfacesFrame);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // Carregar o arquivo usando ObjectInputStream
                try (ObjectInputStream in = new ObjectInputStream(
                        new java.io.BufferedInputStream(new java.io.FileInputStream(selectedFile)))) {
                    
                    // Ler o objeto salvo
                    SavedSurface loadedData = (SavedSurface) in.readObject();
                    
                    // Verificar se já existe uma superfície com o mesmo nome
                    String surfaceName = loadedData.name;
                    int counter = 1;
                    
                    // Se o nome já existe, adicionar um sufixo numérico
                    while (surfaceMap.containsKey(surfaceName)) {
                        surfaceName = loadedData.name + " (" + counter + ")";
                        counter++;
                    }
                    
                    // Adicionar a superfície carregada aos mapas
                    surfaceMap.put(surfaceName, loadedData.surface);
                    settingsMap.put(surfaceName, loadedData.settings);
                    
                    // Criar uma variável final para usar na lambda
                    final String finalSurfaceName = surfaceName;
                    
                    // Criar botão para a superfície carregada
                    JButton surfaceButton = new JButton(finalSurfaceName);
                    surfaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    surfaceButton.addActionListener(e -> {
                        // Atualizar referências usando a variável final
                        settings = settingsMap.get(finalSurfaceName);
                        superficie = surfaceMap.get(finalSurfaceName);
                        
                        // Abrir configurações
                        openConfigurationsForSurface(finalSurfaceName);
                    });
                    
                    // Adicionar botão ao painel
                    surfacesPanel.add(surfaceButton);
                    surfacesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    surfacesPanel.revalidate();
                    surfacesPanel.repaint();
                    
                    // Atualizar referências atuais
                    settings = loadedData.settings;
                    superficie = loadedData.surface;
                    
                    // Abrir configurações para a superfície carregada
                    openConfigurationsForSurface(finalSurfaceName);
                    
                    JOptionPane.showMessageDialog(surfacesFrame, 
                        "Superfície carregada com sucesso!\nNome: " + finalSurfaceName,
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(surfacesFrame, 
                        "Erro ao carregar o arquivo: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(surfacesFrame, 
                "Erro inesperado: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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
        image = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
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

    // Método para abrir o editor de pontos de controle
    private void openControlPointsEditor() {
        if (superficie == null) {
            JOptionPane.showMessageDialog(configFrame, 
                "Nenhuma superfície selecionada para edição.",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFrame editorFrame = new JFrame("Editor de Pontos de Controle");
        editorFrame.setSize(600, 500);
        editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Painel com grid para os pontos de controle
        JPanel gridPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        
        // Adicionar cabeçalhos
        gbc.gridy = 0;
        gbc.gridx = 0;
        gridPanel.add(new JLabel(""), gbc);
        
        for (int j = 0; j <= superficie.n; j++) {
            gbc.gridx = j + 1;
            gridPanel.add(new JLabel("Ponto " + j), gbc);
        }
        
        // Componentes para edição dos pontos
        JTextField[][][] pointFields = new JTextField[superficie.m + 1][superficie.n + 1][3]; // [m][n][xyz]
        
        // Criar campos para cada ponto de controle
        for (int i = 0; i <= superficie.m; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            gridPanel.add(new JLabel("Ponto " + i), gbc);
            
            for (int j = 0; j <= superficie.n; j++) {
                gbc.gridx = j + 1;
                
                // Painel para coordenadas XYZ deste ponto
                JPanel pointPanel = new JPanel(new GridLayout(3, 2));
                
                // Criar campos para X, Y, Z
                JLabel labelX = new JLabel("X:");
                JTextField fieldX = new JTextField(String.format("%.2f", superficie.inp[i][j].x), 5);
                pointFields[i][j][0] = fieldX;
                
                JLabel labelY = new JLabel("Y:");
                JTextField fieldY = new JTextField(String.format("%.2f", superficie.inp[i][j].y), 5);
                pointFields[i][j][1] = fieldY;
                
                JLabel labelZ = new JLabel("Z:");
                JTextField fieldZ = new JTextField(String.format("%.2f", superficie.inp[i][j].z), 5);
                pointFields[i][j][2] = fieldZ;
                
                // Adicionar ao painel deste ponto
                pointPanel.add(labelX);
                pointPanel.add(fieldX);
                pointPanel.add(labelY);
                pointPanel.add(fieldY);
                pointPanel.add(labelZ);
                pointPanel.add(fieldZ);
                
                gridPanel.add(pointPanel, gbc);
            }
        }
        
        // Adicionar painel de rolagem
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Aplicar Alterações");
        JButton cancelButton = new JButton("Cancelar");
        
        saveButton.addActionListener(evt -> {
            try {
                // Atualizar os pontos de controle
                for (int i = 0; i <= superficie.m; i++) {
                    for (int j = 0; j <= superficie.n; j++) {
                        String textX = pointFields[i][j][0].getText().replace(',', '.');
                        String textY = pointFields[i][j][1].getText().replace(',', '.');
                        String textZ = pointFields[i][j][2].getText().replace(',', '.');
                        
                        double x = Double.parseDouble(textX);
                        double y = Double.parseDouble(textY);
                        double z = Double.parseDouble(textZ);
                        
                        superficie.inp[i][j].x = x;
                        superficie.inp[i][j].y = y;
                        superficie.inp[i][j].z = z;
                    }
                }
                
                // Recalcular a superfície
                superficie.UpdateSurfaceOutput();
                
                JOptionPane.showMessageDialog(editorFrame, 
                    "Pontos de controle atualizados com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                editorFrame.dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(editorFrame,
                    "Erro: Digite apenas valores numéricos válidos.",
                    "Erro de formato", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(evt -> editorFrame.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        editorFrame.add(mainPanel);
        editorFrame.setVisible(true);
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

        gbc.gridy++;
        JPanel scalePanel = new JPanel(new BorderLayout(5, 0));
        scalePanel.add(new JLabel("Escala:"), BorderLayout.WEST);

        // Criar um slider com valores inteiros maiores para uma maior precisão
        JSlider scaleSlider = new JSlider(1, 200, 100); // Valores de 1 a 200, começando em 100
        JLabel scaleValueLabel = new JLabel("1.0");
        scaleValueLabel.setPreferredSize(new Dimension(40, 20));
        scaleValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Configura o slider para mostrar as marcações principais
        scaleSlider.setMajorTickSpacing(50);
        scaleSlider.setMinorTickSpacing(10);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(false);

        // Adiciona um ChangeListener que converte o valor inteiro do slider para decimal
        scaleSlider.addChangeListener(e -> {
            // Converter de 1-200 para 0.1-2.0
            double scaleValue = scaleSlider.getValue() / 100.0;
            
            // Formatar para exibição com uma casa decimal
            String formattedValue = String.format("%.1f", scaleValue);
            scaleValueLabel.setText(formattedValue);
            
            if(formattedValue.equals("1,0")){
                settings.scale = 1;
            } else {
                settings.scale = scaleValue;
            }
        });

        // Painel para conter o slider e o label de valor
        JPanel sliderWithValuePanel = new JPanel(new BorderLayout(5, 0));
        sliderWithValuePanel.add(scaleSlider, BorderLayout.CENTER);
        sliderWithValuePanel.add(scaleValueLabel, BorderLayout.EAST);

        scalePanel.add(sliderWithValuePanel, BorderLayout.CENTER);
        mainPanel.add(scalePanel, gbc);

        // Wireframe
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createRadioButtonGroup("Sombreamento", 
            newShading -> settings.shader = Shader.valueOf(newShading),    
    "Wireframe", "Constante", "Gouraud", "Phong"), gbc);

        // Cor de Pintura
        gbc.gridy++;
        mainPanel.add(InterfaceInputs.createColorSelectionRow(
            "Cor da Aresta Visível", "Cor da Aresta Não Visível", 
            settings.visibleEdgeColor, settings.notVisibleEdgeColor,
            newColor -> settings.visibleEdgeColor = newColor,
            newColor -> settings.notVisibleEdgeColor = newColor), gbc);

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

        gbc.gridy++;
        JButton editControlPointsButton = new JButton("Editar Pontos de Controle");
        editControlPointsButton.addActionListener(e -> openControlPointsEditor());
        JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        editButtonPanel.add(editControlPointsButton);
        mainPanel.add(editButtonPanel, gbc);

        // Botão de salvar
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Atualizar Superfície");
        buttonPanel.add(saveButton);

        JButton saveToFileButton = new JButton("Salvar Superfície");
        buttonPanel.add(saveToFileButton);

        saveToFileButton.addActionListener(e -> {
            try {
                // Encontrar a superfície atual
                String surfaceAtualKey = null;
                for (Map.Entry<String, Surface> entry : surfaceMap.entrySet()) {
                    if (entry.getValue() == superficie) {
                        surfaceAtualKey = entry.getKey();
                        break;
                    }
                }
                
                if (surfaceAtualKey != null) {
                    // Criar um seletor de arquivo
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Salvar Superfície");
                    
                    // Adicionar filtro de arquivo para .surf
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Arquivos de Superfície (*.surf)", "surf");
                    fileChooser.setFileFilter(filter);
                    
                    // Sugerir nome baseado na chave da superfície
                    fileChooser.setSelectedFile(new File(surfaceAtualKey.toLowerCase().replace(" ", "_") + ".surf"));
                    
                    int userSelection = fileChooser.showSaveDialog(configFrame);
                    
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        
                        // Garantir que o arquivo tenha a extensão .surf
                        String filePath = fileToSave.getAbsolutePath();
                        if (!filePath.toLowerCase().endsWith(".surf")) {
                            fileToSave = new File(filePath + ".surf");
                        }
                        
                        // Preparar dados para salvar
                        SavedSurface dataToSave = new SavedSurface();
                        dataToSave.surface = superficie;
                        dataToSave.settings = settings;
                        dataToSave.name = surfaceAtualKey;
                        
                        // Salvar usando ObjectOutputStream
                        try (ObjectOutputStream out = new ObjectOutputStream(
                                new BufferedOutputStream(new FileOutputStream(fileToSave)))) {
                            out.writeObject(dataToSave);
                            JOptionPane.showMessageDialog(configFrame, 
                                "Superfície salva com sucesso em:\n" + fileToSave.getAbsolutePath(),
                                "Salvo", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(configFrame, 
                                "Erro ao salvar superfície: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(configFrame, 
                    "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });


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
                    if(settings.type != superficie.settings.type){
                        int resposta = JOptionPane.showConfirmDialog(configFrame, 
                            "Cuidado! Você alterou o tipo de superfície! Isso gerará uma nova superfície.");
        
                        if (resposta != JOptionPane.YES_OPTION) return;
        
                        return;
                    }

                    if(settings.m != superficie.m || settings.n != superficie.n){
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
                    Viewport vp = settings.viewport;

                    // Aplicar transformações para a superfície atual
                    superf.Translate(config.transform.x, -config.transform.y, config.transform.z);
                    superf.Rotate(config.rotation.x, -config.rotation.y, config.rotation.z);
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
                    Cut cut = new Cut(settings.viewport.umin, settings.viewport.umax, settings.viewport.vmin, settings.viewport.vmax);
                    
                    // Usar diretamente os pontos da superfície, modificando apenas a abordagem de recorte
                    boolean algumPontoVisivel = false;
                    
                    // Verificar se algum ponto está dentro da viewport
                    for (Point2D ponto : pontosDaSuperficie) {
                        if (ponto.x >= settings.viewport.umin && ponto.x <= settings.viewport.umax &&
                            ponto.y >= settings.viewport.vmin && ponto.y <= settings.viewport.vmax) {
                            algumPontoVisivel = true;
                            break;
                        }
                    }
                    
                    // Em vez de tentar recortar cada face individualmente (o que é complexo),
                    // vamos deixar que o Pintor use o recorte linha a linha que é mais eficiente
                    if (algumPontoVisivel) {
                        // Adicionar logs para debug
                        System.out.println("Renderizando superfície com pontos visíveis");
                        System.out.println("Viewport: [" + settings.viewport.umin + ", " + settings.viewport.umax + 
                                         "] x [" + settings.viewport.vmin + ", " + settings.viewport.vmax + "]");
                        
                        // Usar o Pintor diretamente com os pontos da superfície e deixar
                        // que ele faça o recorte linha a linha, que é geralmente mais eficiente
                        Pintor.pintor(g, pontosDaSuperficie, superf);
                    } else {
                        System.out.println("Nenhum ponto visível dentro da viewport");
                    }
                }
                
                // Atualizar a exibição
                repaint();   
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(configFrame, "Erro ao processar valores numéricos");
            }
        });
        mainPanel.add(buttonPanel, gbc);

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