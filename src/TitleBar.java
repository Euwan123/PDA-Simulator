import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TitleBar extends JPanel {
    public TitleBar(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 15, 32));
        setPreferredSize(new Dimension(0, 58));
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(50, 70, 120)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("⬡");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 26));
        icon.setForeground(new Color(100, 180, 255));

        JLabel title = new JLabel("PDA Visual Simulator");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("   L = { aⁿbⁿ | n ≥ 1 }");
        sub.setFont(new Font("SansSerif", Font.BOLD, 16));
        sub.setForeground(new Color(80, 200, 140));

        left.add(icon);
        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        right.setOpaque(false);

        JButton minimize = makeBtn(new Color(255, 189, 68), "–");
        JButton close = makeBtn(new Color(255, 95, 87), "✕");
        minimize.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
        close.addActionListener(e -> System.exit(0));
        right.add(minimize);
        right.add(close);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);

        Point[] drag = {null};
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { drag[0] = e.getPoint(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point s = e.getLocationOnScreen();
                frame.setLocation(s.x - drag[0].x, s.y - drag[0].y);
            }
        });
    }

    private JButton makeBtn(Color color, String sym) {
        JButton b = new JButton(sym) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                if (getModel().isRollover()) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        b.setPreferredSize(new Dimension(18, 18));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}