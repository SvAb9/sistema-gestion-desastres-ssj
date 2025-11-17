package edu.universidad.vista.paneles;

import edu.universidad.modelo.*;
import edu.universidad.servicio.GestorEvacuaciones;
import edu.universidad.vista.EstilosUI;
import edu.universidad.vista.dialogos.DialogoRuta;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Evacuaciones con flujo simplificado y claro
 * Programar evacuación → Inicia automáticamente
 */
public class PanelRutas {

    private GestorRecursosLocal gestor;
    private GestorEvacuaciones gestorEvacuaciones;
    private Usuario usuarioActual;
    private Runnable sincronizarGrafo;

    private DefaultTableModel modeloRutas;
    private JTable tablaRutas;

    public PanelRutas(GestorRecursosLocal gestor, GestorEvacuaciones gestorEvacuaciones,
                      Usuario usuarioActual, Runnable sincronizarGrafo) {
        this.gestor = gestor;
        this.gestorEvacuaciones = gestorEvacuaciones;
        this.usuarioActual = usuarioActual;
        this.sincronizarGrafo = sincronizarGrafo;
    }

    public JPanel crear() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Panel de Rutas y Evacuación");
        title.setFont(EstilosUI.FONT_TITLE);
        title.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        tabsPanel.setOpaque(false);

        JButton btnRutas = new JButton("Rutas de Transporte");
        JButton btnEvacuacion = new JButton("Gestión de Evacuaciones");

        EstilosUI.styleSecondaryButton(btnRutas);
        EstilosUI.styleSecondaryButton(btnEvacuacion);

        btnRutas.setBackground(EstilosUI.COLOR_PRIMARY);
        btnRutas.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        contentPanel.add(createRutasTransportePanel(), "rutas");
        contentPanel.add(createEvacuacionesPanel(), "evacuacion");

