package edu.universidad.vista.paneles;

import edu.universidad.estructura.ArbolDistribucion;
import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel de Distribución de Recursos con Árbol
 * ✅ CORREGIDO: Referencias entre paneles funcionando correctamente
 */
public class PanelDistribucion {

    private GestorRecursosLocal gestor;
    private JTextArea txtArbolVisualizacion; // ✅ Referencia compartida

    public PanelDistribucion(GestorRecursosLocal gestor) {
        this.gestor = gestor;
    }

    public JPanel crear() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);

        JLabel title = new JLabel("Árbol de Distribución de Recursos");
        title.setFont(EstilosUI.FONT_TITLE);
        title.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(EstilosUI.COLOR_BACKGROUND);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Crear visualización primero para tener la referencia
        JPanel rightPanel = createVisualizacionPanel();

        // Ahora crear configuración con la referencia disponible
        JPanel leftPanel = createConfiguracionPanel();

        mainContent.add(leftPanel);
        mainContent.add(rightPanel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createConfiguracionPanel() {
        JPanel leftPanel = EstilosUI.createCard("Configurar Distribución");
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Selector de recurso
        JLabel lblRecurso = new JLabel("Seleccionar Recurso:");
        lblRecurso.setFont(EstilosUI.FONT_NORMAL);
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
        lblCantidad.setFont(EstilosUI.FONT_NORMAL);
        lblCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        spnCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        spnCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblCantidad);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(spnCantidad);
        leftPanel.add(Box.createVerticalStrut(20));

        // Área de resultado
        JLabel lblResultado = new JLabel("Resultado de la Distribución:");
        lblResultado.setFont(EstilosUI.FONT_NORMAL);
        lblResultado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtResultado = new JTextArea(10, 30);
        txtResultado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtResultado.setText("Seleccione un método de distribución para ver los resultados");

        JScrollPane scrollResultado = new JScrollPane(txtResultado);
        scrollResultado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scrollResultado.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblResultado);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(scrollResultado);
        leftPanel.add(Box.createVerticalStrut(20));

        // Botones de distribución
        JButton btnEquitativo = new JButton("Distribuir Equitativamente");
        EstilosUI.stylePrimaryButton(btnEquitativo);
        btnEquitativo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnEquitativo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnPrioridad = new JButton("Distribuir por Prioridad");
        EstilosUI.styleSecondaryButton(btnPrioridad);
        btnPrioridad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPrioridad.setAlignmentX(Component.LEFT_ALIGNMENT);

        // EVENTOS CORREGIDOS - Usan la referencia compartida
        btnEquitativo.addActionListener(e -> {
            int cantidad = (Integer) spnCantidad.getValue();

            ArbolDistribucion<String> arbol = new ArbolDistribucion<>("Centro Principal", "centro");

            for (Zona z : gestor.obtenerZonas()) {
                arbol.agregarNodo("centro", z.getNombre(), z.getNombre().toLowerCase().replace(" ", "_"));
            }

            arbol.distribuirRecursos(cantidad);

            // Actualizar usando la referencia compartida
            if (txtArbolVisualizacion != null) {
                txtArbolVisualizacion.setText(arbol.visualizar());
            }
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

            // Actualizar usando la referencia compartida
            if (txtArbolVisualizacion != null) {
                txtArbolVisualizacion.setText(arbol.visualizar());
            }
            txtResultado.setText(arbol.generarReporte());
        });

        leftPanel.add(btnEquitativo);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnPrioridad);

        return leftPanel;
    }

    private JPanel createVisualizacionPanel() {
        JPanel rightPanel = EstilosUI.createCard("Estructura del Árbol");
        rightPanel.setLayout(new BorderLayout());

        // Guardar referencia en la variable de instancia
        txtArbolVisualizacion = new JTextArea();
        txtArbolVisualizacion.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtArbolVisualizacion.setEditable(false);
        txtArbolVisualizacion.setBackground(new Color(248, 250, 252));
        txtArbolVisualizacion.setBorder(new EmptyBorder(15, 15, 15, 15));
        txtArbolVisualizacion.setText("La distribución se realizará\nentre las zonas del sistema\n\n" +
                "Presione un botón de distribución\npara ver la estructura del árbol");

        JScrollPane scrollArbol = new JScrollPane(txtArbolVisualizacion);
        scrollArbol.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1));

        rightPanel.add(scrollArbol, BorderLayout.CENTER);

        return rightPanel;
    }
}