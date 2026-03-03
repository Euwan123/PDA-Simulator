import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PDASimulator {

    public static class StepResult {
        public String fromState;
        public String toState;
        public char inputSymbol;
        public String stackBefore;
        public String stackAfter;
        public String transition;
        public boolean rejected;

        public StepResult(String from, String to, char sym, String sb, String sa, String trans, boolean rej) {
            fromState = from;
            toState = to;
            inputSymbol = sym;
            stackBefore = sb;
            stackAfter = sa;
            transition = trans;
            rejected = rej;
        }
    }

    public static class SimulationResult {
        public boolean accepted;
        public List<StepResult> steps = new ArrayList<>();
    }

    public static SimulationResult simulate(String input) {
        SimulationResult result = new SimulationResult();

        if (input.isEmpty()) {
            result.accepted = false;
            return result;
        }

        Stack<Character> stack = new Stack<>();
        stack.push('Z');
        String state = "q0";

        for (char c : input.toCharArray()) {
            String stackBefore = stackToString(stack);
            String fromState = state;

            if (state.equals("q0")) {
                if (c == 'a') {
                    stack.push('A');
                    String trans = "δ(q0, a, " + stackBefore.charAt(0) + ") = (q0, A" + stackBefore.charAt(0) + ")";
                    result.steps.add(new StepResult(fromState, "q0", c, stackBefore, stackToString(stack), trans, false));
                } else if (c == 'b' && !stack.isEmpty() && stack.peek() == 'A') {
                    stack.pop();
                    state = "q1";
                    String trans = "δ(q0, b, A) = (q1, ε)";
                    result.steps.add(new StepResult(fromState, "q1", c, stackBefore, stackToString(stack), trans, false));
                } else {
                    String trans = "No valid transition for (" + fromState + ", " + c + ", " + (stack.isEmpty() ? "∅" : stack.peek()) + ")";
                    result.steps.add(new StepResult(fromState, fromState, c, stackBefore, stackBefore, trans, true));
                    result.accepted = false;
                    return result;
                }
            } else if (state.equals("q1")) {
                if (c == 'b' && !stack.isEmpty() && stack.peek() == 'A') {
                    stack.pop();
                    String trans = "δ(q1, b, A) = (q1, ε)";
                    result.steps.add(new StepResult(fromState, "q1", c, stackBefore, stackToString(stack), trans, false));
                } else {
                    String trans = "No valid transition for (" + fromState + ", " + c + ", " + (stack.isEmpty() ? "∅" : stack.peek()) + ")";
                    result.steps.add(new StepResult(fromState, fromState, c, stackBefore, stackBefore, trans, true));
                    result.accepted = false;
                    return result;
                }
            }
        }

        if (state.equals("q1") && stack.size() == 1 && stack.peek() == 'Z') {
            String stackBefore = stackToString(stack);
            stack.push('Z');
            stack.pop();
            String trans = "δ(q1, ε, Z) = (q2, Z)";
            result.steps.add(new StepResult("q1", "q2", 'ε', stackBefore, stackBefore, trans, false));
            result.accepted = true;
        } else {
            result.accepted = false;
        }

        return result;
    }

    private static String stackToString(Stack<Character> stack) {
        if (stack.isEmpty()) return "∅";
        StringBuilder sb = new StringBuilder();
        for (int i = stack.size() - 1; i >= 0; i--) sb.append(stack.get(i));
        return sb.toString();
    }
}