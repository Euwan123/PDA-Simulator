import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StackPanel extends JPanel {
    private List<Character> items = new ArrayList<>();
    private int animOffset = 0;

    public StackPanel() {
        setBackground(new Color(15, 20, 40));
        setPreferredSize(new Dimension(160, 0));
    }

    public void setStack(Stack<Character> s) {
        items = new ArrayList<>();
        for (Character c : s) items.add(c);
        animOffset = 0;
        repaint();
    }

    public void animatePush(Stack<Character> s) {
        items = new ArrayList<>();
        for (Character c : s) items.add(c);
        animOffset = -50;
        Timer t = new Timer(12, null);
        t.addActionListener(e -> {
            animOffset += 4;
            if (animOffset >= 0) { animOffset = 0; t.stop(); }
            repaint();
        });
        t.start();
    }

    public void animatePop(Stack<Character> s) {
        Timer t = new Timer(12, null);
        t.addActionListener(e -> {
            animOffset -= 4;
            if (animOffset <= -50) {
                items = new ArrayList<>();
                for (Character c : s) items.add(c);
                animOffset = 0;
                t.stop();
            }
            repaint();
        });
        t.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int cellW = 100, cellH = 52;
        int x = w / 2 - cellW / 2;

        g2.setColor(new Color(20, 28, 52));
        g2.fillRoundRect(8, 8, w - 16, h - 16, 14, 14);
        g2.setColor(new Color(50, 70, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(8, 8, w - 16, h - 16, 14, 14);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        FontMetrics hfm = g2.getFontMetrics();
        g2.drawString("STACK", w / 2 - hfm.stringWidth("STACK") / 2, 34);

        int bottomY = h - 60;

        g2.setColor(new Color(60, 80, 130));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1f, new float[]{5, 5}, 0));
        g2.drawLine(x - 4, bottomY, x + cellW + 4, bottomY);
        g2.setStroke(new BasicStroke(1.5f));

        g2.setColor(new Color(140, 160, 200));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics bfm = g2.getFontMetrics();
        g2.drawString("BOTTOM", w / 2 - bfm.stringWidth("BOTTOM") / 2, h - 40);

        for (int i = 0; i < items.size(); i++) {
            char sym = items.get(i);
            int cellY = bottomY - (i + 1) * cellH + animOffset;
            boolean isTop = (i == items.size() - 1);

            Color base = sym == 'Z' ? new Color(100, 50, 160) : new Color(30, 100, 200);
            GradientPaint gp = new GradientPaint(x, cellY, base.brighter(), x, cellY + cellH, base);
            g2.setPaint(gp);
            g2.fillRoundRect(x, cellY + 3, cellW, cellH - 6, 12, 12);

            g2.setColor(isTop ? new Color(120, 200, 255) : new Color(60, 100, 180));
            g2.setStroke(new BasicStroke(isTop ? 2.5f : 1.5f));
            g2.drawRoundRect(x, cellY + 3, cellW, cellH - 6, 12, 12);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(String.valueOf(sym),
                    x + cellW / 2 - fm.stringWidth(String.valueOf(sym)) / 2,
                    cellY + cellH / 2 + fm.getAscent() / 2 - 5);

            if (isTop) {
                g2.setColor(new Color(120, 200, 255));
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.drawString("◄ TOP", x + cellW + 6, cellY + cellH / 2 + 4);
            }
        }

        if (items.isEmpty()) {
            g2.setColor(new Color(80, 100, 150));
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("empty", w / 2 - fm.stringWidth("empty") / 2, h / 2);
        }

        g2.setColor(new Color(60, 80, 130));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x - 4, 45, x - 4, bottomY);
        g2.drawLine(x + cellW + 4, 45, x + cellW + 4, bottomY);
    }
}