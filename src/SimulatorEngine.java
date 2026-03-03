import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Stack;

public class SimulatorEngine extends JPanel {

    private JTextField inputField;
    private JButton runBtn, stepBtn, resetBtn;
    private JSlider speedSlider;
    private JLabel stateLabel, pointerLabel, resultLabel, inputDisplay;
    private JTextArea logArea;
    private StackPanel stackPanel;
    private DiagramPanel diagramPanel;
    private JPanel presetPanel;

    private Timer timer;
    private String input;
    private int pointer;
    private String currentState;
    private Stack<Character> stack;
    private List<PDASimulator.StepResult> steps;
    private int stepIndex;
    private int delay = 700;

    private static final Color BG = new Color(13, 17, 30);
    private static final Color PANEL_BG = new Color(18, 24, 42);
    private static final Color BORDER_COLOR = new Color(40, 55, 90);
    private static final Color ACCENT = new Color(99, 179, 237);
    private static final Color TEXT_MAIN = new Color(200, 220, 255);
    private static final Color TEXT_DIM = new Color(100, 130, 180);
    private static final Color SUCCESS = new Color(72, 199, 142);
    private static final Color DANGER = new Color(255, 100, 100);

    public SimulatorEngine() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        add(buildControlBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 0));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(8, 12, 8, 12));

        diagramPanel = new DiagramPanel();
        center.add(diagramPanel, BorderLayout.WEST);

        JPanel middle = new JPanel(new BorderLayout(0, 8));
        middle.setBackground(BG);
        middle.add(buildInputDisplay(), BorderLayout.NORTH);
        middle.add(buildLogPanel(), BorderLayout.CENTER);
        middle.add(buildStatusBar(), BorderLayout.SOUTH);
        center.add(middle, BorderLayout.CENTER);

        stackPanel = new StackPanel();
        center.add(stackPanel, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);
        add(buildPresets(), BorderLayout.SOUTH);

        initSimState();
    }

    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(new Color(16, 22, 38));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel lbl = new JLabel("Input:");
        lbl.setForeground(TEXT_DIM);
        lbl.setFont(new Font("Courier New", Font.PLAIN, 13));

        inputField = new JTextField(20);
        inputField.setBackground(new Color(22, 32, 58));
        inputField.setForeground(TEXT_MAIN);
        inputField.setCaretColor(ACCENT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        inputField.setFont(new Font("Courier New", Font.PLAIN, 14));

        runBtn = makeBtn("▶  Auto Run", new Color(30, 90, 160));
        stepBtn = makeBtn("⊳  Step", new Color(40, 70, 110));
        resetBtn = makeBtn("↺  Reset", new Color(50, 40, 80));

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setForeground(TEXT_DIM);
        speedLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));

        speedSlider = new JSlider(100, 1500, 700);
        speedSlider.setBackground(new Color(16, 22, 38));
        speedSlider.setForeground(TEXT_DIM);
        speedSlider.setPreferredSize(new Dimension(120, 30));

        runBtn.addActionListener(e -> startAuto());
        stepBtn.addActionListener(e -> doStep());
        resetBtn.addActionListener(e -> reset());
        speedSlider.addChangeListener(e -> delay = speedSlider.getValue());
        inputField.addActionListener(e -> startAuto());

        bar.add(lbl);
        bar.add(inputField);
        bar.add(runBtn);
        bar.add(stepBtn);
        bar.add(resetBtn);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(speedLbl);
        bar.add(speedSlider);

        return bar;
    }

    private JPanel buildInputDisplay() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        p.setPreferredSize(new Dimension(0, 55));

        JLabel lbl = new JLabel("INPUT TAPE");
        lbl.setFont(new Font("Courier New", Font.BOLD, 10));
        lbl.setForeground(TEXT_DIM);

        inputDisplay = new JLabel("—");
        inputDisplay.setFont(new Font("Courier New", Font.BOLD, 18));
        inputDisplay.setForeground(TEXT_MAIN);

        p.add(lbl, BorderLayout.NORTH);
        p.add(inputDisplay, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane buildLogPanel() {
        logArea = new JTextArea();
        logArea.setBackground(new Color(10, 14, 26));
        logArea.setForeground(new Color(160, 200, 160));
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        logArea.setText("  Transition log will appear here...\n");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(new Color(10, 14, 26));
        return scroll;
    }

    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        p.setBackground(new Color(16, 22, 38));
        p.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));

        stateLabel = makeStatusLabel("State: q0");
        pointerLabel = makeStatusLabel("Position: 0");
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Courier New", Font.BOLD, 14));

        p.add(stateLabel);
        p.add(makeDivider());
        p.add(pointerLabel);
        p.add(makeDivider());
        p.add(resultLabel);
        return p;
    }

    private JPanel buildPresets() {
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        outer.setBackground(new Color(10, 14, 26));
        outer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JLabel lbl = new JLabel("Quick Test:");
        lbl.setForeground(TEXT_DIM);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        outer.add(lbl);

        String[][] presets = {
                {"ab", "✓"}, {"aabb", "✓"}, {"aaabbb", "✓"}, {"aaaabbbb", "✓"},
                {"a", "✗"}, {"b", "✗"}, {"aab", "✗"}, {"aba", "✗"}, {"bba", "✗"}
        };

        for (String[] preset : presets) {
            boolean valid = preset[1].equals("✓");
            JButton btn = new JButton(preset[1] + " " + preset[0]);
            btn.setFont(new Font("Courier New", Font.PLAIN, 12));
            btn.setForeground(valid ? SUCCESS : DANGER);
            btn.setBackground(valid ? new Color(20, 50, 35) : new Color(50, 20, 20));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(valid ? new Color(40, 100, 60) : new Color(100, 40, 40)),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String val = preset[0];
            btn.addActionListener(e -> {
                inputField.setText(val);
                startAuto();
            });
            outer.add(btn);
        }

        return outer;
    }

    private void initSimState() {
        input = "";
        pointer = 0;
        currentState = "q0";
        stack = new Stack<>();
        stack.push('Z');
        steps = null;
        stepIndex = 0;
    }

    private void reset() {
        if (timer != null) timer.stop();
        input = inputField.getText().trim();
        pointer = 0;
        currentState = "q0";
        stack = new Stack<>();
        stack.push('Z');
        steps = null;
        stepIndex = 0;
        stackPanel.setStack(stack);
        diagramPanel.setActiveState("q0");
        diagramPanel.setActiveTransition("");
        stateLabel.setText("State: q0");
        pointerLabel.setText("Position: 0");
        resultLabel.setText("");
        updateInputDisplay(input, -1);
        logArea.setText("  Transition log will appear here...\n");
    }

    private void startAuto() {
        reset();
        PDASimulator.SimulationResult result = PDASimulator.simulate(input);
        steps = result.steps;
        stepIndex = 0;

        if (input.isEmpty()) {
            showResult(false);
            return;
        }

        timer = new Timer(delay, e -> {
            if (stepIndex < steps.size()) {
                applyStep(steps.get(stepIndex));
                stepIndex++;
            } else {
                boolean accepted = result.accepted;
                showResult(accepted);
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void doStep() {
        if (steps == null) {
            input = inputField.getText().trim();
            if (input.isEmpty()) { showResult(false); return; }
            PDASimulator.SimulationResult result = PDASimulator.simulate(input);
            steps = result.steps;
            stepIndex = 0;
            stack = new Stack<>();
            stack.push('Z');
            currentState = "q0";
            pointer = 0;
            stackPanel.setStack(stack);
            diagramPanel.setActiveState("q0");
            stateLabel.setText("State: q0");
            pointerLabel.setText("Position: 0");
            resultLabel.setText("");
            updateInputDisplay(input, -1);
            logArea.setText("  Stepping through transitions...\n");
        }

        if (stepIndex < steps.size()) {
            applyStep(steps.get(stepIndex));
            stepIndex++;
        } else {
            PDASimulator.SimulationResult result = PDASimulator.simulate(input);
            showResult(result.accepted);
        }
    }

    private void applyStep(PDASimulator.StepResult step) {
        currentState = step.toState;
        pointer++;

        stack = new Stack<>();
        stack.push('Z');
        stack.clear();
        String stackStr = step.stackAfter;
        if (!stackStr.equals("∅")) {
            for (int i = stackStr.length() - 1; i >= 0; i--) {
                stack.push(stackStr.charAt(i));
            }
        }

        if (step.transition.contains("Push") || step.transition.contains("→ A") || step.transition.contains("AA")) {
            stackPanel.animatePush(stack);
        } else {
            stackPanel.animatePop(stack);
        }

        stateLabel.setText("State: " + step.toState);
        pointerLabel.setText("Position: " + pointer);
        diagramPanel.setActiveState(step.toState);
        diagramPanel.setActiveTransition(step.transition + "\nStack: " + step.stackBefore + " → " + step.stackAfter);

        String sym = step.inputSymbol == 'ε' ? "ε" : String.valueOf(step.inputSymbol);
        logArea.append("  [" + pointer + "] " + step.fromState + " →" + sym + "→ " + step.toState
                + "  |  " + step.transition + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());

        if (step.rejected) {
            showResult(false);
        } else {
            updateInputDisplay(input, pointer - 1);
        }
    }

    private void showResult(boolean accepted) {
        if (accepted) {
            resultLabel.setText("✓  ACCEPTED");
            resultLabel.setForeground(SUCCESS);
            diagramPanel.setActiveState("q2");
            diagramPanel.setActiveTransition("δ(q1, ε, Z) = (q2, Z)\nAccepted: input ∈ L");
            logArea.append("  ──────────────────────────\n");
            logArea.append("  ✓ ACCEPTED — String is in L = {aⁿbⁿ | n ≥ 1}\n");
        } else {
            resultLabel.setText("✗  REJECTED");
            resultLabel.setForeground(DANGER);
            diagramPanel.setRejected();
            logArea.append("  ──────────────────────────\n");
            logArea.append("  ✗ REJECTED — String is NOT in L = {aⁿbⁿ | n ≥ 1}\n");
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
        if (timer != null) timer.stop();
    }

    private void updateInputDisplay(String inp, int pos) {
        if (inp.isEmpty()) { inputDisplay.setText("(empty)"); return; }
        StringBuilder sb = new StringBuilder("<html><span style='font-family:Courier New;font-size:16px;'>");
        for (int i = 0; i < inp.length(); i++) {
            if (i == pos) {
                sb.append("<span style='color:#63b3ed;background:#1a3a5c;padding:2px 5px;border-radius:3px;'>");
                sb.append(inp.charAt(i));
                sb.append("</span>");
            } else if (i < pos) {
                sb.append("<span style='color:#4a5568;'>").append(inp.charAt(i)).append("</span>");
            } else {
                sb.append("<span style='color:#c8d8ff;'>").append(inp.charAt(i)).append("</span>");
            }
        }
        sb.append("</span></html>");
        inputDisplay.setText(sb.toString());
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(TEXT_MAIN);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter()),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel makeStatusLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Courier New", Font.PLAIN, 12));
        lbl.setForeground(TEXT_DIM);
        return lbl;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 16));
        sep.setForeground(BORDER_COLOR);
        return sep;
    }
}