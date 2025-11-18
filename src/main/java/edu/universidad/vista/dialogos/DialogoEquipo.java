package edu.universidad.vista.dialogos;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Diálogo para agregar o editar equipos de rescate
 */
public class DialogoEquipo {

    private EquipoRescate equipo; // null si es nuevo
    private GestorRecursosLocal gestor;
    private Runnable callback;
    private JDialog dialog;

    public DialogoEquipo(EquipoRescate equipo, GestorRecursosLocal gestor, Runnable callback) {
        this.equipo = equipo;
        this.gestor = gestor;
        this.callback = callback;
    }

    public void mostrar() {
        boolean esNuevo = (equipo == null);
        String titulo = esNuevo ? "Agregar Nuevo Equipo" : "Editar Equipo";

        dialog = new JDialog((Frame) null, titulo, true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(EstilosUI.COLOR_CARD);

        JLabel lblTitulo = new JLabel(esNuevo ? "Nuevo Equipo de Rescate" : "Editar Equipo de Rescate");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(30));

        // Campos del formulario
        JTextField txtNombre = new JTextField(esNuevo ? "" : equipo.getNombre());
        JTextField txtResponsable = new JTextField(esNuevo ? "" : equipo.getResponsable());
        JTextField txtMiembros = new JTextField(esNuevo ? "" : String.valueOf(equipo.getMiembros()));

        EstilosUI.addFormField(panel, "Nombre del equipo:", txtNombre);
        EstilosUI.addFormField(panel, "Responsable:", txtResponsable);

        if (esNuevo) {
            EstilosUI.addFormField(panel, "Número de miembros:", txtMiembros);
        }

        panel.add(Box.createVerticalStrut(20));

        // Botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton(esNuevo ? "Guardar" : "Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        EstilosUI.stylePrimaryButton(btnGuardar);
        EstilosUI.styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String responsable = txtResponsable.getText().trim();

            if (nombre.isEmpty() || responsable.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Complete todos los campos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (esNuevo) {
                String miembrosStr = txtMiembros.getText().trim();
                if (miembrosStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Ingrese el número de miembros",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int miembros = Integer.parseInt(miembrosStr);
                    if (miembros <= 0) {
                        JOptionPane.showMessageDialog(dialog,
                                "El número de miembros debe ser mayor a 0",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    gestor.agregarEquipo(nombre, responsable, miembros);
                    JOptionPane.showMessageDialog(dialog,
                            "Equipo agregado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "El número de miembros debe ser un número válido",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                gestor.actualizarEquipo(equipo.getId(), nombre, responsable);
                JOptionPane.showMessageDialog(dialog,
                        "Equipo actualizado exitosamente",
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
