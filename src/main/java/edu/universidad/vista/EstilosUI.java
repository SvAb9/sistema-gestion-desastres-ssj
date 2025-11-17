package edu.universidad.vista;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Clase de utilidades para estilos y colores del sistema
 */
public class EstilosUI {

    // COLORES DEL DISEÑO

    public static final Color COLOR_PRIMARY = new Color(67, 97, 238);
    public static final Color COLOR_BACKGROUND = new Color(249, 250, 251);
    public static final Color COLOR_CARD = Color.WHITE;
    public static final Color COLOR_TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color COLOR_TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color COLOR_BORDER = new Color(229, 231, 235);
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);
    public static final Color COLOR_WARNING = new Color(245, 158, 11);
    public static final Color COLOR_DANGER = new Color(239, 68, 68);
    public static final Color COLOR_INFO = new Color(59, 130, 246);

    // FUENTES

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    // MÉTODOS DE ESTILO PARA BOTONES

    public static void stylePrimaryButton(JButton btn) {
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    public static void styleSecondaryButton(JButton btn) {
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

    public static void styleDangerButton(JButton btn) {
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_DANGER);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    public static void styleSuccessButton(JButton btn) {
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_SUCCESS);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    // MÉTODOS PARA CREAR COMPONENTES ESTILIZADOS

    public static JPanel createCard(String title) {
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

    public static JLabel createBadge(String text, Color color) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(color);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));
        return badge;
    }

    public static void addFormField(JPanel panel, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_NORMAL);
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

    // UTILIDADES PARA COLORES DE PRIORIDAD

    public static String getNivelPrioridad(int prioridad) {
        if (prioridad >= 80) return "Crítico";
        if (prioridad >= 60) return "Alto";
        if (prioridad >= 40) return "Medio";
        return "Bajo";
    }

    public static Color getColorPrioridad(int prioridad) {
        if (prioridad >= 80) return COLOR_DANGER;
        if (prioridad >= 60) return COLOR_WARNING;
        if (prioridad >= 40) return new Color(234, 179, 8);
        return COLOR_SUCCESS;
    }
}