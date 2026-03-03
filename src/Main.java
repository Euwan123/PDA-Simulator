import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setSize(950, 580);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(new Color(0,0,0,0));
        frame.setShape(new RoundRectangle2D.Double(0, 0, 950, 580, 30, 30));
        frame.setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,247,250));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(230,235,240));
        titleBar.setPreferredSize(new Dimension(0,40));

        JLabel title = new JLabel("  PDA Visual Simulator");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton close = new JButton("X");
        close.setFocusPainted(false);
        close.setBorder(null);
        close.setBackground(new Color(255,90,90));
        close.setForeground(Color.WHITE);
        close.setPreferredSize(new Dimension(50,30));
        close.addActionListener(e -> System.exit(0));

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(close, BorderLayout.EAST);

        Point[] mouseDown = {null};

        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDown[0] = e.getPoint();
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point curr = e.getLocationOnScreen();
                frame.setLocation(curr.x - mouseDown[0].x,
                        curr.y - mouseDown[0].y);
            }
        });

        SimulatorEngine engine = new SimulatorEngine();

        root.add(titleBar, BorderLayout.NORTH);
        root.add(engine, BorderLayout.CENTER);

        frame.add(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}