import java.util.Stack;

public class PDASimulator {

    public static boolean simulate(String input) {

        Stack<Character> stack = new Stack<>();
        stack.push('Z');

        String state = "q0";

        for (char c : input.toCharArray()) {

            if (state.equals("q0")) {

                if (c == 'a') {
                    stack.push('A');
                }
                else if (c == 'b' && stack.peek() == 'A') {
                    stack.pop();
                    state = "q1";
                }
                else {
                    return false;
                }
            }

            else if (state.equals("q1")) {

                if (c == 'b' && stack.peek() == 'A') {
                    stack.pop();
                }
                else {
                    return false;
                }
            }
        }

        return state.equals("q1") && stack.size() == 1;
    }
}