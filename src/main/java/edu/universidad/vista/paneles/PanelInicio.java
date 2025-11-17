package edu.universidad.vista.paneles;

import edu.universidad.modelo.*;
import edu.universidad.servicio.GestorEvacuaciones;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel de Inicio - Dashboard con estadísticas principales
 * Contador de evacuaciones muestra total sin duplicar
 */
public class PanelInicio {

    private GestorRecursosLocal gestor;
    private GestorEvacuaciones gestorEvacuaciones;

    public PanelInicio(GestorRecursosLocal gestor, GestorEvacuaciones gestorEvacuaciones) {
        this.gestor = gestor;
        this.gestorEvacuaciones = gestorEvacuaciones;
    }

    public JPanel crear() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);

        // Título principal
        JLabel title = new JLabel("Panel de Inicio");
        title.setFont(EstilosUI.FONT_TITLE);
        title.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Contenido principal
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(EstilosUI.COLOR_BACKGROUND);

        // Sección de bienvenida
        mainContent.add(crearSeccionBienvenida());
        mainContent.add(Box.createVerticalStrut(30));

        // KPIs principales
        mainContent.add(crearKPIsPanel());
        mainContent.add(Box.createVerticalStrut(30));

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSeccionBienvenida() {
        JPanel card = EstilosUI.createCard(null);
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema de Gestión de Desastres");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBienvenida.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JLabel lblDescripcion = new JLabel(
                "<html>Sistema integral para la coordinación de recursos, equipos de rescate, " +
                        "evacuaciones y gestión de rutas en situaciones de emergencia.<br><br>" +
                        "<b> Utilice el menú de navegación superior para acceder a las diferentes secciones del sistema.</b></html>"
        );
        lblDescripcion.setFont(EstilosUI.FONT_NORMAL);
        lblDescripcion.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(lblBienvenida);
        textos.add(Box.createVerticalStrut(10));
        textos.add(lblDescripcion);

        card.add(textos, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearKPIsPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel lblTitulo = new JLabel("Estadísticas Generales");
        lblTitulo.setFont(EstilosUI.FONT_SUBTITLE);
        lblTitulo.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel kpisGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        kpisGrid.setOpaque(false);

        // Calcular estadísticas
        int totalZonas = gestor.obtenerZonas().size();
        int totalEquipos = gestor.obtenerEquipos().size();
        int totalRecursos = gestor.obtenerRecursos().size();

        int zonasAfectadas = 0;
        for (Zona z : gestor.obtenerZonas()) {
            if (z.getEstado().toLowerCase().contains("afecta") ||
                    z.getEstado().toLowerCase().contains("evacua")) {
                zonasAfectadas++;
            }
        }

        int equiposActivos = 0;
        for (EquipoRescate e : gestor.obtenerEquipos()) {
            if (!e.isDisponible()) {
                equiposActivos++;
            }
        }

        // CORRECCIÓN: Contar solo el tamaño de la lista completa (sin sumar)
        int evacuacionesTotales = gestorEvacuaciones.getTodasEvacuaciones().size();

        kpisGrid.add(crearKPICard("Zonas Totales",
                String.valueOf(totalZonas),
                zonasAfectadas + " afectadas",
                EstilosUI.COLOR_PRIMARY));

        kpisGrid.add(crearKPICard("Equipos de Rescate",
                String.valueOf(totalEquipos),
                equiposActivos + " activos",
                EstilosUI.COLOR_SUCCESS));

        kpisGrid.add(crearKPICard("Recursos Disponibles",
                String.valueOf(totalRecursos),
                "tipos registrados",
                EstilosUI.COLOR_INFO));

        // Card de evacuaciones con contador CORREGIDO
        kpisGrid.add(crearKPICard("Evacuaciones Totales",
                String.valueOf(evacuacionesTotales),
                "operaciones registradas",
                EstilosUI.COLOR_WARNING));

        container.add(lblTitulo, BorderLayout.NORTH);
        container.add(kpisGrid, BorderLayout.CENTER);

        return container;
    }

    private JPanel crearKPICard(String titulo, String valor, String subtitulo, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(EstilosUI.COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(EstilosUI.COLOR_BORDER, 1, true),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(EstilosUI.FONT_SMALL);
        lblSubtitulo.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValor);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSubtitulo);

        return card;
    }
}