package edu.universidad.vista.dialogos;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoRuta {

    private GestorRecursosLocal gestor;
    private Runnable callback;
    private JDialog dialog;

    public DialogoRuta(GestorRecursosLocal gestor, Runnable callback) {
        this.gestor = gestor;
        this.callback = callback;
    }

    public void mostrar() {
        dialog = new JDialog((Frame) null, "Nueva Ruta", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(EstilosUI.COLOR_CARD);

        JLabel titulo = new JLabel("Crear Nueva Ruta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JComboBox<String> cboOrigen = new JComboBox<>();
        for (Zona z : gestor.obtenerZonas()) {
            cboOrigen.addItem(z.getNombre());
        }

        JComboBox<String> cboDestino = new JComboBox<>();
        for (Zona z : gestor.obtenerZonas()) {
            cboDestino.addItem(z.getNombre());
        }

        JTextField txtPeso = new JTextField();

        EstilosUI.addFormField(panel, "Origen:", cboOrigen);
        EstilosUI.addFormField(panel, "Destino:", cboDestino);
        EstilosUI.addFormField(panel, "Distancia (km):", txtPeso);

        JLabel lblAyuda = new JLabel("<html><i>💡 La distancia debe ser un número positivo</i></html>");
        lblAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAyuda.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
        lblAyuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblAyuda);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Crear Ruta");
        JButton btnCancelar = new JButton("Cancelar");

        EstilosUI.stylePrimaryButton(btnGuardar);
        EstilosUI.styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String origen = (String) cboOrigen.getSelectedItem();
            String destino = (String) cboDestino.getSelectedItem();
            String pesoStr = txtPeso.getText().trim();

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(dialog, "Seleccione origen y destino", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origen.equals(destino)) {
                JOptionPane.showMessageDialog(dialog, "El origen y el destino deben ser diferentes", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (pesoStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ingrese la distancia de la ruta", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double peso = Double.parseDouble(pesoStr);

                if (peso <= 0) {
                    JOptionPane.showMessageDialog(dialog, "La distancia debe ser un número positivo", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                gestor.agregarRuta(origen, destino, peso);

                JOptionPane.showMessageDialog(dialog,
                        "Ruta creada exitosamente\n\n" +
                                "Origen: " + origen + "\n" +
                                "Destino: " + destino + "\n" +
                                "Distancia: " + String.format("%.1f", peso) + " km\n\n" +
                                "El mapa se actualizará automáticamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                if (callback != null) {
                    callback.run();
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La distancia debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
