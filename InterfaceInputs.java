import java.util.function.Consumer;
import javax.swing.*;
import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InterfaceInputs {
    
    // Cria um painel com dois inputs lado a lado e listeners para atualizar valores dinamicamente
    public static JPanel createInputRow(String label1, Double value1, Consumer<Double> updateValue1) {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.add(new JLabel(label1));

        JTextField textField1 = new JTextField(value1.toString(), 5);
        panel.add(textField1);

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

        return panel;
    }

    public static JPanel createIntegerInputRow(String label1, Integer value1, String label2, Integer value2, Consumer<Integer> updateValue1, Consumer<Integer> updateValue2) {
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

    public static JPanel createTripleInputRow(String label1, Double value1, Double value2, Double value3,
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
    public static JPanel createColorSelectionRow(String label1, String label2, Color color1, Color color2) {
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
    public static JPanel createRadioButtonGroup(String title, String... options) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        ButtonGroup group = new ButtonGroup();

        boolean first = true;
        for (String option : options) {
            JRadioButton radioButton = new JRadioButton(option);
            group.add(radioButton);
            panel.add(radioButton);

            if(first) {
                radioButton.setSelected(true);
                first = false;
            }
        }
        return panel;
    }
}
