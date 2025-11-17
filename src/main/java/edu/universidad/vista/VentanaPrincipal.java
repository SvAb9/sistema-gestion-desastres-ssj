package edu.universidad.vista;

import edu.universidad.estructura.ArbolDistribucion;
import edu.universidad.modelo.*;
import edu.universidad.servicio.GestorEvacuaciones;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Ventana Principal - Sistema de Gestión de Desastres
 *
 */
public class VentanaPrincipal extends JFrame {

    // ============================================
    // COLORES DEL DISEÑO
    // ============================================
    private static final Color COLOR_PRIMARY = new Color(67, 97, 238);
    private static final Color COLOR_BACKGROUND = new Color(249, 250, 251);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(229, 231, 235);
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);
    private static final Color COLOR_WARNING = new Color(245, 158, 11);
    private static final Color COLOR_DANGER = new Color(239, 68, 68);
    private static final Color COLOR_INFO = new Color(59, 130, 246);

    // ============================================
    // ATRIBUTOS
    // ============================================
    private Grafo grafo;
    private MapPanel mapPanel;
    private JPanel centerContainer;
    private String currentView = "inicio";
    private Usuario usuarioActual;
    private GestorRecursosLocal gestor;
    private GestorEvacuaciones gestorEvacuaciones;

    // Modelos de tablas
    private DefaultTableModel modeloRecursos;
    private DefaultTableModel modeloEquipos;
    private DefaultTableModel modeloRutas;
    private DefaultTableModel modeloZonas;

    // ============================================
    // CONSTRUCTOR
    // ============================================
    public VentanaPrincipal(Grafo grafo, Usuario usuario) {
        super("DesaRecu - Sistema de Gestión de Desastres");
        this.grafo = grafo;
        this.usuarioActual = usuario;
        this.gestor = GestorRecursosLocal.getInstance();
        this.gestorEvacuaciones = new GestorEvacuaciones();

        sincronizarGrafoConRutas();
        initUI();
    }

    // ============================================
    // SINCRONIZACIÓN DEL GRAFO
    // ============================================
    private void sincronizarGrafoConRutas() {
        System.out.println("Sincronizando grafo con rutas...");

        grafo = new Grafo();

        for (Zona zona : gestor.obtenerZonas()) {
            if (grafo.getNodo(zona.getNombre()) == null) {
                grafo.agregarNodo(new Nodo(zona.getNombre()));
            }
        }

        for (Ruta ruta : gestor.obtenerRutas()) {
            if (grafo.getNodo(ruta.getOrigenZona()) == null) {
                grafo.agregarNodo(new Nodo(ruta.getOrigenZona()));
            }
            if (grafo.getNodo(ruta.getDestinoZona()) == null) {
                grafo.agregarNodo(new Nodo(ruta.getDestinoZona()));
            }

            grafo.agregarArista(ruta.getOrigenZona(), ruta.getDestinoZona(), ruta.getPeso());
        }

        if (mapPanel != null) {
            mapPanel.actualizarGrafo(grafo);
        }

        System.out.println("Grafo sincronizado: " + grafo.getNodos().size() + " nodos, " +
                grafo.getAristas().size() + " aristas");
    }

    // ============================================
    // INICIALIZACIÓN DE LA UI
    // ============================================
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);

        centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(COLOR_BACKGROUND);
        centerContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(centerContainer, BorderLayout.CENTER);

        mostrarInicio();
        setVisible(true);
    }

    // ============================================
    // CREACIÓN DEL HEADER
    // ============================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(20, 40, 20, 40)
        ));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JLabel logo = new JLabel("DesaRecu");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(COLOR_PRIMARY);

        JLabel subtitle = new JLabel("Coordinación y gestión de recursos, evacuaciones y equipos de rescate");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(COLOR_TEXT_SECONDARY);

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(subtitle);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel lblUsuario = new JLabel(usuarioActual.getNombre() + " (" + usuarioActual.getRol().getNombre() + ")");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsuario.setForeground(COLOR_TEXT_SECONDARY);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        styleSecondaryButton(btnCerrarSesion);
        btnCerrarSesion.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginView();
                dispose();
            }
        });

        userPanel.add(lblUsuario);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(btnCerrarSesion);

        topSection.add(logoPanel, BorderLayout.WEST);
        topSection.add(userPanel, BorderLayout.EAST);

        JPanel navPanel = createNavigation();

        JPanel headerContent = new JPanel(new BorderLayout(0, 15));
        headerContent.setOpaque(false);
        headerContent.add(topSection, BorderLayout.NORTH);
        headerContent.add(navPanel, BorderLayout.CENTER);

        header.add(headerContent);
        return header;
    }

    // ============================================
    // NAVEGACIÓN
    // ============================================
    private JPanel createNavigation() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nav.setOpaque(false);

        String[][] menuItems = {
                {"Inicio", "inicio"},
                {"Administración", "admin"},
                {"Rutas", "rutas"},
                {"Estadísticas", "estadisticas"},
                {"Distribución", "distribucion"},
                {"Mapa", "mapa"}
        };

        for (String[] item : menuItems) {
            JButton btn = createNavButton(item[0], item[1]);
            nav.add(btn);
        }

        return nav;
    }


    private JButton createNavButton(String text, String view) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(COLOR_TEXT_SECONDARY);
        btn.setBackground(COLOR_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 45));

        if (view.equals(currentView)) {
            btn.setForeground(COLOR_PRIMARY);
            btn.setBackground(new Color(239, 246, 255));
        }

        btn.addActionListener(e -> {
            currentView = view;
            navegarA(view);
            actualizarNavegacion();
        });

        return btn;
    }

    private void actualizarNavegacion() {
        try {
            Component headerComponent = getContentPane().getComponent(0);
            if (headerComponent instanceof JPanel) {
                JPanel header = (JPanel) headerComponent;
                Component headerContent = header.getComponent(0);
                if (headerContent instanceof JPanel) {
                    JPanel content = (JPanel) headerContent;
                    if (content.getComponentCount() > 1) {
                        Component navComponent = content.getComponent(1);
                        if (navComponent instanceof JPanel) {
                            JPanel navPanel = (JPanel) navComponent;
                            for (Component c : navPanel.getComponents()) {
                                if (c instanceof JButton) {
                                    JButton btn = (JButton) c;
                                    String btnText = btn.getText().toLowerCase();
                                    boolean isCurrentView = btnText.contains(currentView);

                                    if (isCurrentView) {
                                        btn.setForeground(COLOR_PRIMARY);
                                        btn.setBackground(new Color(239, 246, 255));
                                    } else {
                                        btn.setForeground(COLOR_TEXT_SECONDARY);
                                        btn.setBackground(COLOR_CARD);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar navegación: " + e.getMessage());
        }
    }

    private void navegarA(String seccion) {
        centerContainer.removeAll();

        switch (seccion) {
            case "inicio":
                mostrarInicio();
                break;
            case "admin":
                mostrarAdministracion();
                break;
            case "rutas":
                mostrarRutas();
                break;
            case "estadisticas":
                mostrarEstadisticas();
                break;
            case "distribucion":
                mostrarDistribucion();
                break;
            case "mapa":
                mostrarMapa();
                break;
        }

        centerContainer.revalidate();
        centerContainer.repaint();
    }

    // ============================================
    // VISTA: INICIO
    // ============================================
    private void mostrarInicio() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_BACKGROUND);

        JPanel alerta = createAlertBanner(
                "Estado de Emergencia Activa",
                "Se están coordinando operaciones de rescate y evacuación en zonas afectadas.",
                COLOR_DANGER
        );
        panel.add(alerta);
        panel.add(Box.createVerticalStrut(20));

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        statsGrid.add(createStatCard("PA", "Personas Afectadas", "15,420",
                "Total en zonas de emergencia", COLOR_DANGER));
        statsGrid.add(createStatCard("EV", "Evacuados", "8,350",
                "Trasladados a refugios", COLOR_INFO));
        statsGrid.add(createStatCard("RD", "Recursos Distribuidos", String.valueOf(calcularRecursosDistribuidos()),
                "Unidades entregadas", COLOR_SUCCESS));
        statsGrid.add(createStatCard("EA", "Equipos Activos", String.valueOf(gestor.obtenerEquipos().size()),
                "Equipos desplegados", COLOR_PRIMARY));

        panel.add(statsGrid);
        panel.add(Box.createVerticalStrut(25));

        panel.add(createProgresoEvacuacionPanel());
        panel.add(Box.createVerticalStrut(25));

        JPanel mainContent = new JPanel(new GridLayout(1, 2, 30, 0));
        mainContent.setOpaque(false);
        mainContent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));

        mainContent.add(createZonasEmergenciaPanel());
        mainContent.add(createRecursosDisponiblesPanel());

        panel.add(mainContent);
        panel.add(Box.createVerticalStrut(25));

        panel.add(createActividadRecientePanel());
        panel.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        centerContainer.add(scroll);
    }

    private int calcularRecursosDistribuidos() {
        return gestor.obtenerRecursos().stream()
                .mapToInt(Recurso::getUsado)
                .sum();
    }

    // ============================================
    // COMPONENTES DE LA VISTA INICIO
    // ============================================

    private JPanel createAlertBanner(String title, String message, Color color) {
        JPanel alert = new JPanel(new BorderLayout(15, 0));
        alert.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        alert.setBorder(new CompoundBorder(
                new LineBorder(color, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        alert.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(color.darker());

        JLabel lblMessage = new JLabel(message);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMessage.setForeground(color.darker());

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblMessage);

        alert.add(textPanel, BorderLayout.CENTER);
        return alert;
    }

    private JPanel createStatCard(String icon, String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(60, 60));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(color);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(COLOR_TEXT_SECONDARY);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(5));
        content.add(lblValue);
        content.add(Box.createVerticalStrut(3));
        content.add(lblSubtitle);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createProgresoEvacuacionPanel() {
        JPanel panel = createCard("Progreso de Evacuación General");
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitulo = new JLabel("Evacuación completada");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblProgreso = new JLabel("54.2%");
        lblProgreso.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblProgreso.setForeground(COLOR_INFO);

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblProgreso, BorderLayout.EAST);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(54);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        progressBar.setForeground(COLOR_INFO);
        progressBar.setBackground(new Color(229, 231, 235));

        JLabel lblDetalle = new JLabel("8.350 de 15.420 personas evacuadas");
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalle.setForeground(COLOR_TEXT_SECONDARY);

        panel.add(header);
        panel.add(Box.createVerticalStrut(10));
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblDetalle);
        return panel;
    }

    private JPanel createZonasEmergenciaPanel() {
        JPanel panel = createCard("Zonas de Emergencia");
        List<Zona> zonas = gestor.obtenerZonas();
        for (Zona zona : zonas) {
            panel.add(createZonaItem(zona));
            panel.add(Box.createVerticalStrut(10));
        }
        return panel;
    }

    private JPanel createZonaItem(Zona zona) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        Color color = getColorPrioridad(zona.getPrioridad());

        JPanel indicator = new JPanel();
        indicator.setBackground(color);
        indicator.setPreferredSize(new Dimension(8, 50));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblNombre = new JLabel(zona.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblInfo = new JLabel(zona.getEstado() + " • Prioridad: " + zona.getPrioridad());
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(COLOR_TEXT_SECONDARY);

        content.add(lblNombre);
        content.add(lblInfo);

        JLabel badge = createBadge(getNivelPrioridad(zona.getPrioridad()), color);

        item.add(indicator, BorderLayout.WEST);
        item.add(content, BorderLayout.CENTER);
        item.add(badge, BorderLayout.EAST);
        return item;
    }

    private JPanel createRecursosDisponiblesPanel() {
        JPanel panel = createCard("Recursos Disponibles");
        panel.setLayout(new GridLayout(2, 2, 15, 15));

        List<Recurso> recursos = gestor.obtenerRecursos();
        int[] categorias = {0, 0, 0, 0};

        for (Recurso r : recursos) {
            String nombre = r.getNombre().toLowerCase();
            if (nombre.contains("kit") || nombre.contains("médico")) {
                categorias[0] += r.getDisponible();
            } else if (nombre.contains("comida") || nombre.contains("ración") || nombre.contains("alimento")) {
                categorias[1] += r.getDisponible();
            } else if (nombre.contains("agua")) {
                categorias[2] += r.getDisponible();
            } else if (nombre.contains("manta")) {
                categorias[3] += r.getDisponible();
            }
        }

        panel.add(createRecursoCard(String.valueOf(categorias[0]), "Kits Médicos", new Color(59, 130, 246)));
        panel.add(createRecursoCard(String.valueOf(categorias[1]), "Raciones de Comida", new Color(34, 197, 94)));
        panel.add(createRecursoCard(String.valueOf(categorias[2]), "Litros de Agua", new Color(14, 165, 233)));
        panel.add(createRecursoCard(String.valueOf(categorias[3]), "Mantas", new Color(168, 85, 247)));
        return panel;
    }

    private JPanel createRecursoCard(String cantidad, String nombre, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        card.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel lblCantidad = new JLabel(cantidad);
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblCantidad.setForeground(color);
        lblCantidad.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNombre.setForeground(color.darker());
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblCantidad);
        card.add(Box.createVerticalStrut(5));
        card.add(lblNombre);
        return card;
    }

    private JPanel createActividadRecientePanel() {
        JPanel panel = createCard("Actividad Reciente");
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        panel.add(createActividadItem("Evacuación completada en Sector A3", "Hace 15 minutos", COLOR_SUCCESS));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createActividadItem("Equipo de rescate desplegado a Zona Norte", "Hace 32 minutos", COLOR_INFO));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createActividadItem("Alerta de riesgo elevado en Zona Centro", "Hace 1 hora", COLOR_WARNING));
        return panel;
    }

    private JPanel createActividadItem(String texto, String tiempo, Color color) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel leftBar = new JPanel();
        leftBar.setBackground(color);
        leftBar.setPreferredSize(new Dimension(4, 55));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTexto.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblTiempo = new JLabel(tiempo);
        lblTiempo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTiempo.setForeground(COLOR_TEXT_SECONDARY);

        content.add(lblTexto);
        content.add(lblTiempo);

        item.add(leftBar, BorderLayout.WEST);
        item.add(content, BorderLayout.CENTER);
        return item;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        if (title != null && !title.isEmpty()) {
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTitle.setForeground(COLOR_TEXT_PRIMARY);
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(lblTitle);
            card.add(Box.createVerticalStrut(20));
        }
        return card;
    }

    private JLabel createBadge(String text, Color color) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(color);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));
        return badge;
    }

    // ============================================
    // VISTA: ADMINISTRACIÓN
    // ============================================

    private void mostrarAdministracion() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel title = new JLabel("Panel de Administración");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_PRIMARY);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("Equipos de Rescate", createEquiposPanel());
        tabs.addTab("Recursos", createRecursosAdminPanel());
        tabs.addTab("Zonas", createZonasPanel());

        panel.add(title, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);

        centerContainer.add(panel);
    }

    // ============================================
    // PANEL DE EQUIPOS
    // ============================================

    private JPanel createEquiposPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Equipo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        stylePrimaryButton(btnAgregar);
        styleSecondaryButton(btnEditar);
        styleSecondaryButton(btnEliminar);
        styleSecondaryButton(btnRefresh);

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

        JTable tabla = new JTable(modeloEquipos);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnAgregar.addActionListener(e -> mostrarDialogoAgregarEquipo());
        btnEditar.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                mostrarDialogoEditarEquipo(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un equipo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnEliminar.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                eliminarEquipo(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un equipo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnRefresh.addActionListener(e -> cargarTablaEquipos());

        cargarTablaEquipos();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));

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

    private void mostrarDialogoAgregarEquipo() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Equipo", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Nuevo Equipo de Rescate");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField();
        JTextField txtResponsable = new JTextField();
        JTextField txtMiembros = new JTextField();

        addFormField(panel, "Nombre del equipo:", txtNombre);
        addFormField(panel, "Responsable:", txtResponsable);
        addFormField(panel, "Número de miembros:", txtMiembros);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String responsable = txtResponsable.getText().trim();
            String miembrosStr = txtMiembros.getText().trim();

            if (nombre.isEmpty() || responsable.isEmpty() || miembrosStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int miembros = Integer.parseInt(miembrosStr);
                gestor.agregarEquipo(nombre, responsable, miembros);
                cargarTablaEquipos();
                JOptionPane.showMessageDialog(dialog, "Equipo agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El número de miembros debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void mostrarDialogoEditarEquipo(int row) {
        Long id = (Long) modeloEquipos.getValueAt(row, 0);
        String nombre = (String) modeloEquipos.getValueAt(row, 1);
        String responsable = (String) modeloEquipos.getValueAt(row, 2);

        JDialog dialog = new JDialog(this, "Editar Equipo", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Editar Equipo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField(nombre);
        JTextField txtResponsable = new JTextField(responsable);

        addFormField(panel, "Nombre del equipo:", txtNombre);
        addFormField(panel, "Responsable:", txtResponsable);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoResponsable = txtResponsable.getText().trim();

            if (nuevoNombre.isEmpty() || nuevoResponsable.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            gestor.actualizarEquipo(id, nuevoNombre, nuevoResponsable);
            cargarTablaEquipos();
            JOptionPane.showMessageDialog(dialog, "Equipo actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void eliminarEquipo(int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este equipo?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) modeloEquipos.getValueAt(row, 0);
            gestor.eliminarEquipo(id);
            cargarTablaEquipos();
            JOptionPane.showMessageDialog(this, "Equipo eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ============================================
    // PANEL DE RECURSOS
    // ============================================


    private JPanel createRecursosAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Recurso");
        JButton btnEditar = new JButton("Editar");
        JButton btnUsar = new JButton("Usar Recursos");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        stylePrimaryButton(btnAgregar);
        styleSecondaryButton(btnEditar);
        styleSecondaryButton(btnUsar);
        styleSecondaryButton(btnEliminar);
        styleSecondaryButton(btnRefresh);

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
                new Object[]{"ID", "Nombre", "Disponible", "Usado", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloRecursos);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setSelectionBackground(new Color(239, 246, 255));

        btnAgregar.addActionListener(e -> mostrarDialogoAgregarRecurso());
        btnEditar.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                mostrarDialogoEditarRecurso(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un recurso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Botón para usar recursos
        btnUsar.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                mostrarDialogoUsarRecurso(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un recurso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                eliminarRecurso(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un recurso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnRefresh.addActionListener(e -> cargarTablaRecursos());

        cargarTablaRecursos();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Diálogo para usar recursos
     */
    private void mostrarDialogoUsarRecurso(int row) {
        Long id = (Long) modeloRecursos.getValueAt(row, 0);
        String nombre = (String) modeloRecursos.getValueAt(row, 1);
        int disponible = (Integer) modeloRecursos.getValueAt(row, 2);

        JDialog dialog = new JDialog(this, "Usar Recurso", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Usar Recursos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        // Info del recurso
        JLabel lblInfo = new JLabel(String.format("<html><b>Recurso:</b> %s<br><b>Disponibles:</b> %d unidades</html>",
                nombre, disponible));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblInfo.setOpaque(true);
        lblInfo.setBackground(new Color(239, 246, 255));

        panel.add(lblInfo);
        panel.add(Box.createVerticalStrut(20));

        // Campo cantidad a usar
        JLabel lblCantidad = new JLabel("Cantidad a usar:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, disponible, 1));
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

        stylePrimaryButton(btnConfirmar);
        styleSecondaryButton(btnCancelar);

        btnConfirmar.addActionListener(e -> {
            int cantidad = (Integer) spnCantidad.getValue();

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(dialog,
                        "La cantidad debe ser mayor a 0",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cantidad > disponible) {
                JOptionPane.showMessageDialog(dialog,
                        "No hay suficientes recursos disponibles",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar el recurso y usar
            for (Recurso r : gestor.obtenerRecursos()) {
                if (r.getId().equals(id)) {
                    r.usar(cantidad);
                    gestor.guardarTodo(); // Guardar cambios
                    cargarTablaRecursos();

                    JOptionPane.showMessageDialog(dialog,
                            String.format("Se usaron %d unidades de %s\n\nDisponibles ahora: %d",
                                    cantidad, nombre, r.getDisponible()),
                            "Recursos Usados",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                    return;
                }
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnConfirmar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void cargarTablaRecursos() {
        modeloRecursos.setRowCount(0);
        List<Recurso> recursos = gestor.obtenerRecursos();
        for (Recurso r : recursos) {
            modeloRecursos.addRow(new Object[]{
                    r.getId(),
                    r.getNombre(),
                    r.getDisponible(),
                    r.getUsado(),
                    r.getTotal()
            });
        }
    }

    private void mostrarDialogoAgregarRecurso() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Recurso", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Nuevo Recurso");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField();
        JTextField txtCantidad = new JTextField();

        addFormField(panel, "Nombre del recurso:", txtNombre);
        addFormField(panel, "Cantidad disponible:", txtCantidad);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String cantidadStr = txtCantidad.getText().trim();

            if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int cantidad = Integer.parseInt(cantidadStr);
                gestor.agregarRecurso(nombre, cantidad);
                cargarTablaRecursos();
                JOptionPane.showMessageDialog(dialog, "Recurso agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La cantidad debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void mostrarDialogoEditarRecurso(int row) {
        Long id = (Long) modeloRecursos.getValueAt(row, 0);
        String nombre = (String) modeloRecursos.getValueAt(row, 1);
        int disponible = (Integer) modeloRecursos.getValueAt(row, 2);

        JDialog dialog = new JDialog(this, "Editar Recurso", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Editar Recurso");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField(nombre);
        JTextField txtCantidad = new JTextField(String.valueOf(disponible));

        addFormField(panel, "Nombre del recurso:", txtNombre);
        addFormField(panel, "Cantidad disponible:", txtCantidad);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevaCantidadStr = txtCantidad.getText().trim();

            if (nuevoNombre.isEmpty() || nuevaCantidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);
                gestor.actualizarRecurso(id, nuevoNombre, nuevaCantidad);
                cargarTablaRecursos();
                JOptionPane.showMessageDialog(dialog, "Recurso actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La cantidad debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void eliminarRecurso(int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este recurso?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) modeloRecursos.getValueAt(row, 0);
            gestor.eliminarRecurso(id);
            cargarTablaRecursos();
            JOptionPane.showMessageDialog(this, "Recurso eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ============================================
    // PANEL DE ZONAS
    // ============================================

    private JPanel createZonasPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Zona");
        JButton btnEditar = new JButton("Editar");
        JButton btnAsignarEquipo = new JButton("Asignar Equipo");
        JButton btnRefresh = new JButton("Actualizar");

        stylePrimaryButton(btnAgregar);
        styleSecondaryButton(btnEditar);
        styleSecondaryButton(btnAsignarEquipo);
        styleSecondaryButton(btnRefresh);

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

        JTable tabla = new JTable(modeloZonas);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnAgregar.addActionListener(e -> mostrarDialogoAgregarZona());
        btnEditar.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                mostrarDialogoEditarZona(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una zona", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Asignar equipos
        btnAsignarEquipo.addActionListener(e -> {
            int selected = tabla.getSelectedRow();
            if (selected >= 0) {
                mostrarDialogoAsignarEquipo(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una zona", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> {
            cargarTablaZonas();
            sincronizarGrafoConRutas();
        });

        cargarTablaZonas();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Diálogo para asignar/desasignar equipos a una zona
     */
    private void mostrarDialogoAsignarEquipo(int row) {
        Long zonaId = (Long) modeloZonas.getValueAt(row, 0);
        String zonaNombre = (String) modeloZonas.getValueAt(row, 1);

        // Buscar la zona
        Zona zona = null;
        for (Zona z : gestor.obtenerZonas()) {
            if (z.getId().equals(zonaId)) {
                zona = z;
                break;
            }
        }

        if (zona == null) return;

        final Zona zonaFinal = zona;

        JDialog dialog = new JDialog(this, "Asignar Equipos - " + zonaNombre, true);
        dialog.setSize(900, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Gestión de Equipos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(COLOR_TEXT_PRIMARY);

        // Panel de equipos disponibles
        JPanel equiposPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        equiposPanel.setOpaque(false);

        // Equipos asignados
        JPanel asignadosPanel = createCard("Equipos Asignados a esta Zona");
        asignadosPanel.setLayout(new BoxLayout(asignadosPanel, BoxLayout.Y_AXIS));
        asignadosPanel.setPreferredSize(new Dimension(550, 200));

        if (zonaFinal.getEquiposAsignados().isEmpty()) {
            JLabel lblVacio = new JLabel("No hay equipos asignados");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblVacio.setForeground(COLOR_TEXT_SECONDARY);
            asignadosPanel.add(lblVacio);
        } else {
            for (EquipoRescate equipo : zonaFinal.getEquiposAsignados()) {
                JPanel equipoItem = createEquipoItem(equipo, true, zonaFinal, dialog);
                asignadosPanel.add(equipoItem);
                asignadosPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scrollAsignados = new JScrollPane(asignadosPanel);
        scrollAsignados.setBorder(null);

        // Equipos disponibles
        JPanel disponiblesPanel = createCard("Equipos Disponibles");
        disponiblesPanel.setLayout(new BoxLayout(disponiblesPanel, BoxLayout.Y_AXIS));
        disponiblesPanel.setPreferredSize(new Dimension(550, 200));

        List<EquipoRescate> equiposDisponibles = new ArrayList<>();
        for (EquipoRescate e : gestor.obtenerEquipos()) {
            if (!zonaFinal.getEquiposAsignados().contains(e)) {
                equiposDisponibles.add(e);
            }
        }

        if (equiposDisponibles.isEmpty()) {
            JLabel lblVacio = new JLabel("Todos los equipos están asignados");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblVacio.setForeground(COLOR_TEXT_SECONDARY);
            disponiblesPanel.add(lblVacio);
        } else {
            for (EquipoRescate equipo : equiposDisponibles) {
                JPanel equipoItem = createEquipoItem(equipo, false, zonaFinal, dialog);
                disponiblesPanel.add(equipoItem);
                disponiblesPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scrollDisponibles = new JScrollPane(disponiblesPanel);
        scrollDisponibles.setBorder(null);

        equiposPanel.add(scrollAsignados);
        equiposPanel.add(scrollDisponibles);

        JButton btnCerrar = new JButton("Cerrar");
        styleSecondaryButton(btnCerrar);
        btnCerrar.addActionListener(e -> {
            cargarTablaZonas();
            dialog.dispose();
        });

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(equiposPanel, BorderLayout.CENTER);
        panel.add(btnCerrar, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    /**
     * Crea un item de equipo con botón de asignar/desasignar
     */
    private JPanel createEquipoItem(EquipoRescate equipo, boolean asignado, Zona zona, JDialog parentDialog) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblNombre = new JLabel(equipo.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblResponsable = new JLabel("Responsable: " + equipo.getResponsable());
        lblResponsable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblResponsable.setForeground(COLOR_TEXT_SECONDARY);

        infoPanel.add(lblNombre);
        infoPanel.add(lblResponsable);

        JButton btnAccion;
        if (asignado) {
            btnAccion = new JButton("❌ Desasignar");
            btnAccion.setBackground(COLOR_DANGER);
            btnAccion.setForeground(Color.WHITE);
            btnAccion.addActionListener(e -> {
                zona.removerEquipo(equipo);
                gestor.guardarTodo();
                JOptionPane.showMessageDialog(parentDialog,
                        "Equipo desasignado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                parentDialog.dispose();
                mostrarDialogoAsignarEquipo(obtenerIndiceZona(zona.getId()));
            });
        } else {
            btnAccion = new JButton("➕ Asignar");
            btnAccion.setBackground(COLOR_SUCCESS);
            btnAccion.setForeground(Color.WHITE);
            btnAccion.addActionListener(e -> {
                zona.asignarEquipo(equipo);
                gestor.guardarTodo();
                JOptionPane.showMessageDialog(parentDialog,
                        "Equipo asignado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                parentDialog.dispose();
                mostrarDialogoAsignarEquipo(obtenerIndiceZona(zona.getId()));
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

    /**
     * Obtiene el índice de una zona en la tabla
     */
    private int obtenerIndiceZona(Long zonaId) {
        for (int i = 0; i < modeloZonas.getRowCount(); i++) {
            if (modeloZonas.getValueAt(i, 0).equals(zonaId)) {
                return i;
            }
        }
        return -1;
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

    private void mostrarDialogoAgregarZona() {
        JDialog dialog = new JDialog(this, "Agregar Nueva Zona", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Nueva Zona Afectada");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField();
        JComboBox<String> cboEstado = new JComboBox<>(new String[]{"normal", "afectada", "evacuando", "evacuada"});
        JSpinner spnPrioridad = new JSpinner(new SpinnerNumberModel(50, 0, 100, 5));

        addFormField(panel, "Nombre de la zona:", txtNombre);
        addFormField(panel, "Estado:", cboEstado);
        addFormField(panel, "Prioridad (0-100):", spnPrioridad);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String estado = (String) cboEstado.getSelectedItem();
            int prioridad = (Integer) spnPrioridad.getValue();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ingrese el nombre de la zona", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            gestor.agregarZona(nombre, estado, prioridad);
            cargarTablaZonas();
            sincronizarGrafoConRutas();
            JOptionPane.showMessageDialog(dialog, "Zona agregada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void mostrarDialogoEditarZona(int row) {
        Long id = (Long) modeloZonas.getValueAt(row, 0);
        String nombre = (String) modeloZonas.getValueAt(row, 1);
        String estado = (String) modeloZonas.getValueAt(row, 2);
        int prioridad = (Integer) modeloZonas.getValueAt(row, 3);

        JDialog dialog = new JDialog(this, "Editar Zona", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Editar Zona");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JTextField txtNombre = new JTextField(nombre);
        JComboBox<String> cboEstado = new JComboBox<>(new String[]{"normal", "afectada", "evacuando", "evacuada"});
        cboEstado.setSelectedItem(estado);
        JSpinner spnPrioridad = new JSpinner(new SpinnerNumberModel(prioridad, 0, 100, 5));

        addFormField(panel, "Nombre de la zona:", txtNombre);
        addFormField(panel, "Estado:", cboEstado);
        addFormField(panel, "Prioridad (0-100):", spnPrioridad);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoEstado = (String) cboEstado.getSelectedItem();
            int nuevaPrioridad = (Integer) spnPrioridad.getValue();

            if (nuevoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ingrese el nombre de la zona", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            gestor.actualizarZona(id, nuevoNombre, nuevoEstado, nuevaPrioridad);
            cargarTablaZonas();
            sincronizarGrafoConRutas();
            JOptionPane.showMessageDialog(dialog, "Zona actualizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }


// ============================================
// VISTA: RUTAS
// ============================================

    private void mostrarRutas() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Panel de Rutas y Evacuación");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_PRIMARY);

        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        tabsPanel.setOpaque(false);

        JButton btnRutas = new JButton("Rutas de Transporte");
        JButton btnPlanes = new JButton("Planes de Evacuación");

        styleSecondaryButton(btnRutas);
        styleSecondaryButton(btnPlanes);

        btnRutas.setBackground(COLOR_PRIMARY);
        btnRutas.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        contentPanel.add(createRutasTransportePanel(), "rutas");
        contentPanel.add(createPlanesEvacuacionPanel(), "planes");

        btnRutas.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "rutas");
            btnRutas.setBackground(COLOR_PRIMARY);
            btnRutas.setForeground(Color.WHITE);
            btnPlanes.setBackground(COLOR_CARD);
            btnPlanes.setForeground(COLOR_TEXT_PRIMARY);
        });

        btnPlanes.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "planes");
            btnPlanes.setBackground(COLOR_PRIMARY);
            btnPlanes.setForeground(Color.WHITE);
            btnRutas.setBackground(COLOR_CARD);
            btnRutas.setForeground(COLOR_TEXT_PRIMARY);
        });

        tabsPanel.add(btnRutas);
        tabsPanel.add(btnPlanes);

        header.add(title, BorderLayout.WEST);
        header.add(tabsPanel, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        centerContainer.add(panel);
    }

    private JPanel createRutasTransportePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JPanel tableCard = createCard("Gestión de Rutas de Transporte");
        tableCard.setLayout(new BorderLayout(15, 15));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnAgregar = new JButton("+ Nueva Ruta");
        stylePrimaryButton(btnAgregar);

        if (!usuarioActual.soloLectura()) {
            btnAgregar.addActionListener(e -> mostrarDialogoAgregarRuta());
            toolbar.add(btnAgregar);
        }

        // Modelo con columna de acciones
        modeloRutas = new DefaultTableModel(
                new Object[]{"ID", "Origen", "Destino", "Distancia (km)", "Estado", "Acciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Solo la columna de acciones es editable
            }
        };

        JTable tabla = new JTable(modeloRutas);
        tabla.setRowHeight(50);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Configurar columna de acciones con botones
        tabla.getColumn("Acciones").setCellRenderer((TableCellRenderer) new ButtonRenderer());
        tabla.getColumn("Acciones").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // Origen
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Destino
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120); // Distancia
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100); // Estado
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100); // Acciones

        cargarTablaRutas();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));

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
                    r.getId() // Pasamos el ID para la acción de eliminar
            });
        }
    }

    // Renderer para mostrar el botón en la tabla
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText("Eliminar");
            setBackground(new Color(239, 68, 68));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            return this;
        }
    }

    // Editor para hacer funcional el botón en la tabla
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String rutaId;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            rutaId = value.toString();
            button.setText("Eliminar");
            button.setBackground(new Color(239, 68, 68));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                if (!usuarioActual.soloLectura()) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "¿Está seguro que desea eliminar esta ruta?",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        eliminarRuta(rutaId);
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

    private void eliminarRuta(String rutaId) {
        try {
            gestor.eliminarRuta(Long.valueOf(rutaId));
            cargarTablaRutas();
            sincronizarGrafoConRutas();
            JOptionPane.showMessageDialog(this,
                    "Ruta eliminada exitosamente\nEl mapa se ha actualizado",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar la ruta: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoAgregarRuta() {
        JDialog dialog = new JDialog(this, "Nueva Ruta", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(COLOR_CARD);

        JLabel titulo = new JLabel("Crear Nueva Ruta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        JComboBox<String> cboOrigen = new JComboBox<>();
        JComboBox<String> cboDestino = new JComboBox<>();

        for (Zona z : gestor.obtenerZonas()) {
            cboOrigen.addItem(z.getNombre());
            cboDestino.addItem(z.getNombre());
        }

        JTextField txtPeso = new JTextField();

        addFormField(panel, "Origen:", cboOrigen);
        addFormField(panel, "Destino:", cboDestino);
        addFormField(panel, "Distancia (km):", txtPeso);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Crear Ruta");
        JButton btnCancelar = new JButton("Cancelar");

        stylePrimaryButton(btnGuardar);
        styleSecondaryButton(btnCancelar);

        btnGuardar.addActionListener(e -> {
            try {
                String origen = (String) cboOrigen.getSelectedItem();
                String destino = (String) cboDestino.getSelectedItem();
                double peso = Double.parseDouble(txtPeso.getText().trim());

                if (origen == null || destino == null) {
                    JOptionPane.showMessageDialog(dialog, "Seleccione origen y destino", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (origen.equals(destino)) {
                    JOptionPane.showMessageDialog(dialog, "Origen y destino deben ser diferentes", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                gestor.agregarRuta(origen, destino, peso);
                cargarTablaRutas();
                sincronizarGrafoConRutas();
                JOptionPane.showMessageDialog(dialog, "Ruta creada exitosamente\nEl mapa se ha actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La distancia debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(btnGuardar);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    // ============================================
// PANEL DE EVACUACIONES
// ============================================

    private JPanel createPlanesEvacuacionPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);

        // Header con título y BOTONES
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitulo = new JLabel("Planes de Evacuación por Zona");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);

        // Panel para MÚLTIPLES botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnSimular = new JButton("Programar Evacuaciones");
        JButton btnIniciar = new JButton("Iniciar Evacuaciones");

        stylePrimaryButton(btnSimular);
        styleSecondaryButton(btnIniciar);

        if (!usuarioActual.puedeCoordinarOperaciones()) {
            btnSimular.setEnabled(false);
            btnIniciar.setEnabled(false);
            btnSimular.setToolTipText("No tiene permisos para coordinar operaciones");
            btnIniciar.setToolTipText("No tiene permisos para coordinar operaciones");
        }

        btnSimular.addActionListener(e -> {
            simularEvacuaciones();
            centerContainer.removeAll();
            mostrarRutas();
            centerContainer.revalidate();
            centerContainer.repaint();
        });

        btnIniciar.addActionListener(e -> {
            iniciarEvacuacionesPendientes();
            centerContainer.removeAll();
            mostrarRutas();
            centerContainer.revalidate();
            centerContainer.repaint();
        });

        buttonsPanel.add(btnSimular);
        buttonsPanel.add(btnIniciar);

        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        // Panel de contenido scrolleable
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        actualizarPanelEvacuaciones(contentPanel);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);

        return mainPanel;
    }

    private void actualizarPanelEvacuaciones(JPanel contentPanel) {
        contentPanel.removeAll();

        Map<String, Object> stats = gestorEvacuaciones.obtenerEstadisticas();
        int totalEvacuaciones = (Integer) stats.get("pendientes") +
                (Integer) stats.get("enProceso") +
                (Integer) stats.get("completadas");

        if (totalEvacuaciones == 0) {
            // Mensaje cuando no hay evacuaciones
            contentPanel.add(crearPanelVacio());
        } else {
            // Evacuaciones en proceso
            List<GestorEvacuaciones.Evacuacion> enProceso = gestorEvacuaciones.getEvacuacionesEnProceso();
            if (!enProceso.isEmpty()) {
                JLabel lblSeccion = new JLabel("En Proceso");
                lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblSeccion.setForeground(COLOR_TEXT_PRIMARY);
                lblSeccion.setBorder(new EmptyBorder(10, 5, 10, 5));
                contentPanel.add(lblSeccion);

                for (GestorEvacuaciones.Evacuacion ev : enProceso) {
                    contentPanel.add(createEvacuacionCard(ev));
                    contentPanel.add(Box.createVerticalStrut(15));
                }
            }

            // Evacuaciones pendientes
            List<GestorEvacuaciones.Evacuacion> pendientes = gestorEvacuaciones.getEvacuacionesPendientes();
            if (!pendientes.isEmpty()) {
                JLabel lblSeccion = new JLabel("Pendientes");
                lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblSeccion.setForeground(COLOR_TEXT_PRIMARY);
                lblSeccion.setBorder(new EmptyBorder(10, 5, 10, 5));
                contentPanel.add(lblSeccion);

                for (GestorEvacuaciones.Evacuacion ev : pendientes) {
                    contentPanel.add(createEvacuacionCard(ev));
                    contentPanel.add(Box.createVerticalStrut(15));
                }
            }

            // Evacuaciones completadas
            List<GestorEvacuaciones.Evacuacion> completadas = gestorEvacuaciones.getEvacuacionesCompletadas();
            if (!completadas.isEmpty()) {
                JLabel lblSeccion = new JLabel("Completadas");
                lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblSeccion.setForeground(COLOR_SUCCESS);
                lblSeccion.setBorder(new EmptyBorder(10, 5, 10, 5));
                contentPanel.add(lblSeccion);

                for (GestorEvacuaciones.Evacuacion ev : completadas) {
                    contentPanel.add(createEvacuacionCard(ev));
                    contentPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel crearPanelVacio() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));

        JLabel lblIcono = new JLabel("⚠");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblIcono.setForeground(COLOR_TEXT_SECONDARY);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMensaje = new JLabel("No hay evacuaciones programadas");
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMensaje.setForeground(COLOR_TEXT_PRIMARY);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescripcion = new JLabel("Presione 'Programar Evacuaciones' para comenzar");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDescripcion.setForeground(COLOR_TEXT_SECONDARY);
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyPanel.add(lblIcono);
        emptyPanel.add(Box.createVerticalStrut(15));
        emptyPanel.add(lblMensaje);
        emptyPanel.add(Box.createVerticalStrut(10));
        emptyPanel.add(lblDescripcion);

        return emptyPanel;
    }

    private void simularEvacuaciones() {
        int zonasEvacuadas = 0;
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("╔═══════════════════════════════════════╗\n");
        mensaje.append("║   SIMULACIÓN DE EVACUACIONES          ║\n");
        mensaje.append("╚═══════════════════════════════════════╝\n\n");

        for (Zona z : gestor.obtenerZonas()) {
            if (z.getPrioridad() >= 60) {
                int personas = (int) (Math.random() * 3000) + 500;
                gestorEvacuaciones.programarEvacuacion(z, personas);
                zonasEvacuadas++;

                mensaje.append(String.format("   %s\n", z.getNombre()));
                mensaje.append(String.format("   Personas: %d\n", personas));
                mensaje.append(String.format("   Prioridad: %d\n", z.getPrioridad()));
                mensaje.append(String.format("   Estado: %s\n\n", z.getEstado()));
            }
        }

        if (zonasEvacuadas > 0) {
            mensaje.append("═══════════════════════════════════════════\n");
            mensaje.append(String.format("Total: %d evacuaciones programadas\n", zonasEvacuadas));
            mensaje.append("═══════════════════════════════════════════\n");

            JOptionPane.showMessageDialog(this,
                    mensaje.toString(),
                    "Evacuaciones Programadas",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No hay zonas con prioridad ≥60 para evacuar\n\n" +
                            "Sugerencias:\n" +
                            "• Vaya a Administración → Zonas\n" +
                            "• Edite una zona y aumente su prioridad a 60 o más\n" +
                            "• Vuelva a intentar simular evacuaciones",
                    "Sin Evacuaciones Necesarias",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void iniciarEvacuacionesPendientes() {
        List<GestorEvacuaciones.Evacuacion> pendientes = gestorEvacuaciones.getEvacuacionesPendientes();

        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay evacuaciones pendientes para iniciar\n\n" +
                            "Primero programa evacuaciones con el botón:\n" +
                            "Programar Evacuaciones",
                    "Sin Evacuaciones Pendientes",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener equipos disponibles
        List<EquipoRescate> equiposDisponibles = new ArrayList<>();
        for (EquipoRescate e : gestor.obtenerEquipos()) {
            if (e.isDisponible()) {
                equiposDisponibles.add(e);
            }
        }

        if (equiposDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay equipos disponibles para asignar\n\n" +
                            "Todos los equipos están ocupados.\n" +
                            "Espere a que terminen las evacuaciones actuales.",
                    "Sin Equipos Disponibles",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Iniciar evacuaciones con equipos disponibles
        int iniciadas = 0;
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("╔═══════════════════════════════════════╗\n");
        mensaje.append("║   EVACUACIONES INICIADAS              ║\n");
        mensaje.append("╚═══════════════════════════════════════╝\n\n");

        for (EquipoRescate equipo : equiposDisponibles) {
            if (!gestorEvacuaciones.getEvacuacionesPendientes().isEmpty()) {
                GestorEvacuaciones.Evacuacion evacuacion =
                        gestorEvacuaciones.iniciarSiguienteEvacuacion(equipo);

                if (evacuacion != null) {
                    iniciadas++;
                    mensaje.append(String.format("   %s\n", evacuacion.getZonaNombre()));
                    mensaje.append(String.format("   Equipo: %s\n", equipo.getNombre()));
                    mensaje.append(String.format("   Personas: %d\n", evacuacion.getPersonasAEvacuar()));
                    mensaje.append(String.format("   Estado: EN PROCESO\n\n"));
                }
            }
        }

        if (iniciadas > 0) {
            mensaje.append("═══════════════════════════════════════════\n");
            mensaje.append(String.format("Total: %d evacuaciones iniciadas\n", iniciadas));
            mensaje.append("═══════════════════════════════════════════\n\n");
            mensaje.append("💡 Ve a Estadísticas para ver el progreso");

            JOptionPane.showMessageDialog(this,
                    mensaje.toString(),
                    "Evacuaciones Iniciadas",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron iniciar evacuaciones",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createEvacuacionCard(GestorEvacuaciones.Evacuacion ev) {
        Color color = ev.getEstado().equals("completada") ? COLOR_SUCCESS :
                ev.getEstado().equals("en_proceso") ? COLOR_INFO : COLOR_WARNING;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(color, 3, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Header con nombre de zona y badge de estado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblNombre = new JLabel(ev.getZonaNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombre.setForeground(COLOR_TEXT_PRIMARY);

        JLabel badge = createBadge(formatearEstado(ev.getEstado()), color);

        headerPanel.add(lblNombre, BorderLayout.WEST);
        headerPanel.add(badge, BorderLayout.EAST);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(12));

        // Información de la evacuación
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        infoPanel.setOpaque(false);

        JLabel lblPersonas = new JLabel(String.format("👥 Personas: %d / %d",
                ev.getPersonasEvacuadas(), ev.getPersonasAEvacuar()));
        lblPersonas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPersonas.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblPrioridad = new JLabel(String.format("⚠ Prioridad: %d", ev.getZonaPrioridad()));
        lblPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrioridad.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblProgreso = new JLabel(String.format(" %.1f%% completado", ev.getProgreso()));
        lblProgreso.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblProgreso.setForeground(COLOR_TEXT_SECONDARY);

        infoPanel.add(lblPersonas);
        infoPanel.add(lblPrioridad);
        infoPanel.add(lblProgreso);

        card.add(infoPanel);
        card.add(Box.createVerticalStrut(12));

        // Barra de progreso
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) ev.getProgreso());
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.0f%%", ev.getProgreso()));
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(229, 231, 235));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setBorder(new LineBorder(color, 1));

        card.add(progressBar);
        return card;
    }


    private String formatearEstado(String estado) {
        switch (estado) {
            case "completada": return "Completada";
            case "en_proceso": return "En Proceso";
            case "pendiente": return "Pendiente";
            default: return estado;
        }
    }
    // ============================================
    // VISTA: DISTRIBUCIÓN (ÁRBOL)
    // ============================================

    private void mostrarDistribucion() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel title = new JLabel("Árbol de Distribución de Recursos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_PRIMARY);

        // Panel principal dividido en 2 columnas
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(COLOR_BACKGROUND);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== COLUMNA IZQUIERDA: Configuración =====
        JPanel leftPanel = createCard("Configurar Distribución");
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Selector de recurso
        JLabel lblRecurso = new JLabel("Seleccionar Recurso:");
        lblRecurso.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRecurso.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cboRecursos = new JComboBox<>();
        for (Recurso r : gestor.obtenerRecursos()) {
            cboRecursos.addItem(r.getNombre() + " (" + r.getDisponible() + " disponibles)");
        }
        cboRecursos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cboRecursos.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblRecurso);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(cboRecursos);
        leftPanel.add(Box.createVerticalStrut(20));

        // Cantidad a distribuir
        JLabel lblCantidad = new JLabel("Cantidad a Distribuir:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        spnCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        spnCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblCantidad);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(spnCantidad);
        leftPanel.add(Box.createVerticalStrut(20));

        // Botones de distribución
        JButton btnEquitativo = new JButton("Distribuir Equitativamente");
        stylePrimaryButton(btnEquitativo);
        btnEquitativo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnEquitativo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnPrioridad = new JButton("Distribuir por Prioridad");
        styleSecondaryButton(btnPrioridad);
        btnPrioridad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPrioridad.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(btnEquitativo);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnPrioridad);
        leftPanel.add(Box.createVerticalStrut(20));

        // Área de resultado
        JLabel lblResultado = new JLabel("Resultado de la Distribución:");
        lblResultado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblResultado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtResultado = new JTextArea(10, 30);
        txtResultado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtResultado.setText("Ve a Configuración para activar Windows.");

        JScrollPane scrollResultado = new JScrollPane(txtResultado);
        scrollResultado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        scrollResultado.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblResultado);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(scrollResultado);

        // ===== COLUMNA DERECHA: Estructura del Árbol =====
        JPanel rightPanel = createCard("Estructura del Árbol");
        rightPanel.setLayout(new BorderLayout());

        JTextArea txtArbol = new JTextArea();
        txtArbol.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtArbol.setEditable(false);
        txtArbol.setBackground(new Color(248, 250, 252));
        txtArbol.setBorder(new EmptyBorder(15, 15, 15, 15));
        txtArbol.setText("La distribución se realizará\nentre las zonas del sistema");

        JScrollPane scrollArbol = new JScrollPane(txtArbol);
        scrollArbol.setBorder(new LineBorder(COLOR_BORDER, 1));

        rightPanel.add(scrollArbol, BorderLayout.CENTER);

        // ===== LÓGICA DE LOS BOTONES =====
        btnEquitativo.addActionListener(e -> {
            int cantidad = (Integer) spnCantidad.getValue();

            ArbolDistribucion<String> arbol = new ArbolDistribucion<>("Centro Principal", "centro");

            for (Zona z : gestor.obtenerZonas()) {
                arbol.agregarNodo("centro", z.getNombre(), z.getNombre().toLowerCase().replace(" ", "_"));
            }

            arbol.distribuirRecursos(cantidad);

            txtArbol.setText(arbol.visualizar());
            txtResultado.setText(arbol.generarReporte());
        });

        btnPrioridad.addActionListener(e -> {
            int cantidad = (Integer) spnCantidad.getValue();

            ArbolDistribucion<String> arbol = new ArbolDistribucion<>("Centro Principal", "centro");

            for (Zona z : gestor.obtenerZonas()) {
                arbol.agregarNodo("centro", z.getNombre(), z.getNombre().toLowerCase().replace(" ", "_"));
            }

            Map<String, Integer> prioridades = new HashMap<>();
            for (Zona z : gestor.obtenerZonas()) {
                prioridades.put(z.getNombre().toLowerCase().replace(" ", "_"), z.getPrioridad());
            }

            arbol.distribuirConPrioridades(cantidad, prioridades);

            txtArbol.setText(arbol.visualizar());
            txtResultado.setText(arbol.generarReporte());
        });

        // Agregar columnas al contenido principal
        mainContent.add(leftPanel);
        mainContent.add(rightPanel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);

        centerContainer.add(panel);
    }
    // ============================================
    // VISTA: ESTADÍSTICAS
    // ============================================

    private void mostrarEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel title = new JLabel("Estadísticas del Sistema");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_PRIMARY);

        JButton btnActualizar = new JButton("Actualizar");
        styleSecondaryButton(btnActualizar);
        btnActualizar.addActionListener(e -> {
            centerContainer.removeAll();
            mostrarEstadisticas();
            centerContainer.revalidate();
            centerContainer.repaint();
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnActualizar, BorderLayout.EAST);

        panel.add(header);
        panel.add(Box.createVerticalStrut(30));

        // CALCULAR ESTADÍSTICAS REALES
        int totalRecursosDisponibles = 0;
        int totalRecursosUsados = 0;
        for (Recurso r : gestor.obtenerRecursos()) {
            totalRecursosDisponibles += r.getDisponible();
            totalRecursosUsados += r.getUsado();
        }
        int totalRecursos = totalRecursosDisponibles + totalRecursosUsados;

        int equiposDisponibles = 0;
        int equiposAsignados = 0;
        for (EquipoRescate e : gestor.obtenerEquipos()) {
            if (e.isDisponible()) {
                equiposDisponibles++;
            } else {
                equiposAsignados++;
            }
        }
        int totalEquipos = equiposDisponibles + equiposAsignados;

        int zonasNormales = 0;
        int zonasAfectadas = 0;
        int zonasEvacuando = 0;
        int zonasEvacuadas = 0;
        for (Zona z : gestor.obtenerZonas()) {
            switch (z.getEstado().toLowerCase()) {
                case "normal": zonasNormales++; break;
                case "afectada": zonasAfectadas++; break;
                case "evacuando": zonasEvacuando++; break;
                case "evacuada": zonasEvacuadas++; break;
            }
        }
        int totalZonas = zonasNormales + zonasAfectadas + zonasEvacuando + zonasEvacuadas;

        Map<String, Object> statsEvacuaciones = gestorEvacuaciones.obtenerEstadisticas();
        int personasEvacuadas = (Integer) statsEvacuaciones.get("personasEvacuadas");
        int personasTotales = (Integer) statsEvacuaciones.get("personasTotales");
        double progresoEvacuacion = (Double) statsEvacuaciones.get("progresoGeneral");

        // KPIs principales
        JPanel kpisGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        kpisGrid.setOpaque(false);
        kpisGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        double eficienciaRecursos = totalRecursos > 0 ?
                (totalRecursosUsados * 100.0 / totalRecursos) : 0;

        double tasaAsignacion = totalEquipos > 0 ?
                (equiposAsignados * 100.0 / totalEquipos) : 0;

        kpisGrid.add(createKPICard("Recursos Usados",
                String.format("%.1f%%", eficienciaRecursos),
                String.format("%d de %d unidades", totalRecursosUsados, totalRecursos),
                COLOR_INFO, false));

        kpisGrid.add(createKPICard("Personas Evacuadas",
                String.format("%d", personasEvacuadas),
                String.format("%.1f%% del total", progresoEvacuacion),
                COLOR_SUCCESS, false));

        kpisGrid.add(createKPICard("Equipos Activos",
                String.format("%d", equiposAsignados),
                String.format("%.0f%% desplegados", tasaAsignacion),
                COLOR_PRIMARY, false));

        kpisGrid.add(createKPICard("Zonas Afectadas",
                String.format("%d", zonasAfectadas + zonasEvacuando),
                String.format("%d de %d zonas", zonasAfectadas + zonasEvacuando, totalZonas),
                COLOR_DANGER, false));

        panel.add(kpisGrid);
        panel.add(Box.createVerticalStrut(30));

        // Tabla de recursos
        JPanel tableCard = createCard("Estado de Recursos");
        tableCard.setLayout(new BorderLayout(15, 15));
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        DefaultTableModel modelRecursos = new DefaultTableModel(
                new Object[]{"Recurso", "Disponible", "Usado", "Total", "% Usado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Recurso r : gestor.obtenerRecursos()) {
            double porcentajeUsado = r.getTotal() > 0 ?
                    (r.getUsado() * 100.0 / r.getTotal()) : 0;

            modelRecursos.addRow(new Object[]{
                    r.getNombre(),
                    r.getDisponible(),
                    r.getUsado(),
                    r.getTotal(),
                    String.format("%.1f%%", porcentajeUsado)
            });
        }

        JTable tablaRecursos = new JTable(modelRecursos);
        tablaRecursos.setRowHeight(45);
        tablaRecursos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaRecursos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaRecursos.setSelectionBackground(new Color(239, 246, 255));

        JScrollPane scrollRecursos = new JScrollPane(tablaRecursos);
        scrollRecursos.setBorder(new LineBorder(COLOR_BORDER, 1));

        tableCard.add(scrollRecursos);

        panel.add(tableCard);
        panel.add(Box.createVerticalStrut(30));

        // Tabla de zonas
        JPanel zonasCard = createCard("Estado de Zonas");
        zonasCard.setLayout(new BorderLayout(15, 15));
        zonasCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        DefaultTableModel modelZonasStats = new DefaultTableModel(
                new Object[]{"Zona", "Estado", "Prioridad", "Equipos", "Evacuaciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Zona z : gestor.obtenerZonas()) {
            // Contar evacuaciones para esta zona
            int evacuacionesZona = 0;
            for (GestorEvacuaciones.Evacuacion ev : gestorEvacuaciones.getTodasEvacuaciones()) {
                if (ev.getZonaId().equals(z.getId())) {
                    evacuacionesZona++;
                }
            }

            modelZonasStats.addRow(new Object[]{
                    z.getNombre(),
                    z.getEstado(),
                    z.getPrioridad(),
                    z.getEquiposAsignados().size(),
                    evacuacionesZona
            });
        }

        JTable tablaZonas = new JTable(modelZonasStats);
        tablaZonas.setRowHeight(45);
        tablaZonas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaZonas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaZonas.setSelectionBackground(new Color(239, 246, 255));

        JScrollPane scrollZonas = new JScrollPane(tablaZonas);
        scrollZonas.setBorder(new LineBorder(COLOR_BORDER, 1));

        zonasCard.add(scrollZonas);

        panel.add(zonasCard);
        panel.add(Box.createVerticalStrut(20));

        JScrollPane mainScroll = new JScrollPane(panel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        centerContainer.add(mainScroll);
    }
    private JPanel createKPICard(String title, String value, String subtitle, Color color, boolean isPercentage) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(isPercentage && subtitle.startsWith("+") ? COLOR_SUCCESS : COLOR_TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSubtitle);

        return card;
    }

    private JPanel createOperacionesRecientesPanel() {
        JPanel panel = createCard("Operaciones Recientes");
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        String[][] operaciones = {
                {"Evacuación", "Completada", "Zona Norte Sector A3", "145 personas • 2h 15m", String.valueOf(COLOR_SUCCESS.getRGB())},
                {"Distribución", "En Progreso", "Refugio Central", "250 raciones • 45m", String.valueOf(COLOR_INFO.getRGB())},
                {"Rescate", "Completada", "Zona Centro B2", "8 personas • 1h 30m", String.valueOf(COLOR_SUCCESS.getRGB())},
                {"Transporte", "Completada", "Zona Sur → Base", "50 kits médicos • 35m", String.valueOf(COLOR_SUCCESS.getRGB())}
        };

        for (String[] op : operaciones) {
            panel.add(createOperacionItem(op[0], op[1], op[2], op[3], new Color(Integer.parseInt(op[4]))));
            panel.add(Box.createVerticalStrut(12));
        }

        return panel;
    }

    private JPanel createOperacionItem(String tipo, String estado, String ubicacion, String detalles, Color color) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        item.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTipo = new JLabel(tipo);
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTipo.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblUbicacion = new JLabel(ubicacion);
        lblUbicacion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUbicacion.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblDetalles = new JLabel(detalles);
        lblDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetalles.setForeground(COLOR_TEXT_SECONDARY);

        content.add(lblTipo);
        content.add(lblUbicacion);
        content.add(lblDetalles);

        JLabel badge = createBadge(estado, color);

        item.add(content, BorderLayout.CENTER);
        item.add(badge, BorderLayout.EAST);

        return item;
    }

    // ============================================
    // VISTA: MAPA
    // ============================================

    private void mostrarMapa() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel title = new JLabel("Mapa Interactivo de Operaciones");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controls.setOpaque(false);

        JLabel lblOrigen = new JLabel("Origen:");
        JComboBox<String> cboOrigen = new JComboBox<>();
        JLabel lblDestino = new JLabel("Destino:");
        JComboBox<String> cboDestino = new JComboBox<>();

        for (Nodo n : grafo.getNodos()) {
            cboOrigen.addItem(n.getId());
            cboDestino.addItem(n.getId());
        }

        JButton btnCalcular = new JButton("Calcular Ruta Óptima");
        stylePrimaryButton(btnCalcular);

        JButton btnLimpiar = new JButton("Limpiar");
        styleSecondaryButton(btnLimpiar);

        btnCalcular.addActionListener(e -> {
            String origen = (String) cboOrigen.getSelectedItem();
            String destino = (String) cboDestino.getSelectedItem();

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(this, "Seleccione origen y destino", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Nodo> ruta = grafo.dijkstra(origen, destino);

            if (mapPanel != null) {
                mapPanel.setHighlightedPath(ruta);
                mapPanel.repaint();
            }

            if (ruta != null && !ruta.isEmpty()) {
                StringBuilder rutaStr = new StringBuilder("Ruta calculada:\n");
                for (int i = 0; i < ruta.size(); i++) {
                    rutaStr.append(ruta.get(i).getId());
                    if (i < ruta.size() - 1) rutaStr.append(" → ");
                }
                JOptionPane.showMessageDialog(this, rutaStr.toString(), "Ruta Óptima", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ruta entre estos puntos", "Sin Ruta", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            if (mapPanel != null) {
                mapPanel.limpiarRutaResaltada();
            }
        });

        controls.add(lblOrigen);
        controls.add(cboOrigen);
        controls.add(lblDestino);
        controls.add(cboDestino);
        controls.add(btnCalcular);
        controls.add(btnLimpiar);

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBackground(COLOR_CARD);
        mapContainer.setBorder(new LineBorder(COLOR_BORDER, 1, true));

        if (mapPanel == null) {
            mapPanel = new MapPanel(grafo);
        }

        mapContainer.add(mapPanel);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(mapContainer, BorderLayout.CENTER);

        centerContainer.add(panel);
    }

    // ============================================
    // MÉTODOS AUXILIARs
    // ============================================

    private void addFormField(JPanel panel, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(new CompoundBorder(
                    new LineBorder(new Color(209, 213, 219), 1, true),
                    new EmptyBorder(10, 12, 10, 12)
            ));
        }

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setBackground(COLOR_CARD);
        btn.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private String getNivelPrioridad(int prioridad) {
        if (prioridad >= 80) return "Crítico";
        if (prioridad >= 60) return "Alto";
        if (prioridad >= 40) return "Medio";
        return "Bajo";
    }

    private Color getColorPrioridad(int prioridad) {
        if (prioridad >= 80) return COLOR_DANGER;
        if (prioridad >= 60) return COLOR_WARNING;
        if (prioridad >= 40) return new Color(234, 179, 8);
        return COLOR_SUCCESS;
    }
}

