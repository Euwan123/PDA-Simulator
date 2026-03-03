import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TitleBar extends JPanel {

    public TitleBar(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 24, 42));
        setPreferredSize(new Dimension(0, 52));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 52, 80)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("⬡");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 22));
        icon.setForeground(new Color(99, 179, 237));

        JLabel title = new JLabel("PDA Visual Simulator");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(new Color(220, 230, 255));

        JLabel sub = new JLabel("  L = { aⁿbⁿ | n ≥ 1 }");
        sub.setFont(new Font("Courier New", Font.PLAIN, 13));
        sub.setForeground(new Color(99, 179, 137));

        left.add(icon);
        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        right.setOpaque(false);

        JButton minimize = makeWindowBtn(new Color(255, 189, 68), "–");
        JButton close = makeWindowBtn(new Color(255, 95, 87), "✕");

        minimize.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
        close.addActionListener(e -> System.exit(0));

        right.add(minimize);
        right.add(close);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);

        Point[] dragStart = {null};
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point s = e.getLocationOnScreen();
                frame.setLocation(s.x - dragStart[0].x, s.y - dragStart[0].y);
            }
        });
    }

    private JButton makeWindowBtn(Color color, String symbol) {
        JButton btn = new JButton(symbol) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0, 0, 0, 100));
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                if (getModel().isRollover()) g2.drawString(getText(), tx, ty);
            }
        };
        btn.setPreferredSize(new Dimension(16, 16));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}