        btnRutas.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "rutas");
            btnRutas.setBackground(EstilosUI.COLOR_PRIMARY);
            btnRutas.setForeground(Color.WHITE);
            btnEvacuacion.setBackground(EstilosUI.COLOR_CARD);
            btnEvacuacion.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);
        });

        btnEvacuacion.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "evacuacion");
            btnEvacuacion.setBackground(EstilosUI.COLOR_PRIMARY);
            btnEvacuacion.setForeground(Color.WHITE);
            btnRutas.setBackground(EstilosUI.COLOR_CARD);
            btnRutas.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);
        });

        tabsPanel.add(btnRutas);
        tabsPanel.add(btnEvacuacion);

        header.add(title, BorderLayout.WEST);
        header.add(tabsPanel, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // RUTAS DE TRANSPORTE
    // ============================================

    private JPanel createRutasTransportePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JPanel tableCard = EstilosUI.createCard("Gestión de Rutas de Transporte");
        tableCard.setLayout(new BorderLayout(15, 15));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("+ Nueva Ruta");
        EstilosUI.stylePrimaryButton(btnAgregar);

        if (!usuarioActual.soloLectura()) {
            btnAgregar.addActionListener(e -> new DialogoRuta(gestor, () -> {
                cargarTablaRutas();
                sincronizarGrafo.run();
            }).mostrar());
            toolbar.add(btnAgregar);
        }

        modeloRutas = new DefaultTableModel(
                new Object[]{"ID", "Origen", "Destino", "Distancia (km)", "Estado", "Acciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        tablaRutas = new JTable(modeloRutas);
        tablaRutas.setRowHeight(50);
        tablaRutas.setFont(EstilosUI.FONT_NORMAL);
        tablaRutas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tablaRutas.getColumn("Acciones").setCellRenderer(new ButtonRenderer());
        tablaRutas.getColumn("Acciones").setCellEditor(new ButtonEditor(new JCheckBox(), gestor,
                this::cargarTablaRutas, sincronizarGrafo, usuarioActual));

        tablaRutas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaRutas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaRutas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaRutas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaRutas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaRutas.getColumnModel().getColumn(5).setPreferredWidth(100);

        cargarTablaRutas();

        JScrollPane scroll = new JScrollPane(tablaRutas);
        scroll.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.add(toolbar, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        tableCard.add(content);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void cargarTablaRutas() {
        modeloRutas.setRowCount(0);
        List<Ruta> rutas = gestor.obtenerRutas();
        for (Ruta r : rutas) {
            modeloRutas.addRow(new Object[]{
                    r.getId(),
                    r.getOrigenZona(),
                    r.getDestinoZona(),
                    String.format("%.1f", r.getPeso()),
                    "Activa",
                    r.getId()
            });
        }
    }

    // ============================================
    // EVACUACIONES SIMPLIFICADAS
    // ============================================

    private JPanel createEvacuacionesPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);

        // Header con instrucciones claras
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Gestión de Evacuaciones");
        lblTitulo.setFont(EstilosUI.FONT_SUBTITLE);
        lblTitulo.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JLabel lblInstruccion = new JLabel(
                "<html><i>💡 Seleccione una zona, ingrese el número de personas y programe la evacuación.<br>" +
                        "El sistema iniciará automáticamente la evacuación si hay equipos disponibles.</i></html>"
        );
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstruccion.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitulo);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(lblInstruccion);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Panel principal con formulario
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(EstilosUI.COLOR_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Card de programación de evacuación
        JPanel cardEvacuacion = EstilosUI.createCard("Programar Nueva Evacuación");
        cardEvacuacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Selector de zona
        JLabel lblZona = new JLabel("Seleccionar Zona:");
        lblZona.setFont(EstilosUI.FONT_NORMAL);
        lblZona.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cboZonas = new JComboBox<>();
        for (Zona z : gestor.obtenerZonas()) {
            cboZonas.addItem(z.getNombre() + " (Prioridad: " + z.getPrioridad() + ")");
        }
        cboZonas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cboZonas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboZonas.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Número de personas
        JLabel lblPersonas = new JLabel("Número de personas a evacuar:");
        lblPersonas.setFont(EstilosUI.FONT_NORMAL);
        lblPersonas.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner spnPersonas = new JSpinner(new SpinnerNumberModel(100, 10, 10000, 50));
        spnPersonas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        spnPersonas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnPersonas.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón de programar
        JButton btnProgramar = new JButton(" Programar Evacuación");
        EstilosUI.stylePrimaryButton(btnProgramar);
        btnProgramar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnProgramar.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!usuarioActual.puedeCoordinarOperaciones()) {
            btnProgramar.setEnabled(false);
            btnProgramar.setToolTipText("No tiene permisos para coordinar operaciones");
        }

        btnProgramar.addActionListener(e -> {
            String zonaNombreCompleto = (String) cboZonas.getSelectedItem();
            if (zonaNombreCompleto == null) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar una zona",
                        "Zona Requerida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Extraer nombre de la zona (antes del paréntesis)
            String zonaNombre = zonaNombreCompleto.split(" \\(")[0];
            Zona zona = gestor.buscarZonaPorNombre(zonaNombre);

            if (zona == null) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Zona no encontrada",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int personas = (Integer) spnPersonas.getValue();

            // Programar evacuación
            gestorEvacuaciones.programarEvacuacion(zona, personas);

            // Buscar equipo disponible e iniciar automáticamente
            EquipoRescate equipoDisponible = null;
            for (EquipoRescate eq : gestor.obtenerEquipos()) {
                if (eq.isDisponible()) {
                    equipoDisponible = eq;
                    break;
                }
            }

            String mensajeResultado;
            if (equipoDisponible != null) {
                gestorEvacuaciones.iniciarSiguienteEvacuacion(equipoDisponible);
                mensajeResultado = String.format(
                        " EVACUACIÓN INICIADA EXITOSAMENTE\n\n" +
                                "╔═════════════════════════════════════════╗\n" +
                                "║     DETALLES DE LA EVACUACIÓN           ║\n" +
                                "╚═════════════════════════════════════════╝\n\n" +
                                "📍 Zona: %s\n" +
                                "👥 Personas: %d\n" +
                                "🚨 Prioridad: %d\n" +
                                "👨‍🚒 Equipo asignado: %s\n" +
                                "📊 Estado: EN PROCESO\n\n" +
                                "═════════════════════════════════════════\n\n" +
                                "💡 Vaya a Inicio para ver el contador\n" +
                                "   de evacuaciones actualizado.",
                        zona.getNombre(),
                        personas,
                        zona.getPrioridad(),
                        equipoDisponible.getNombre()
                );
            } else {
                mensajeResultado = String.format(
                        " EVACUACIÓN PROGRAMADA\n\n" +
                                "╔═════════════════════════════════════════╗\n" +
                                "║     DETALLES DE LA EVACUACIÓN           ║\n" +
                                "╚═════════════════════════════════════════╝\n\n" +
                                "📍 Zona: %s\n" +
                                "👥 Personas: %d\n" +
                                "🚨 Prioridad: %d\n" +
                                "📊 Estado: PENDIENTE\n\n" +
                                "═════════════════════════════════════════\n\n" +
                                "⚠️ No hay equipos disponibles en este momento.\n" +
                                "   La evacuación se iniciará cuando haya equipos libres.\n\n" +
                                "💡 Vaya a Inicio para ver el contador\n" +
                                "   de evacuaciones actualizado.",
                        zona.getNombre(),
                        personas,
                        zona.getPrioridad()
                );
            }

            JOptionPane.showMessageDialog(mainPanel,
                    mensajeResultado,
                    "Evacuación Registrada",
                    JOptionPane.INFORMATION_MESSAGE);

            // Resetear formulario
            if (cboZonas.getItemCount() > 0) {
                cboZonas.setSelectedIndex(0);
            }
            spnPersonas.setValue(100);
        });

        // Agregar componentes al card
        cardEvacuacion.add(lblZona);
        cardEvacuacion.add(Box.createVerticalStrut(8));
        cardEvacuacion.add(cboZonas);
        cardEvacuacion.add(Box.createVerticalStrut(20));
        cardEvacuacion.add(lblPersonas);
        cardEvacuacion.add(Box.createVerticalStrut(8));
        cardEvacuacion.add(spnPersonas);
        cardEvacuacion.add(Box.createVerticalStrut(30));
        cardEvacuacion.add(btnProgramar);

        contentPanel.add(cardEvacuacion);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);

        return mainPanel;
    }

    // ============================================
    // RENDERER Y EDITOR PARA BOTONES EN TABLA
    // ============================================

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText("Eliminar");
            setBackground(EstilosUI.COLOR_DANGER);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String rutaId;
        private boolean clicked;
        private GestorRecursosLocal gestor;
        private Runnable callback;
        private Runnable sincronizarGrafo;
        private Usuario usuario;

        public ButtonEditor(JCheckBox checkBox, GestorRecursosLocal gestor,
                            Runnable callback, Runnable sincronizarGrafo, Usuario usuario) {
            super(checkBox);
            this.gestor = gestor;
            this.callback = callback;
            this.sincronizarGrafo = sincronizarGrafo;
            this.usuario = usuario;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            rutaId = value.toString();
            button.setText("Eliminar");
            button.setBackground(EstilosUI.COLOR_DANGER);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                if (!usuario.soloLectura()) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "¿Está seguro que desea eliminar esta ruta?",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        gestor.eliminarRuta(Long.valueOf(rutaId));
                        callback.run();
                        sincronizarGrafo.run();
                        JOptionPane.showMessageDialog(null,
                                "Ruta eliminada exitosamente\nEl mapa se ha actualizado",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No tiene permisos para eliminar rutas",
                            "Acceso Denegado",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return rutaId;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}