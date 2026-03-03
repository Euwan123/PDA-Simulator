import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class SimulatorEngine extends JPanel {

    private JTextField inputField;
    private JButton autoBtn;
    private JButton stepBtn;
    private JButton resetBtn;
    private JSlider speedSlider;

    private JLabel stateLabel;
    private JLabel pointerLabel;
    private JLabel resultLabel;

    private StackPanel stackPanel;

    private Timer timer;
    private String input;
    private int pointer;
    private String state;
    private Stack<Character> stack;
    private int delay = 700;

    public SimulatorEngine() {

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        JPanel top = new JPanel();
        top.setBackground(new Color(235,240,245));

        inputField = new JTextField(20);
        autoBtn = new JButton("Auto");
        stepBtn = new JButton("Step");
        resetBtn = new JButton("Reset");
        speedSlider = new JSlider(100,1500,700);

        top.add(new JLabel("Input"));
        top.add(inputField);
        top.add(autoBtn);
        top.add(stepBtn);
        top.add(resetBtn);
        top.add(new JLabel("Speed"));
        top.add(speedSlider);

        add(top, BorderLayout.NORTH);

        stackPanel = new StackPanel();
        add(stackPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(235,240,245));

        stateLabel = new JLabel("State: q0");
        pointerLabel = new JLabel("Pointer: 0");
        resultLabel = new JLabel("");

        bottom.add(stateLabel);
        bottom.add(pointerLabel);
        bottom.add(resultLabel);

        add(bottom, BorderLayout.SOUTH);

        autoBtn.addActionListener(e -> startAuto());
        stepBtn.addActionListener(e -> step());
        resetBtn.addActionListener(e -> reset());

        speedSlider.addChangeListener(e -> delay = speedSlider.getValue());
    }

    private void reset() {
        if(timer != null) timer.stop();
        input = inputField.getText();
        pointer = 0;
        state = "q0";
        stack = new Stack<>();
        stack.push('Z');
        stackPanel.updateStack(stack);
        stateLabel.setText("State: q0");
        pointerLabel.setText("Pointer: 0");
        resultLabel.setText("");
    }

    private void startAuto() {
        reset();
        timer = new Timer(delay, e -> step());
        timer.start();
    }

    private void step() {

        if(pointer >= input.length()) {

            if(state.equals("q1") && stack.size()==1) {
                resultLabel.setText("ACCEPTED");
                resultLabel.setForeground(new Color(0,150,0));
            } else {
                resultLabel.setText("REJECTED");
                resultLabel.setForeground(Color.RED);
            }

            if(timer != null) timer.stop();
            return;
        }

        char c = input.charAt(pointer);

        if(state.equals("q0")) {

            if(c=='a') {
                stack.push('A');
                stackPanel.animatePush(stack);
            }
            else if(c=='b' && stack.peek()=='A') {
                stack.pop();
                state="q1";
                stackPanel.animatePop(stack);
            }
            else {
                resultLabel.setText("REJECTED");
                resultLabel.setForeground(Color.RED);
                if(timer!=null) timer.stop();
                return;
            }
        }
        else if(state.equals("q1")) {

            if(c=='b' && stack.peek()=='A') {
                stack.pop();
                stackPanel.animatePop(stack);
            }
            else {
                resultLabel.setText("REJECTED");
                resultLabel.setForeground(Color.RED);
                if(timer!=null) timer.stop();
                return;
            }
        }

        pointer++;
        pointerLabel.setText("Pointer: "+pointer);
        stateLabel.setText("State: "+state);
    }
}