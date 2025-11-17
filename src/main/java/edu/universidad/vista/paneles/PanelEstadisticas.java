package edu.universidad.vista.paneles;

import edu.universidad.modelo.*;
import edu.universidad.servicio.GestorEvacuaciones;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel de Estadísticas del Sistema - VERSIÓN SIMPLIFICADA
 *Con botón "Exportar Reporte"
 */
public class PanelEstadisticas {

    private GestorRecursosLocal gestor;
    private GestorEvacuaciones gestorEvacuaciones;

    public PanelEstadisticas(GestorRecursosLocal gestor, GestorEvacuaciones gestorEvacuaciones) {
        this.gestor = gestor;
        this.gestorEvacuaciones = gestorEvacuaciones;
    }

    public JScrollPane crear() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel title = new JLabel("Estadísticas del Sistema");
        title.setFont(EstilosUI.FONT_TITLE);
        title.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        // Botón Exportar Reporte
        JButton btnExportar = new JButton("Exportar Reporte");
        EstilosUI.stylePrimaryButton(btnExportar);
        btnExportar.addActionListener(e -> exportarReporte());

        header.add(title, BorderLayout.WEST);
        header.add(btnExportar, BorderLayout.EAST);

        panel.add(header);
        panel.add(Box.createVerticalStrut(30));

        // KPIs principales
        panel.add(createKPIsPanel());
        panel.add(Box.createVerticalStrut(30));

        // Tabla de recursos (ÚNICA TABLA)
        panel.add(createRecursosTable());
        panel.add(Box.createVerticalStrut(20));

        JScrollPane mainScroll = new JScrollPane(panel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        return mainScroll;
    }

    private JPanel createKPIsPanel() {
        JPanel kpisGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        kpisGrid.setOpaque(false);
        kpisGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Calcular estadísticas
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

        int zonasAfectadas = 0;
        int zonasEvacuando = 0;
        for (Zona z : gestor.obtenerZonas()) {
            String estado = z.getEstado().toLowerCase();
            if (estado.equals("afectada")) zonasAfectadas++;
            if (estado.equals("evacuando")) zonasEvacuando++;
        }
        int totalZonas = gestor.obtenerZonas().size();

        Map<String, Object> statsEvacuaciones = gestorEvacuaciones.obtenerEstadisticas();
        int totalEvacuaciones = (Integer) statsEvacuaciones.get("pendientes") +
                (Integer) statsEvacuaciones.get("enProceso") +
                (Integer) statsEvacuaciones.get("completadas");

        double eficienciaRecursos = totalRecursos > 0 ?
                (totalRecursosUsados * 100.0 / totalRecursos) : 0;

        double tasaAsignacion = totalEquipos > 0 ?
                (equiposAsignados * 100.0 / totalEquipos) : 0;

        kpisGrid.add(createKPICard("Recursos Usados",
                String.format("%.1f%%", eficienciaRecursos),
                String.format("%d de %d unidades", totalRecursosUsados, totalRecursos),
                EstilosUI.COLOR_INFO, false));

        kpisGrid.add(createKPICard("Total Evacuaciones",
                String.format("%d", totalEvacuaciones),
                "operaciones registradas",
                EstilosUI.COLOR_SUCCESS, false));

        kpisGrid.add(createKPICard("Equipos Activos",
                String.format("%d", equiposAsignados),
                String.format("%.0f%% desplegados", tasaAsignacion),
                EstilosUI.COLOR_PRIMARY, false));

        kpisGrid.add(createKPICard("Zonas Afectadas",
                String.format("%d", zonasAfectadas + zonasEvacuando),
                String.format("%d de %d zonas", zonasAfectadas + zonasEvacuando, totalZonas),
                EstilosUI.COLOR_DANGER, false));

        return kpisGrid;
    }

    private JPanel createKPICard(String title, String value, String subtitle, Color color, boolean isPercentage) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(EstilosUI.COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(EstilosUI.COLOR_BORDER, 1, true),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(EstilosUI.FONT_SMALL);
        lblSubtitle.setForeground(isPercentage && subtitle.startsWith("+") ?
                EstilosUI.COLOR_SUCCESS : EstilosUI.COLOR_TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSubtitle);

        return card;
    }

