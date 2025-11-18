package edu.universidad.vista.dialogos;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo para asignar/desasignar equipos a zonas
 */
public class DialogoAsignarEquipo {

    private Zona zona;
    private GestorRecursosLocal gestor;
    private Runnable callback;
    private JDialog dialog;

    public DialogoAsignarEquipo(Zona zona, GestorRecursosLocal gestor, Runnable callback) {
        this.zona = zona;
        this.gestor = gestor;
        this.callback = callback;
    }

    public void mostrar() {
        dialog = new JDialog((Frame) null, "Asignar Equipos - " + zona.getNombre(), true);
        dialog.setSize(900, 450);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstilosUI.COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Gestión de Equipos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        // Panel de equipos dividido en dos columnas
        JPanel equiposPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        equiposPanel.setOpaque(false);

        // Equipos asignados
        JPanel asignadosPanel = EstilosUI.createCard("✅ Equipos Asignados a esta Zona");
        asignadosPanel.setLayout(new BoxLayout(asignadosPanel, BoxLayout.Y_AXIS));
        asignadosPanel.setPreferredSize(new Dimension(550, 200));

        actualizarPanelAsignados(asignadosPanel);

        JScrollPane scrollAsignados = new JScrollPane(asignadosPanel);
        scrollAsignados.setBorder(null);

        // Equipos disponibles
        JPanel disponiblesPanel = EstilosUI.createCard("➕ Equipos Disponibles");
        disponiblesPanel.setLayout(new BoxLayout(disponiblesPanel, BoxLayout.Y_AXIS));
        disponiblesPanel.setPreferredSize(new Dimension(550, 200));

        actualizarPanelDisponibles(disponiblesPanel);

        JScrollPane scrollDisponibles = new JScrollPane(disponiblesPanel);
        scrollDisponibles.setBorder(null);

        equiposPanel.add(scrollAsignados);
        equiposPanel.add(scrollDisponibles);

        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        EstilosUI.styleSecondaryButton(btnCerrar);
        btnCerrar.addActionListener(e -> {
            if (callback != null) {
                callback.run();
            }
            dialog.dispose();
        });

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(equiposPanel, BorderLayout.CENTER);
        panel.add(btnCerrar, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void actualizarPanelAsignados(JPanel panel) {
        // Limpiar contenido actual (excepto el título que está en el card)
        Component[] components = panel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i] instanceof JPanel || components[i] instanceof Box.Filler) {
                panel.remove(components[i]);
            }
        }

        if (zona.getEquiposAsignados().isEmpty()) {
            JLabel lblVacio = new JLabel("No hay equipos asignados");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblVacio.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
            panel.add(lblVacio);
        } else {
            for (EquipoRescate equipo : zona.getEquiposAsignados()) {
                panel.add(createEquipoItem(equipo, true));
                panel.add(Box.createVerticalStrut(5));
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private void actualizarPanelDisponibles(JPanel panel) {
        Component[] components = panel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i] instanceof JPanel || components[i] instanceof Box.Filler) {
                panel.remove(components[i]);
            }
        }

        List<EquipoRescate> equiposDisponibles = new ArrayList<>();
        for (EquipoRescate e : gestor.obtenerEquipos()) {
            if (!zona.getEquiposAsignados().contains(e)) {
                equiposDisponibles.add(e);
            }
        }

        if (equiposDisponibles.isEmpty()) {
            JLabel lblVacio = new JLabel("Todos los equipos están asignados");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblVacio.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
            panel.add(lblVacio);
        } else {
            for (EquipoRescate equipo : equiposDisponibles) {
                panel.add(createEquipoItem(equipo, false));
                panel.add(Box.createVerticalStrut(5));
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createEquipoItem(EquipoRescate equipo, boolean asignado) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setBorder(new CompoundBorder(
                new LineBorder(EstilosUI.COLOR_BORDER, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblNombre = new JLabel(equipo.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JLabel lblResponsable = new JLabel("Responsable: " + equipo.getResponsable());
        lblResponsable.setFont(EstilosUI.FONT_SMALL);
        lblResponsable.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);

        infoPanel.add(lblNombre);
        infoPanel.add(lblResponsable);

        JButton btnAccion;
        if (asignado) {
            btnAccion = new JButton("❌ Desasignar");
            btnAccion.setBackground(EstilosUI.COLOR_DANGER);
            btnAccion.setForeground(Color.WHITE);
            btnAccion.addActionListener(e -> {
                zona.removerEquipo(equipo);
                gestor.guardarTodo();

                // Actualizar ambos paneles
                Container parent = item.getParent();
                while (parent != null && !(parent instanceof JScrollPane)) {
                    parent = parent.getParent();
                }
                if (parent != null) {
                    JScrollPane scroll = (JScrollPane) parent;
                    JViewport viewport = scroll.getViewport();
                    if (viewport.getView() instanceof JPanel) {
                        JPanel panelAsignados = (JPanel) viewport.getView();
                        actualizarPanelAsignados(panelAsignados);
                    }
                }

                // Buscar y actualizar panel de disponibles
                Container dialogContent = dialog.getContentPane();
                actualizarTodosPaneles(dialogContent);

                JOptionPane.showMessageDialog(dialog,
                        "Equipo desasignado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            btnAccion = new JButton("➕ Asignar");
            btnAccion.setBackground(EstilosUI.COLOR_SUCCESS);
            btnAccion.setForeground(Color.WHITE);
            btnAccion.addActionListener(e -> {
                zona.asignarEquipo(equipo);
                gestor.guardarTodo();

                // Actualizar todos los paneles
                Container dialogContent = dialog.getContentPane();
                actualizarTodosPaneles(dialogContent);

                JOptionPane.showMessageDialog(dialog,
                        "Equipo asignado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }

        btnAccion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAccion.setBorderPainted(false);
        btnAccion.setFocusPainted(false);
        btnAccion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAccion.setPreferredSize(new Dimension(120, 35));

        item.add(infoPanel, BorderLayout.CENTER);
        item.add(btnAccion, BorderLayout.EAST);

        return item;
    }

    private void actualizarTodosPaneles(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                JViewport viewport = scroll.getViewport();
                Component view = viewport.getView();

                if (view instanceof JPanel) {
                    JPanel panel = (JPanel) view;
                    // Verificar si es panel de asignados o disponibles por el título
                    boolean esAsignados = false;
                    boolean esDisponibles = false;

                    for (Component c : panel.getComponents()) {
                        if (c instanceof JLabel) {
                            String text = ((JLabel) c).getText();
                            if (text.contains("Asignados")) esAsignados = true;
                            if (text.contains("Disponibles")) esDisponibles = true;
                        }
                    }

                    if (esAsignados) {
                        actualizarPanelAsignados(panel);
                    } else if (esDisponibles) {
                        actualizarPanelDisponibles(panel);
                    }
                }
            } else if (comp instanceof Container) {
                actualizarTodosPaneles((Container) comp);
            }
        }
    }
}
