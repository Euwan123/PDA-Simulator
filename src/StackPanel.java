import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StackPanel extends JPanel {

    private List<Character> stackItems = new ArrayList<>();
    private int animOffset = 0;
    private boolean animatingPush = false;
    private boolean animatingPop = false;

    private static final Color BG = new Color(13, 17, 30);
    private static final Color CELL_A = new Color(30, 80, 160);
    private static final Color CELL_Z = new Color(80, 50, 120);
    private static final Color CELL_BORDER = new Color(99, 179, 237);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color LABEL_COLOR = new Color(100, 130, 180);

    public StackPanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(140, 0));
    }

    public void setStack(Stack<Character> stack) {
        stackItems = new ArrayList<>();
        for (Character c : stack) stackItems.add(c);
        animOffset = 0;
        repaint();
    }

    public void animatePush(Stack<Character> newStack) {
        stackItems = new ArrayList<>();
        for (Character c : newStack) stackItems.add(c);
        animOffset = -40;
        animatingPush = true;

        Timer t = new Timer(12, null);
        t.addActionListener(e -> {
            animOffset += 3;
            if (animOffset >= 0) {
                animOffset = 0;
                animatingPush = false;
                t.stop();
            }
            repaint();
        });
        t.start();
    }

    public void animatePop(Stack<Character> newStack) {
        animatingPop = true;
        Timer t = new Timer(12, null);
        t.addActionListener(e -> {
            animOffset -= 3;
            if (animOffset <= -40) {
                stackItems = new ArrayList<>();
                for (Character c : newStack) stackItems.add(c);
                animOffset = 0;
                animatingPop = false;
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

        int w = getWidth();
        int h = getHeight();
        int cellH = 44;
        int cellW = 80;
        int startX = w / 2 - cellW / 2;

        g2.setColor(new Color(20, 28, 50));
        g2.fillRoundRect(8, 8, w - 16, h - 16, 14, 14);
        g2.setColor(new Color(40, 55, 90));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(8, 8, w - 16, h - 16, 14, 14);

        g2.setColor(LABEL_COLOR);
        g2.setFont(new Font("Courier New", Font.BOLD, 11));
        String topLabel = "STACK";
        g2.drawString(topLabel, w / 2 - g2.getFontMetrics().stringWidth(topLabel) / 2, 30);

        g2.setColor(new Color(40, 55, 90));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4, 4}, 0));
        g2.drawLine(startX - 2, h - 52, startX + cellW + 2, h - 52);
        g2.setStroke(new BasicStroke(1f));

        g2.setColor(LABEL_COLOR);
        g2.setFont(new Font("Courier New", Font.PLAIN, 9));
        g2.drawString("BOTTOM", w / 2 - 18, h - 38);

        for (int i = 0; i < stackItems.size(); i++) {
            char sym = stackItems.get(i);
            int baseY = h - 52 - (i + 1) * cellH;
            int y = baseY + animOffset;

            boolean isTop = (i == stackItems.size() - 1);
            Color cellColor = sym == 'Z' ? CELL_Z : CELL_A;

            GradientPaint gp = new GradientPaint(startX, y, cellColor.brighter(), startX, y + cellH, cellColor.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(startX, y + 2, cellW, cellH - 4, 10, 10);

            g2.setColor(isTop ? CELL_BORDER : new Color(60, 100, 160));
            g2.setStroke(new BasicStroke(isTop ? 2f : 1f));
            g2.drawRoundRect(startX, y + 2, cellW, cellH - 4, 10, 10);

            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Courier New", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(String.valueOf(sym), startX + cellW / 2 - fm.stringWidth(String.valueOf(sym)) / 2, y + cellH / 2 + fm.getAscent() / 2 - 4);

            if (isTop) {
                g2.setColor(CELL_BORDER);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.drawString("TOP", startX + cellW + 4, y + cellH / 2 + 3);
            }
        }

        if (stackItems.isEmpty()) {
            g2.setColor(new Color(60, 80, 120));
            g2.setFont(new Font("Courier New", Font.PLAIN, 11));
            String empty = "empty";
            g2.drawString(empty, w / 2 - g2.getFontMetrics().stringWidth(empty) / 2, h / 2);
        }

        g2.setColor(new Color(40, 55, 90));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(startX - 2, 40, startX - 2, h - 52);
        g2.drawLine(startX + cellW + 2, 40, startX + cellW + 2, h - 52);
    }
}