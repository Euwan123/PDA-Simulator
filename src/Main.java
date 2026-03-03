import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(1200, 780);
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setShape(new RoundRectangle2D.Double(0, 0, 1200, 780, 24, 24));
            frame.setLayout(new BorderLayout());
            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(new Color(15, 20, 40));
            root.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 130), 2));
            root.add(new TitleBar(frame), BorderLayout.NORTH);
            root.add(new SimulatorEngine(), BorderLayout.CENTER);
            frame.add(root);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}