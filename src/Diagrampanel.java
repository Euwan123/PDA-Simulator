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
    private static final Color STATE_REJECT = new Color(140, 30, 30);
    private static final Color TEXT_MAIN = new Color(200, 220, 255);
    private static final Color TEXT_DIM = new Color(100, 130, 180);
    private static final Color ARROW = new Color(70, 100, 160);
    private static final Color ARROW_ACTIVE = new Color(99, 179, 237);

    public DiagramPanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(380, 0));
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
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(20, 28, 50));
        g2.fillRoundRect(10, 10, w - 20, h - 20, 16, 16);
        g2.setColor(new Color(40, 55, 90));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 16, 16);

        g2.setColor(TEXT_DIM);
        g2.setFont(new Font("Courier New", Font.BOLD, 11));
        g2.drawString("STATE TRANSITION DIAGRAM", w / 2 - 95, 35);

        int cx = w / 2;
        int q0x = cx, q0y = 130;
        int q1x = cx, q1y = 310;
        int q2x = cx, q2y = 490;

        drawSelfLoop(g2, q0x, q0y, "a,Z→AZ | a,A→AA", activeState.equals("q0") && activeTransition.contains("q0"));
        drawArrow(g2, q0x, q0y + 30, q1x, q1y - 30, "b,A→ε", activeTransition.contains("q0") && activeTransition.contains("q1"));
        drawArrow(g2, q1x, q1y + 30, q2x, q2y - 30, "ε,Z→Z", activeTransition.contains("q1") && activeTransition.contains("q2"));
        drawSelfLoop(g2, q1x, q1y, "b,A→ε", activeState.equals("q1") && activeTransition.contains("q1,"));

        boolean rejected = activeState.equals("REJECT");
        drawState(g2, q0x, q0y, "q0", "Start", activeState.equals("q0"), false, rejected && false);
        drawState(g2, q1x, q1y, "q1", "Match", activeState.equals("q1"), false, false);
        drawState(g2, q2x, q2y, "q2", "Accept", activeState.equals("q2"), true, false);

        g2.setColor(new Color(70, 90, 140));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(q0x - 35, q0y, q0x - 15, q0y);
        int[] ax = {q0x - 35, q0x - 28, q0x - 28};
        int[] ay = {q0y, q0y - 6, q0y + 6};
        g2.setColor(new Color(70, 90, 140));
        g2.fillPolygon(ax, ay, 3);

        if (!activeTransition.isEmpty()) {
            g2.setColor(new Color(30, 50, 80));
            g2.fillRoundRect(14, h - 58, w - 28, 44, 10, 10);
            g2.setColor(ARROW_ACTIVE);
            g2.setFont(new Font("Courier New", Font.BOLD, 11));
            String[] lines = activeTransition.split("\n");
            for (int i = 0; i < lines.length; i++) {
                g2.drawString(lines[i], 24, h - 40 + i * 16);
            }
        }
    }

    private void drawState(Graphics2D g2, int cx, int cy, String name, String role, boolean active, boolean accepting, boolean rejected) {
        int r = 28;

        if (accepting && active) {
            g2.setColor(STATE_ACCEPT);
        } else if (rejected) {
            g2.setColor(STATE_REJECT);
        } else if (active) {
            g2.setColor(STATE_ACTIVE);
        } else {
            g2.setColor(STATE_DEFAULT);
        }
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        if (active) {
            g2.setColor(accepting ? new Color(50, 200, 130) : rejected ? new Color(255, 80, 80) : ARROW_ACTIVE);
            g2.setStroke(new BasicStroke(2.5f));
        } else {
            g2.setColor(new Color(50, 70, 110));
            g2.setStroke(new BasicStroke(1.5f));
        }
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);

        if (accepting) {
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(active ? new Color(50, 200, 130) : new Color(40, 120, 80));
            g2.drawOval(cx - r + 4, cy - r + 4, (r - 4) * 2, (r - 4) * 2);
        }

        g2.setColor(active ? Color.WHITE : TEXT_MAIN);
        g2.setFont(new Font("Courier New", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(name, cx - fm.stringWidth(name) / 2, cy + fm.getAscent() / 2 - 2);

        g2.setColor(TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fm = g2.getFontMetrics();
        g2.drawString(role, cx - fm.stringWidth(role) / 2, cy + r + 14);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2, String label, boolean active) {
        g2.setColor(active ? ARROW_ACTIVE : ARROW);
        g2.setStroke(new BasicStroke(active ? 2f : 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int ax = x2 - (int) (10 * Math.cos(angle - 0.4));
        int ay = y2 - (int) (10 * Math.sin(angle - 0.4));
        int bx = x2 - (int) (10 * Math.cos(angle + 0.4));
        int by = y2 - (int) (10 * Math.sin(angle + 0.4));
        g2.fillPolygon(new int[]{x2, ax, bx}, new int[]{y2, ay, by}, 3);

        g2.setColor(active ? ARROW_ACTIVE : TEXT_DIM);
        g2.setFont(new Font("Courier New", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        int mx = (x1 + x2) / 2 + 12;
        int my = (y1 + y2) / 2;
        g2.drawString(label, mx - fm.stringWidth(label) / 2, my);
    }

    private void drawSelfLoop(Graphics2D g2, int cx, int cy, String label, boolean active) {
        g2.setColor(active ? ARROW_ACTIVE : ARROW);
        g2.setStroke(new BasicStroke(active ? 2f : 1.5f));
        int loopX = cx + 28;
        int loopY = cy - 28;
        g2.drawOval(loopX - 18, loopY - 18, 36, 36);

        g2.setColor(active ? ARROW_ACTIVE : TEXT_DIM);
        g2.setFont(new Font("Courier New", Font.PLAIN, 9));
        FontMetrics fm = g2.getFontMetrics();
        String[] parts = label.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            g2.drawString(part, loopX + 20 - fm.stringWidth(part) / 2, loopY - 22 + i * 13);
        }
    }
}