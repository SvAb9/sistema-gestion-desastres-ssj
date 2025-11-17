// ============================================
// ARCHIVO 1: HeaderComponent.java
// ============================================
package edu.universidad.vista.componentes;

import edu.universidad.modelo.Usuario;
import edu.universidad.vista.EstilosUI;
import edu.universidad.vista.VentanaPrincipal;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

// ENCABEZADO DE LA VENTANA PRINCIPAL
public class HeaderComponent {

    private Usuario usuarioActual;
    private VentanaPrincipal ventanaPrincipal;

    public HeaderComponent(Usuario usuarioActual, VentanaPrincipal ventanaPrincipal) {
        this.usuarioActual = usuarioActual;
        this.ventanaPrincipal = ventanaPrincipal;
    }

    public JPanel crear(NavigationComponent navigationComponent) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(EstilosUI.COLOR_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, EstilosUI.COLOR_BORDER),
                new EmptyBorder(20, 40, 20, 40)
        ));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JPanel logoPanel = crearLogoPanel();
        JPanel userPanel = crearUserPanel();

        topSection.add(logoPanel, BorderLayout.WEST);
        topSection.add(userPanel, BorderLayout.EAST);

        JPanel navPanel = navigationComponent.crear();

        JPanel headerContent = new JPanel(new BorderLayout(0, 15));
        headerContent.setOpaque(false);
        headerContent.add(topSection, BorderLayout.NORTH);
        headerContent.add(navPanel, BorderLayout.CENTER);

        header.add(headerContent);
        return header;
    }

    private JPanel crearLogoPanel() {
        JLabel logo = new JLabel("DesaRecu");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(EstilosUI.COLOR_PRIMARY);

        JLabel subtitle = new JLabel("Coordinación y gestión de recursos, evacuaciones y equipos de rescate");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(subtitle);

        return logoPanel;
    }

    private JPanel crearUserPanel() {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel lblUsuario = new JLabel(usuarioActual.getNombre() + " (" + usuarioActual.getRol().getNombre() + ")");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsuario.setForeground(EstilosUI.COLOR_TEXT_SECONDARY);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        EstilosUI.styleSecondaryButton(btnCerrarSesion);
        btnCerrarSesion.addActionListener(e -> ventanaPrincipal.cerrarSesion());

        userPanel.add(lblUsuario);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(btnCerrarSesion);

        return userPanel;
    }
}
