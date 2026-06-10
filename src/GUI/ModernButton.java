package GUI;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;


public class ModernButton extends JButton {

    public enum Style { PRIMARY, OUTLINE, GHOST }

    private final Style style;
    private boolean hovered = false;

    private static final int ARC = 8;

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;

        // Strip all default Swing chrome
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyFontAndForeground();

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    /** Shorthand – PRIMARY style */
    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    private void applyFontAndForeground() {
        switch (style) {
            case PRIMARY -> {
                setFont(UIConstants.FONT_BOLD_13);
                setForeground(Color.WHITE);
            }
            case OUTLINE -> {
                setFont(UIConstants.FONT_BOLD_13);
                setForeground(UIConstants.TEXT_DARK);
            }
            case GHOST -> {
                setFont(UIConstants.FONT_PLAIN_14);
                setForeground(UIConstants.TEXT_GRAY);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (!isEnabled()) {
            // Disabled state — flat light gray
            g2.setColor(new Color(0xE5E7EB));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));
        } else {
            switch (style) {
                case PRIMARY -> paintPrimary(g2, w, h);
                case OUTLINE -> paintOutline(g2, w, h);
                case GHOST   -> paintGhost(g2, w, h);
            }
        }

        g2.dispose();
        super.paintComponent(g); // draws the text label on top
    }

    // ── PRIMARY: red-to-dark-red vertical gradient ────────────────────────────
    private void paintPrimary(Graphics2D g2, int w, int h) {
        Color top    = hovered ? UIConstants.PRIMARY_DARK : UIConstants.PRIMARY;
        Color bottom = hovered ? new Color(0x991B1B)      : UIConstants.PRIMARY_DARK;

        GradientPaint gradient = new GradientPaint(0, 0, top, 0, h, bottom);
        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));

        // Subtle inner highlight
        g2.setColor(new Color(255, 255, 255, 20));
        g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h / 2f, ARC, ARC));
    }

    // ── OUTLINE: white bg, gray border; hover → very light gray bg ───────────
    private void paintOutline(Graphics2D g2, int w, int h) {
        g2.setColor(hovered ? new Color(0xF9FAFB) : Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));

        g2.setColor(hovered ? UIConstants.BORDER_INPUT : UIConstants.BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, w - 1.2f, h - 1.2f, ARC, ARC));
    }

    // ── GHOST: transparent; hover → light red tint bg, text turns red ─────────
    private void paintGhost(Graphics2D g2, int w, int h) {
        if (hovered) {
            g2.setColor(UIConstants.PRIMARY_TINT);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));
            setForeground(UIConstants.PRIMARY);
        } else {
            setForeground(UIConstants.TEXT_GRAY);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            applyFontAndForeground();
        } else {
            setForeground(UIConstants.TEXT_GRAY);
        }
        repaint();
    }
}
