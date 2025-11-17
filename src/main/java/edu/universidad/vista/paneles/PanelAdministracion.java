package edu.universidad.vista.paneles;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import edu.universidad.vista.dialogos.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de Administración con tabs para Equipos, Recursos y Zonas
 */
public class PanelAdministracion {

    private GestorRecursosLocal gestor;
    private Usuario usuarioActual;
    private Runnable sincronizarGrafo;

    private DefaultTableModel modeloEquipos;
    private DefaultTableModel modeloRecursos;
    private DefaultTableModel modeloZonas;

    private JTable tablaEquipos;
    private JTable tablaRecursos;
    private JTable tablaZonas;

    public PanelAdministracion(GestorRecursosLocal gestor, Usuario usuarioActual, Runnable sincronizarGrafo) {
        this.gestor = gestor;
        this.usuarioActual = usuarioActual;
        this.sincronizarGrafo = sincronizarGrafo;
    }

    public JPanel crear() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);

        JLabel title = new JLabel("Panel de Administración");
        title.setFont(EstilosUI.FONT_TITLE);
        title.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("Equipos de Rescate", createEquiposPanel());
        tabs.addTab("Recursos", createRecursosPanel());
        tabs.addTab("Zonas", createZonasPanel());

        panel.add(title, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PANEL DE EQUIPOS
    // ============================================

    private JPanel createEquiposPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Equipo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        EstilosUI.stylePrimaryButton(btnAgregar);
        EstilosUI.styleSecondaryButton(btnEditar);
        EstilosUI.styleSecondaryButton(btnEliminar);
        EstilosUI.styleSecondaryButton(btnRefresh);

        if (usuarioActual.soloLectura()) {
            btnAgregar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }

        toolbar.add(btnAgregar);
        toolbar.add(btnEditar);
        toolbar.add(btnEliminar);
        toolbar.add(btnRefresh);

        modeloEquipos = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Responsable", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEquipos = new JTable(modeloEquipos);
        tablaEquipos.setRowHeight(40);
        tablaEquipos.setFont(EstilosUI.FONT_NORMAL);
        tablaEquipos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaEquipos.setSelectionBackground(new Color(239, 246, 255));

        btnAgregar.addActionListener(e -> new DialogoEquipo(null, gestor, this::cargarTablaEquipos).mostrar());

        btnEditar.addActionListener(e -> {
            int selected = tablaEquipos.getSelectedRow();
            if (selected >= 0) {
                Long id = (Long) modeloEquipos.getValueAt(selected, 0);
                EquipoRescate equipo = buscarEquipoPorId(id);
                if (equipo != null) {
                    new DialogoEquipo(equipo, gestor, this::cargarTablaEquipos).mostrar();
                } else {
                    JOptionPane.showMessageDialog(null, "Equipo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un equipo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selected = tablaEquipos.getSelectedRow();
            if (selected >= 0) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de eliminar este equipo?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long id = (Long) modeloEquipos.getValueAt(selected, 0);
                    if (gestor.eliminarEquipo(id)) {
                        cargarTablaEquipos();
                        JOptionPane.showMessageDialog(null, "Equipo eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar equipo", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un equipo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> cargarTablaEquipos());

        cargarTablaEquipos();

        JScrollPane scroll = new JScrollPane(tablaEquipos);
        scroll.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void cargarTablaEquipos() {
        modeloEquipos.setRowCount(0);
        List<EquipoRescate> equipos = gestor.obtenerEquipos();
        for (EquipoRescate e : equipos) {
            modeloEquipos.addRow(new Object[]{
                    e.getId(),
                    e.getNombre(),
                    e.getResponsable(),
                    e.isDisponible() ? "Disponible" : "Asignado"
            });
        }
    }

    // Método auxiliar para buscar equipo por ID
    private EquipoRescate buscarEquipoPorId(Long id) {
        List<EquipoRescate> equipos = gestor.obtenerEquipos();
        for (EquipoRescate e : equipos) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    // ============================================
    // PANEL DE RECURSOS
    // ============================================

    private JPanel createRecursosPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Recurso");
        JButton btnEditar = new JButton("Editar");
        JButton btnUsar = new JButton("Usar Recursos");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        EstilosUI.stylePrimaryButton(btnAgregar);
        EstilosUI.styleSecondaryButton(btnEditar);
        EstilosUI.styleSecondaryButton(btnUsar);
        EstilosUI.styleSecondaryButton(btnEliminar);
        EstilosUI.styleSecondaryButton(btnRefresh);

        if (usuarioActual.soloLectura()) {
            btnAgregar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnUsar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }

        toolbar.add(btnAgregar);
        toolbar.add(btnEditar);
        toolbar.add(btnUsar);
        toolbar.add(btnEliminar);
        toolbar.add(btnRefresh);

        modeloRecursos = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Disponible", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRecursos = new JTable(modeloRecursos);
        tablaRecursos.setRowHeight(40);
        tablaRecursos.setFont(EstilosUI.FONT_NORMAL);
        tablaRecursos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaRecursos.setSelectionBackground(new Color(239, 246, 255));

        btnAgregar.addActionListener(e -> new DialogoRecurso(null, gestor, this::cargarTablaRecursos).mostrar());

        btnEditar.addActionListener(e -> {
            int selected = tablaRecursos.getSelectedRow();
            if (selected >= 0) {
                Long id = (Long) modeloRecursos.getValueAt(selected, 0);
                Recurso recurso = buscarRecursoPorId(id);
                if (recurso != null) {
                    new DialogoRecurso(recurso, gestor, this::cargarTablaRecursos).mostrar();
                } else {
                    JOptionPane.showMessageDialog(null, "Recurso no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un recurso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnUsar.addActionListener(e -> {
            int selected = tablaRecursos.getSelectedRow();
            if (selected >= 0) {
                Long id = (Long) modeloRecursos.getValueAt(selected, 0);
                Recurso recurso = buscarRecursoPorId(id);
                if (recurso != null) {
                    new DialogoUsarRecurso(recurso, gestor, this::cargarTablaRecursos).mostrar();
                } else {
                    JOptionPane.showMessageDialog(null, "Recurso no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un recurso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selected = tablaRecursos.getSelectedRow();
            if (selected >= 0) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de eliminar este recurso?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long id = (Long) modeloRecursos.getValueAt(selected, 0);
                    if (gestor.eliminarRecurso(id)) {
                        cargarTablaRecursos();
                        JOptionPane.showMessageDialog(null, "Recurso eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar recurso", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un recurso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> cargarTablaRecursos());

        cargarTablaRecursos();

        JScrollPane scroll = new JScrollPane(tablaRecursos);
        scroll.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void cargarTablaRecursos() {
        modeloRecursos.setRowCount(0);
        List<Recurso> recursos = gestor.obtenerRecursos();
        for (Recurso r : recursos) {
            modeloRecursos.addRow(new Object[]{
                    r.getId(),
                    r.getNombre(),
                    r.getDisponible(),
                    r.getTotal()
            });
        }
    }

    // Método auxiliar para buscar recurso por ID
    private Recurso buscarRecursoPorId(Long id) {
        List<Recurso> recursos = gestor.obtenerRecursos();
        for (Recurso r : recursos) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    // ============================================
    // PANEL DE ZONAS
    // ============================================

    private JPanel createZonasPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Zona");
        JButton btnEditar = new JButton("Editar");
        JButton btnAsignarEquipo = new JButton("Asignar Equipo");
        JButton btnRefresh = new JButton("Actualizar");

        EstilosUI.stylePrimaryButton(btnAgregar);
        EstilosUI.styleSecondaryButton(btnEditar);
        EstilosUI.styleSecondaryButton(btnAsignarEquipo);
        EstilosUI.styleSecondaryButton(btnRefresh);

        if (usuarioActual.soloLectura()) {
            btnAgregar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnAsignarEquipo.setEnabled(false);
        }

        toolbar.add(btnAgregar);
        toolbar.add(btnEditar);
        toolbar.add(btnAsignarEquipo);
        toolbar.add(btnRefresh);

        modeloZonas = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Estado", "Prioridad", "Equipos Asignados"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaZonas = new JTable(modeloZonas);
        tablaZonas.setRowHeight(40);
        tablaZonas.setFont(EstilosUI.FONT_NORMAL);
        tablaZonas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaZonas.setSelectionBackground(new Color(239, 246, 255));

        btnAgregar.addActionListener(e -> new DialogoZona(null, gestor, () -> {
            cargarTablaZonas();
            sincronizarGrafo.run();
        }).mostrar());

        btnEditar.addActionListener(e -> {
            int selected = tablaZonas.getSelectedRow();
            if (selected >= 0) {
                Long id = (Long) modeloZonas.getValueAt(selected, 0);
                Zona zona = buscarZonaPorId(id);
                if (zona != null) {
                    new DialogoZona(zona, gestor, () -> {
                        cargarTablaZonas();
                        sincronizarGrafo.run();
                    }).mostrar();
                } else {
                    JOptionPane.showMessageDialog(null, "Zona no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione una zona", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnAsignarEquipo.addActionListener(e -> {
            int selected = tablaZonas.getSelectedRow();
            if (selected >= 0) {
                Long id = (Long) modeloZonas.getValueAt(selected, 0);
                Zona zona = buscarZonaPorId(id);
                if (zona != null) {
                    new DialogoAsignarEquipo(zona, gestor, this::cargarTablaZonas).mostrar();
                } else {
                    JOptionPane.showMessageDialog(null, "Zona no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione una zona", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> {
            cargarTablaZonas();
            sincronizarGrafo.run();
        });

        cargarTablaZonas();

        JScrollPane scroll = new JScrollPane(tablaZonas);
        scroll.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void cargarTablaZonas() {
        modeloZonas.setRowCount(0);
        List<Zona> zonas = gestor.obtenerZonas();
        for (Zona z : zonas) {
            modeloZonas.addRow(new Object[]{
                    z.getId(),
                    z.getNombre(),
                    z.getEstado(),
                    z.getPrioridad(),
                    z.getEquiposAsignados().size()
            });
        }
    }

    // Método auxiliar para buscar zona por ID
    private Zona buscarZonaPorId(Long id) {
        List<Zona> zonas = gestor.obtenerZonas();
        for (Zona z : zonas) {
            if (z.getId().equals(id)) {
                return z;
            }
        }
        return null;
    }
}