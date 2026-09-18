package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Centralized UI design tokens, color palettes, typography, and component styling.
 * Gives the Java Swing application a clean, modern aesthetic.
 */
public final class UITheme {

    // Palette: Slate & Deep Indigo
    public static final Color PRIMARY = new Color(30, 58, 138);       // #1E3A8A Navy Blue
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color SECONDARY = new Color(71, 85, 105);     // #475569 Slate 600
    public static final Color ACCENT = new Color(59, 130, 246);       // #3B82F6 Blue

    // Status Colors
    public static final Color SUCCESS = new Color(16, 185, 129);      // #10B981 Emerald
    public static final Color WARNING = new Color(245, 158, 11);      // #F59E0B Amber
    public static final Color DANGER = new Color(239, 68, 68);        // #EF4444 Rose
    public static final Color INFO = new Color(14, 165, 233);         // #0EA5E9 Sky

    // Surfaces & Backgrounds
    public static final Color BG_MAIN = new Color(248, 250, 252);     // #F8FAFC
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);     // #0F172A Dark Slate
    public static final Color BORDER = new Color(226, 232, 240);      // #E2E8F0

    // Typography Colors
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);   // Slate 900
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);// Slate 600
    public static final Color TEXT_MUTED = new Color(148, 163, 184);  // Slate 400
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;

    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("SansSerif", Font.BOLD, 11);

    private UITheme() {}

    /**
     * Creates a styled primary button with smooth background and hover effects.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(TEXT_ON_PRIMARY);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY.darker(), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    /**
     * Creates a secondary outline or neutral button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(241, 245, 249));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    /**
     * Creates an accent action button (e.g. Danger or Success button).
     */
    public static JButton createActionButton(String text, Color bgColor, Color textColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(textColor);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    /**
     * Standard styled card panel with rounded border and subtle inset padding.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    /**
     * Creates a styled text field with clean padding and border.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(PRIMARY);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(PRIMARY);
        pf.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    /**
     * Creates a stat KPI metric card with icon/value/label.
     */
    public static JPanel createMetricCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(accentColor, 2, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valLabel.setForeground(accentColor);

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLabel.setForeground(TEXT_SECONDARY);

        card.add(valLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Custom Table Cell Renderer for colored status and priority badges.
     */
    public static class BadgeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(FONT_BADGE);

            if (value == null) {
                return label;
            }

            String text = value.toString();
            Color bgColor = new Color(241, 245, 249);
            Color fgColor = TEXT_PRIMARY;

            switch (text.toLowerCase()) {
                case "submitted" -> {
                    bgColor = new Color(226, 232, 240);
                    fgColor = new Color(51, 65, 85);
                }
                case "in progress" -> {
                    bgColor = new Color(254, 243, 199);
                    fgColor = new Color(180, 83, 9);
                }
                case "resolved" -> {
                    bgColor = new Color(209, 250, 229);
                    fgColor = new Color(4, 120, 87);
                }
                case "rejected" -> {
                    bgColor = new Color(254, 226, 226);
                    fgColor = new Color(185, 28, 28);
                }
                case "urgent" -> {
                    bgColor = new Color(254, 226, 226);
                    fgColor = new Color(185, 28, 28);
                }
                case "high" -> {
                    bgColor = new Color(254, 243, 199);
                    fgColor = new Color(180, 83, 9);
                }
                case "medium" -> {
                    bgColor = new Color(224, 231, 255);
                    fgColor = new Color(67, 56, 202);
                }
                case "low" -> {
                    bgColor = new Color(209, 250, 229);
                    fgColor = new Color(4, 120, 87);
                }
            }

            if (!isSelected) {
                label.setBackground(bgColor);
                label.setForeground(fgColor);
                label.setOpaque(true);
            }
            label.setBorder(new EmptyBorder(3, 8, 3, 8));
            return label;
        }
    }
}
