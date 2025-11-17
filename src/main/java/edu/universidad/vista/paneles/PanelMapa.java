package edu.universidad.vista.paneles;

import edu.universidad.modelo.*;
import edu.universidad.vista.EstilosUI;
import edu.universidad.vista.MapPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class PanelMapa {

    private Grafo grafo;
    private MapPanel mapPanel;

    public PanelMapa(Grafo grafo) {
        this.grafo = grafo;
        this.mapPanel = new MapPanel(grafo);
    }

    public JPanel crear() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(EstilosUI.COLOR_BACKGROUND);

        JLabel title = new JLabel(" Mapa Interactivo de Operaciones");
        title.setFont(EstilosUI.FONT_TITLE);
        title.setForeground(EstilosUI.COLOR_TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controls.setOpaque(false);

        JLabel lblOrigen = new JLabel("Origen:");
        lblOrigen.setFont(EstilosUI.FONT_NORMAL);

        JComboBox<String> cboOrigen = new JComboBox<>();
        cboOrigen.setFont(EstilosUI.FONT_NORMAL);

        JLabel lblDestino = new JLabel("Destino:");
        lblDestino.setFont(EstilosUI.FONT_NORMAL);

        JComboBox<String> cboDestino = new JComboBox<>();
        cboDestino.setFont(EstilosUI.FONT_NORMAL);

        for (Nodo n : grafo.getNodos()) {
            cboOrigen.addItem(n.getId());
            cboDestino.addItem(n.getId());
        }

        JButton btnCalcular = new JButton(" Calcular Ruta Óptima");
        EstilosUI.stylePrimaryButton(btnCalcular);

        JButton btnLimpiar = new JButton(" Limpiar");
        EstilosUI.styleSecondaryButton(btnLimpiar);

        btnCalcular.addActionListener(e -> {
            String origen = (String) cboOrigen.getSelectedItem();
            String destino = (String) cboDestino.getSelectedItem();

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(null, "Seleccione origen y destino", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origen.equals(destino)) {
                JOptionPane.showMessageDialog(null, "El origen y destino deben ser diferentes", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Nodo> ruta = grafo.dijkstra(origen, destino);

            if (mapPanel != null) {
                mapPanel.setHighlightedPath(ruta);
                mapPanel.repaint();
            }

            if (ruta != null && !ruta.isEmpty()) {
                StringBuilder rutaStr = new StringBuilder("Ruta óptima calculada:\n\n");
                double distanciaTotal = 0;

                for (int i = 0; i < ruta.size(); i++) {
                    rutaStr.append(ruta.get(i).getId());

                    if (i < ruta.size() - 1) {
                        Nodo actual = ruta.get(i);
                        Nodo siguiente = ruta.get(i + 1);

                        for (Arista arista : grafo.getAristas()) {
                            if ((arista.getOrigen().equals(actual.getId()) &&
                                    arista.getDestino().equals(siguiente.getId())) ||
                                    (arista.getOrigen().equals(siguiente.getId()) &&
                                            arista.getDestino().equals(actual.getId()))) {
                                distanciaTotal += arista.getPeso();
                                rutaStr.append(" → ");
                                break;
                            }
                        }
                    }
                }

                rutaStr.append("\n\nDistancia total: ").append(String.format("%.1f", distanciaTotal)).append(" km");
                rutaStr.append("\nNodos en la ruta: ").append(ruta.size());

                JOptionPane.showMessageDialog(null, rutaStr.toString(), "Ruta Óptima Encontrada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "No se encontró una ruta disponible entre estos puntos.\n\n" +
                                "Verifique que:\n" +
                                "• Las zonas estén conectadas por rutas\n" +
                                "• Exista un camino válido entre origen y destino",
                        "Sin Ruta Disponible",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            if (mapPanel != null) {
                mapPanel.limpiarRutaResaltada();
            }
        });

        controls.add(lblOrigen);
        controls.add(cboOrigen);
        controls.add(Box.createHorizontalStrut(10));
        controls.add(lblDestino);
        controls.add(cboDestino);
        controls.add(Box.createHorizontalStrut(10));
        controls.add(btnCalcular);
        controls.add(btnLimpiar);

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBackground(EstilosUI.COLOR_CARD);
        mapContainer.setBorder(new LineBorder(EstilosUI.COLOR_BORDER, 1, true));
        mapContainer.add(mapPanel);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(mapContainer, BorderLayout.CENTER);

        return panel;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
