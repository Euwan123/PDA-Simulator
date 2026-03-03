import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DiagramPanel extends JPanel {

    private String activeState = "q0";
    private String activeTransition = "";

    private static final Color BG = new Color(13, 17, 30);
    private static final Color STATE_DEFAULT = new Color(25, 35, 60);
    private static final Color STATE_ACTIVE = new Color(30, 90, 160);
    private static final Color STATE_ACCEPT = new Color(20, 110, 70);
    private static final Color TEXT_MAIN = new Color(220, 235, 255);
    private static final Color TEXT_DIM = new Color(140, 170, 210);
    private static final Color ARROW = new Color(80, 110, 170);
    private static final Color ARROW_ACTIVE = new Color(120, 190, 255);
    private static final Color LABEL_BOX = new Color(22, 32, 56);

    public DiagramPanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(400, 0));
    }

    public void setActiveState(String state) {
        activeState = state;
        repaint();
    }

    public void setActiveTransition(String transition) {
        activeTransition = transition;
        repaint();
    }

    public void setRejected() {
        activeState = "REJECT";
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(18, 26, 48));
        g2.fillRoundRect(10, 10, w - 20, h - 20, 16, 16);
        g2.setColor(new Color(45, 62, 100));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 16, 16);

        g2.setColor(TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        String header = "STATE TRANSITION DIAGRAM";
        FontMetrics hfm = g2.getFontMetrics();
        g2.drawString(header, w / 2 - hfm.stringWidth(header) / 2, 34);

        g2.setColor(new Color(45, 62, 100));
        g2.drawLine(20, 42, w - 20, 42);

        int cx = w / 2;
        int q0y = 140;
        int q1y = 320;
        int q2y = 490;

        drawSelfLoop(g2, cx, q0y, new String[]{"a, Z → AZ", "a, A → AA"},
                activeState.equals("q0"));

        drawArrow(g2, cx, q0y + 36, cx, q1y - 36,
                "b, A → ε",
                activeState.equals("q1") || (activeTransition.contains("q0") && activeTransition.contains("q1")));

        drawSelfLoop(g2, cx, q1y, new String[]{"b, A → ε"},
                activeState.equals("q1"));

        drawArrow(g2, cx, q1y + 36, cx, q2y - 36,
                "ε, Z → Z",
                activeState.equals("q2"));

        boolean rejected = activeState.equals("REJECT");
        drawState(g2, cx, q0y, "q0", "Start", activeState.equals("q0"), false, rejected);
        drawState(g2, cx, q1y, "q1", "Match", activeState.equals("q1"), false, false);
        drawState(g2, cx, q2y, "q2", "Accept", activeState.equals("q2"), true, false);

        drawInitArrow(g2, cx, q0y);

        if (!activeTransition.isEmpty()) {
            String[] lines = activeTransition.split("\n");
            int boxH = 14 + lines.length * 18 + 8;
            int boxY = h - boxH - 14;
            g2.setColor(new Color(22, 34, 62));
            g2.fillRoundRect(16, boxY, w - 32, boxH, 10, 10);
            g2.setColor(new Color(60, 90, 150));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(16, boxY, w - 32, boxH, 10, 10);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            for (int i = 0; i < lines.length; i++) {
                g2.setColor(i == 0 ? ARROW_ACTIVE : TEXT_DIM);
                g2.drawString(lines[i], 26, boxY + 16 + i * 18);
            }
        }
    }

    private void drawState(Graphics2D g2, int cx, int cy, String name, String role,
                           boolean active, boolean accepting, boolean rejected) {
        int r = 34;

        Color fill;
        if (accepting && active) fill = STATE_ACCEPT;
        else if (rejected) fill = new Color(120, 25, 25);
        else if (active) fill = STATE_ACTIVE;
        else fill = STATE_DEFAULT;

        GradientPaint gp = new GradientPaint(cx - r, cy - r, fill.brighter(), cx + r, cy + r, fill.darker());
        g2.setPaint(gp);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        Color border;
        if (accepting && active) border = new Color(60, 220, 140);
        else if (rejected) border = new Color(255, 80, 80);
        else if (active) border = ARROW_ACTIVE;
        else border = new Color(55, 75, 120);

        g2.setColor(border);
        g2.setStroke(new BasicStroke(active ? 2.5f : 1.5f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);

        if (accepting) {
            int ir = r - 5;
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(active ? new Color(60, 220, 140) : new Color(40, 130, 85));
            g2.drawOval(cx - ir, cy - ir, ir * 2, ir * 2);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(name, cx - fm.stringWidth(name) / 2, cy + fm.getAscent() / 2 - 2);

        g2.setColor(active ? TEXT_MAIN : TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fm = g2.getFontMetrics();
        g2.drawString(role, cx - fm.stringWidth(role) / 2, cy + r + 17);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2, String label, boolean active) {
        Color color = active ? ARROW_ACTIVE : ARROW;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(active ? 2.2f : 1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        drawArrowHead(g2, x2, y2, angle, color);

        int mx = (x1 + x2) / 2;
        int my = (y1 + y2) / 2;
        drawLabel(g2, label, mx + 14, my, active);
    }

    private void drawArrowHead(Graphics2D g2, int x, int y, double angle, Color color) {
        int len = 11;
        double spread = 0.42;
        int ax = x - (int) (len * Math.cos(angle - spread));
        int ay = y - (int) (len * Math.sin(angle - spread));
        int bx = x - (int) (len * Math.cos(angle + spread));
        int by = y - (int) (len * Math.sin(angle + spread));
        g2.setColor(color);
        g2.fillPolygon(new int[]{x, ax, bx}, new int[]{y, ay, by}, 3);
    }

    private void drawSelfLoop(Graphics2D g2, int cx, int cy, String[] lines, boolean active) {
        Color color = active ? ARROW_ACTIVE : ARROW;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(active ? 2.2f : 1.6f));

        int loopR = 26;
        int loopCx = cx + 36 + loopR;
        int loopCy = cy - 26;
        g2.drawOval(loopCx - loopR, loopCy - loopR, loopR * 2, loopR * 2);

        double arrowAngle = Math.PI * 0.85;
        int arrowX = loopCx + (int) (loopR * Math.cos(arrowAngle));
        int arrowY = loopCy + (int) (loopR * Math.sin(arrowAngle));
        drawArrowHead(g2, arrowX, arrowY, Math.PI * 0.3, color);

        int labelX = loopCx + loopR + 8;
        int labelY = loopCy - 6;
        for (int i = 0; i < lines.length; i++) {
            drawLabel(g2, lines[i], labelX, labelY + i * 18, active);
        }
    }

    private void drawLabel(Graphics2D g2, String text, int x, int y, boolean active) {
        Font font = new Font("SansSerif", Font.BOLD, 12);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getHeight();
        int pad = 4;

        g2.setColor(LABEL_BOX);
        g2.fillRoundRect(x - pad, y - th + 3, tw + pad * 2, th + 2, 6, 6);

        g2.setColor(active ? ARROW_ACTIVE : TEXT_DIM);
        g2.drawString(text, x, y);
    }

    private void drawInitArrow(Graphics2D g2, int cx, int q0y) {
        int x1 = cx - 70, y1 = q0y;
        int x2 = cx - 36, y2 = q0y;
        g2.setColor(new Color(80, 110, 170));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
        drawArrowHead(g2, x2, y2, 0, new Color(80, 110, 170));
    }
}