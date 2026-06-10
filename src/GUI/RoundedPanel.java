package GUI;

import javax.swing.JPanel;
import java.awt.*;


public class RoundedPanel extends JPanel {

    private final int   radius;
    private final boolean hasShadow;
    private Color       background;


    public RoundedPanel(int radius, boolean hasShadow, Color bg) {
        this.radius    = radius;
        this.hasShadow = hasShadow;
        this.background = bg;
        setOpaque(false); // we do our own painting
    }

    public RoundedPanel() {
        this(14, true, Color.WHITE);
    }

    public RoundedPanel(int radius) {
        this(radius, true, Color.WHITE);
    }

    public RoundedPanel(boolean hasShadow) {
        this(14, hasShadow, Color.WHITE);
    }

    public RoundedPanel(Color bg) {
        this(14, true, bg);
    }

    @Override
    public void setBackground(Color bg) {
        this.background = bg;
        repaint();
    }

    @Override
    public Color getBackground() {
        return background != null ? background : Color.WHITE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (hasShadow) {
            // Layer 1 – outermost, faintest
            g2.setColor(new Color(0, 0, 0, 6));
            g2.fillRoundRect(3, 5, w - 6, h - 5, radius + 4, radius + 4);

            // Layer 2
            g2.setColor(UIConstants.SHADOW_OUTER);  // (0,0,0,8)
            g2.fillRoundRect(2, 4, w - 4, h - 4, radius + 2, radius + 2);

            // Layer 3 – closest to card
            g2.setColor(UIConstants.SHADOW_INNER);  // (0,0,0,4)
            g2.fillRoundRect(1, 2, w - 2, h - 2, radius, radius);
        }

        // Main fill
        g2.setColor(background != null ? background : Color.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

        g2.dispose();
        super.paintComponent(g); // paint children
    }

    @Override
    protected void paintChildren(Graphics g) {
        // clip children to rounded region so nothing bleeds past the corners
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        super.paintChildren(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {

    }


    public void drawOutline(Graphics g, Color color, float strokeWidth) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
        g2.dispose();
    }
}