    private JPanel createRecursosTable() {
        JPanel tableCard = EstilosUI.createCard("Estado de Recursos");
        tableCard.setLayout(new BorderLayout(15, 15));
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        String[] columnas = {"Recurso", "Disponible", "Usado", "Total", "% Usado"};
        Object[][] datos = new Object[gestor.obtenerRecursos().size()][5];

        int i = 0;
        for (Recurso r : gestor.obtenerRecursos()) {
            double porcentajeUsado = r.getTotal() > 0 ?
                    (r.getUsado() * 100.0 / r.getTotal()) : 0;

            datos[i][0] = r.getNombre();
            datos[i][1] = r.getDisponible();
            datos[i][2] = r.getUsado();
            datos[i][3] = r.getTotal();
            datos[i][4] = String.format("%.1f%%", porcentajeUsado);
            i++;
        }

        JTable tablaRecursos = new JTable(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRecursos.setRowHeight(45);
        tablaRecursos.setFont(EstilosUI.FONT_NORMAL);
        tablaRecursos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaRecursos.setSelectionBackground(new Color(239, 246, 255));

        JScrollPane scrollRecursos = new JScrollPane(tablaRecursos);
        scrollRecursos.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1));

        tableCard.add(scrollRecursos);
        return tableCard;
    }

    // MÉTODO PARA EXPORTAR REPORTE
    private void exportarReporte() {
        // Simular exportación
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Generando reporte de estadísticas..."), BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        JDialog dialog = new JDialog((Frame) null, "Exportando Reporte", true);
        dialog.setContentPane(panel);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(null);

        // Simular progreso en un hilo separado
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 10) {
                    final int progreso = i;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progreso));
                    Thread.sleep(200);
                }

                SwingUtilities.invokeLater(() -> {
                    dialog.dispose();

                    // Generar contenido del reporte
                    StringBuilder reporte = new StringBuilder();
                    reporte.append("╔═══════════════════════════════════════╗\n");
                    reporte.append("║   REPORTE DE ESTADÍSTICAS             ║\n");
                    reporte.append("╚═══════════════════════════════════════╝\n\n");

                    reporte.append("RECURSOS:\n");
                    for (Recurso r : gestor.obtenerRecursos()) {
                        reporte.append(String.format("  • %s: %d/%d unidades (%.1f%% usado)\n",
                                r.getNombre(), r.getUsado(), r.getTotal(),
                                r.getTotal() > 0 ? (r.getUsado() * 100.0 / r.getTotal()) : 0));
                    }

                    reporte.append("\n EQUIPOS:\n");
                    for (EquipoRescate e : gestor.obtenerEquipos()) {
                        reporte.append(String.format("  • %s - %s (%s)\n",
                                e.getNombre(), e.getResponsable(),
                                e.isDisponible() ? "Disponible" : "Asignado"));
                    }

                    reporte.append("\n ZONAS:\n");
                    for (Zona z : gestor.obtenerZonas()) {
                        reporte.append(String.format("  • %s: %s (Prioridad: %d)\n",
                                z.getNombre(), z.getEstado(), z.getPrioridad()));
                    }

                    Map<String, Object> stats = gestorEvacuaciones.obtenerEstadisticas();
                    reporte.append("\n EVACUACIONES:\n");
                    reporte.append(String.format("  • Pendientes: %d\n", stats.get("pendientes")));
                    reporte.append(String.format("  • En Proceso: %d\n", stats.get("enProceso")));
                    reporte.append(String.format("  • Completadas: %d\n", stats.get("completadas")));

                    reporte.append("\n════════════════════════════════════════\n");

                    JOptionPane.showMessageDialog(null,
                            "Reporte exportado exitosamente\n\n" + reporte.toString(),
                            "Exportación Completa",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        dialog.setVisible(true);
    }
}