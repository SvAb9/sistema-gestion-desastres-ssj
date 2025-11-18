package edu.universidad.vista.dialogos;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoRecurso {

    private Recurso recurso;
    private GestorRecursosLocal gestor;
    private Runnable callback;
    private JDialog dialog;

    public DialogoRecurso(Recurso recurso, GestorRecursosLocal gestor, Runnable callback) {
        this.recurso = recurso;
        this.gestor = gestor;
        this.callback = callback;
    }

    public void mostrar() {
        boolean esNuevo = (recurso == null);
        String titulo = esNuevo ? "Agregar Nuevo Recurso" : "Editar Recurso";

        dialog = new JDialog((Frame) null, titulo, true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(EstilosUI.COLOR_CARD);

        JLabel lblTitulo = new JLabel(esNuevo ? "Nuevo Recurso" : "Editar Recurso");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField(esNuevo ? "" : recurso.getNombre());
        JTextField txtCantidad = new JTextField(esNuevo ? "" : String.valueOf(recurso.getDisponible()));

        EstilosUI.addFormField(panel, "Nombre del recurso:", txtNombre);
        EstilosUI.addFormField(panel, esNuevo ? "Cantidad disponible:" : "Nueva cantidad disponible:", txtCantidad);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton(esNuevo ? "Guardar" : "Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        EstilosUI.stylePrimaryButton(btnGuardar);
        EstilosUI.styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String cantidadStr = txtCantidad.getText().trim();

            if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int cantidad = Integer.parseInt(cantidadStr);
                if (cantidad < 0) {
                    JOptionPane.showMessageDialog(dialog, "La cantidad no puede ser negativa", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (esNuevo) {
                    gestor.agregarRecurso(nombre, cantidad);
                    JOptionPane.showMessageDialog(dialog, "Recurso agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    gestor.actualizarRecurso(recurso.getId(), nombre, cantidad);
                    JOptionPane.showMessageDialog(dialog, "Recurso actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }

                if (callback != null) {
                    callback.run();
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La cantidad debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
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
