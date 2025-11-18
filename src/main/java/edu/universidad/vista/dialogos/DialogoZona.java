package edu.universidad.vista.dialogos;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Diálogo para agregar o editar zonas
 */
public class DialogoZona {

    private Zona zona; // null si es nueva
    private GestorRecursosLocal gestor;
    private Runnable callback;
    private JDialog dialog;

    public DialogoZona(Zona zona, GestorRecursosLocal gestor, Runnable callback) {
        this.zona = zona;
        this.gestor = gestor;
        this.callback = callback;
    }

    public void mostrar() {
        boolean esNueva = (zona == null);
        String titulo = esNueva ? "Agregar Nueva Zona" : "Editar Zona";

        dialog = new JDialog((Frame) null, titulo, true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(EstilosUI.COLOR_CARD);

        JLabel lblTitulo = new JLabel(esNueva ? "Nueva Zona Afectada" : "Editar Zona");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(30));

        // Campos del formulario
        JTextField txtNombre = new JTextField(esNueva ? "" : zona.getNombre());

        JComboBox<String> cboEstado = new JComboBox<>(
                new String[]{"normal", "afectada", "evacuando", "evacuada"});
        if (!esNueva) {
            cboEstado.setSelectedItem(zona.getEstado());
        }

        JSpinner spnPrioridad = new JSpinner(new SpinnerNumberModel(
                esNueva ? 50 : zona.getPrioridad(), 0, 100, 5));

        EstilosUI.addFormField(panel, "Nombre de la zona:", txtNombre);
        EstilosUI.addFormField(panel, "Estado:", cboEstado);

        // Personalizar spinner
        JLabel lblPrioridad = new JLabel("Prioridad (0-100):");
        lblPrioridad.setFont(EstilosUI.FONT_NORMAL);
        lblPrioridad.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);
        lblPrioridad.setAlignmentX(Component.LEFT_ALIGNMENT);

        spnPrioridad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        spnPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnPrioridad.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblPrioridad);
        panel.add(Box.createVerticalStrut(8));
        panel.add(spnPrioridad);
        panel.add(Box.createVerticalStrut(15));

        panel.add(Box.createVerticalStrut(20));

        // Botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton(esNueva ? "Guardar" : "Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        EstilosUI.stylePrimaryButton(btnGuardar);
        EstilosUI.styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String estado = (String) cboEstado.getSelectedItem();
            int prioridad = (Integer) spnPrioridad.getValue();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Ingrese el nombre de la zona",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (esNueva) {
                gestor.agregarZona(nombre, estado, prioridad);
                JOptionPane.showMessageDialog(dialog,
                        "Zona agregada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                gestor.actualizarZona(zona.getId(), nombre, estado, prioridad);
                JOptionPane.showMessageDialog(dialog,
                        "Zona actualizada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (callback != null) {
                callback.run();
            }
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
