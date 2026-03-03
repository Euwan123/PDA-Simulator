import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(1100, 700);
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setShape(new RoundRectangle2D.Double(0, 0, 1100, 700, 24, 24));
            frame.setLayout(new BorderLayout());

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(new Color(13, 17, 30));
            root.setBorder(BorderFactory.createLineBorder(new Color(50, 60, 90), 1));

            TitleBar titleBar = new TitleBar(frame);
            root.add(titleBar, BorderLayout.NORTH);

            SimulatorEngine engine = new SimulatorEngine();
            root.add(engine, BorderLayout.CENTER);

            frame.add(root);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}