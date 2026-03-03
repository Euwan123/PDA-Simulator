import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class StackPanel extends JPanel {

    private Stack<Character> stack = new Stack<>();
    private int offset = 0;

    public StackPanel() {
        setBackground(new Color(245,247,250));
    }

    public void updateStack(Stack<Character> newStack) {
        stack = (Stack<Character>) newStack.clone();
        repaint();
    }

    public void animatePush(Stack<Character> newStack) {
        stack = (Stack<Character>) newStack.clone();
        offset = -30;

        Timer t = new Timer(10, e -> {
            offset += 2;
            if(offset >= 0) {
                offset = 0;
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });

        t.start();
    }

    public void animatePop(Stack<Character> newStack) {

        Timer t = new Timer(10, e -> {
            offset -= 2;
            if(offset <= -30) {
                stack = (Stack<Character>) newStack.clone();
                offset = 0;
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });

        t.start();
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int baseY = getHeight() - 60;

        for(int i=0;i<stack.size();i++) {

            int y = baseY - (i*45) + offset;

            g2.setColor(new Color(100,149,237));
            g2.fillRoundRect(getWidth()/2-45,y,90,35,15,15);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif",Font.BOLD,16));
            g2.drawString(stack.get(i).toString(),getWidth()/2-5,y+23);
        }
    }
}