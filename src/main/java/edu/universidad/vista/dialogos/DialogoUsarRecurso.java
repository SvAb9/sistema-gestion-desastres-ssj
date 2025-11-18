package edu.universidad.vista.dialogos;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoUsarRecurso {

    private Recurso recurso;
    private GestorRecursosLocal gestor;
    private Runnable callback;
    private JDialog dialog;

    public DialogoUsarRecurso(Recurso recurso, GestorRecursosLocal gestor, Runnable callback) {
        this.recurso = recurso;
        this.gestor = gestor;
        this.callback = callback;
    }

    public void mostrar() {
        dialog = new JDialog((Frame) null, "Usar Recurso", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(EstilosUI.COLOR_CARD);

        JLabel titulo = new JLabel("Usar Recursos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JLabel lblInfo = new JLabel(String.format(
                "<html><b>Recurso:</b> %s<br><b>Disponibles:</b> %d unidades</html>",
                recurso.getNombre(), recurso.getDisponible()));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblInfo.setOpaque(true);
        lblInfo.setBackground(new Color(239, 246, 255));

        panel.add(lblInfo);
        panel.add(Box.createVerticalStrut(20));

        JLabel lblCantidad = new JLabel("Cantidad a usar:");
        lblCantidad.setFont(EstilosUI.FONT_NORMAL);
        lblCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(
                1, 1, recurso.getDisponible() > 0 ? recurso.getDisponible() : 1, 1));
        spnCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        spnCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblCantidad);
        panel.add(Box.createVerticalStrut(8));
        panel.add(spnCantidad);
        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnConfirmar = new JButton("Confirmar Uso");
        JButton btnCancelar = new JButton("Cancelar");

        EstilosUI.stylePrimaryButton(btnConfirmar);
        EstilosUI.styleSecondaryButton(btnCancelar);

        btnConfirmar.addActionListener(e -> {
            int cantidad = (Integer) spnCantidad.getValue();

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(dialog, "La cantidad debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cantidad > recurso.getDisponible()) {
                JOptionPane.showMessageDialog(dialog, "No hay suficientes recursos disponibles", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            recurso.usar(cantidad);
            gestor.guardarTodo();

            JOptionPane.showMessageDialog(dialog,
                    String.format("Se usaron %d unidades de %s\n\nDisponibles ahora: %d",
                            cantidad, recurso.getNombre(), recurso.getDisponible()),
                    "Recursos Usados",
                    JOptionPane.INFORMATION_MESSAGE);

            if (callback != null) {
                callback.run();
            }
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnConfirmar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
