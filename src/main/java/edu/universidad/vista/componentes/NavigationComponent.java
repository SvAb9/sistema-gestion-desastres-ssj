package edu.universidad.vista.componentes;

import edu.universidad.vista.EstilosUI;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// INTERACTIVIDAD DE LA BARRA PRINCIPAL DE LA VENTANA
public class NavigationComponent {

    private Consumer<String> navegarCallback;
    private List<JButton> botones;
    private String currentView;

    public NavigationComponent(Consumer<String> navegarCallback) {
        this.navegarCallback = navegarCallback;
        this.botones = new ArrayList<>();
        this.currentView = "inicio";
    }

    public JPanel crear() {
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
            botones.add(btn);
            nav.add(btn);
        }

        return nav;
    }

    private JButton createNavButton(String text, String view) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
        btn.setBackground(EstilosUI.COLOR_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 45));

        if (view.equals(currentView)) {
            btn.setForeground(EstilosUI.COLOR_PRIMARY);
            btn.setBackground(new Color(239, 246, 255));
        }

        btn.addActionListener(e -> {
            currentView = view;
            navegarCallback.accept(view);
            actualizarEstado(view);
        });

        return btn;
    }

    public void actualizarEstado(String view) {
        currentView = view;
        for (JButton btn : botones) {
            String btnText = btn.getText().toLowerCase();
            boolean isCurrentView = btnText.contains(view);

            if (isCurrentView) {
                btn.setForeground(EstilosUI.COLOR_PRIMARY);
                btn.setBackground(new Color(239, 246, 255));
            } else {
                btn.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);
                btn.setBackground(EstilosUI.COLOR_CARD);
            }
        }
    }
}