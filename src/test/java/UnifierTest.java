import com.github.geje1017.logic.Unifier;
import com.github.geje1017.logic.UnifyResult;
import com.github.geje1017.term.*;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UnifierTest {

    private Constant a;         // a
    private Constant b;         // b
    private Variable x;         // X
    private Variable y;         // Y
    private Function f_x;       // f(X)
    private Function f_y;       // f(y)
    private Function g_x;       // g(X)
    private Function g_y;       // g(Y)
    private Function f_ax;      // f(a,X)
    private Function f_ab;      // f(a,b)
    private Function f_y_a;     // f(Y,a)
    private Function f_g_x;     // f(g(X))
    private Function f_g_x_x;   // f(g(X),X)

    @BeforeEach
    void setUp() {
        a = new Constant("a");
        b = new Constant("b");
        x = new Variable("X");
        y = new Variable("Y");
        f_x = new Function("f", x);
        f_y = new Function("f", y);
        g_x = new Function("g", x);
        g_y = new Function("g", y);
        f_ax = new Function("f", a, x);
        f_ab = new Function("f", a, b);
        f_y_a = new Function("f", y, a);
        f_g_x = new Function("f", g_x);
        f_g_x_x = new Function("f", g_x, x);
    }

    /**
     * Utility to assert expected substitution mapping for a variable.
     */
    private static void assertBinding(UnifyResult result, Variable var, Term expected) {
        assertTrue(result.isSuccess(), "Expected unification to succeed for binding " + var);
        Term actual = result.getSubstitution().lookup(var);
        assertEquals(expected, actual,
                () -> String.format("Variable %s should map to %s but was %s", var, expected, actual));
    }

    @Test
	// Tests: a = a; 		
	// Result: Should succeed with empty substitution
    void testEquatingSameConstant() {
        Equation eq = new Equation(a, a);
        UnifyResult result = Unifier.unify(List.of(eq));
        assertTrue(result.isSuccess(), "Unification of identical constants should succeed");
        assertTrue(result.getSubstitution().entrySet().isEmpty(),
                "Substitution should remain empty when unifying identical constants");
    }

    @Test
	// Tests: a = b; 		
	// Result: Should fail
    void testEquatingDifferentConstants() {
        Equation eq = new Equation(a, b);
        UnifyResult result = Unifier.unify(List.of(eq));
        assertFalse(result.isSuccess(), "Unification of different constants should fail");
        assertTrue(result.getSubstitution().entrySet().isEmpty(),
                "Substitution should remain empty after failure");
    }

    @Test
	// Tests: X = a and a = X; 		
	// Result: Should map X->a
    void testVariableToConstant() {
        UnifyResult result1 = Unifier.unify(List.of(new Equation(x, a)));
        assertBinding(result1, x, a);
	// Ensure only one binding
        assertEquals(1, result1.getSubstitution().entrySet().size());

        UnifyResult result2 = Unifier.unify(List.of(new Equation(a, x)));
        assertBinding(result2, x, a);
        assertEquals(1, result2.getSubstitution().entrySet().size());
    }

    @Test
	// Tests: X = Y; 		
	// Result: Should map X->Y and Y remains unbound
    void testVariableToVariable() {
        UnifyResult result = Unifier.unify(List.of(new Equation(x, y)));
        assertBinding(result, x, y);
        assertEquals(1, result.getSubstitution().entrySet().size(),
                "Only one binding expected");
	// Y should be unbound: lookup returns itself
        assertEquals(y, result.getSubstitution().lookup(y),
                "Y should remain unbound and map to itself");
    }

    @Test
	// Tests: f(X) = f(a,b); 		
	// Result: Should fail due to arity mismatch
    void testFunctionArityMismatch() {
        UnifyResult result = Unifier.unify(List.of(new Equation(f_x, f_ab)));
        assertFalse(result.isSuccess(), "Arity mismatch should cause failure");
    }

    @Test
	// Tests: f(X) = g(Y)		
	// Result: Should fail due to name mismatch
    void testFunctionNameMismatch() {
        UnifyResult result = Unifier.unify(List.of(new Equation(f_x, g_y)));
        assertFalse(result.isSuccess(), "Function name mismatch should cause failure");
    }

    @Test
	// Tests: f(a,X) = f(a,b)		
	// Result: Should bind X->b
    void testSimpleFunctionUnification() {
        UnifyResult result = Unifier.unify(List.of(new Equation(f_ax, f_ab)));
        assertBinding(result, x, b);
        assertEquals(1, result.getSubstitution().entrySet().size());
    }

    @Test
	// Tests: f(g(X)) = f(Y)		
	// Result: Should bind Y->g(X)
    void testNestedFunctionUnification() {
        UnifyResult result = Unifier.unify(List.of(new Equation(f_g_x, f_y)));
        assertBinding(result, y, g_x);
        assertEquals(1, result.getSubstitution().entrySet().size());
    }

    @Test
	// Tests: f(g(X), X) = f(Y, a)		
	// Result: Should bind X->a then Y->g(a)
    void testNestedFunctionWithMultipleArgs() {
        UnifyResult result = Unifier.unify(List.of(new Equation(f_g_x_x, f_y_a)));
	// First binding: X->a
        assertBinding(result, x, a);
	// After applying X->a, g(X) becomes g(a)
        assertBinding(result, y, new Function("g", a));
        assertEquals(2, result.getSubstitution().entrySet().size());
    }

    @Test
	// Tests: X = f(X)		
	// Result: Should fail occurs-check
    void testOccursCheck() {
        UnifyResult result = Unifier.unify(List.of(new Equation(x, f_x)));
        assertFalse(result.isSuccess(), "Occurs-check should prevent circular binding");
        assertTrue(result.getSubstitution().entrySet().isEmpty(),
                "No bindings should be produced on failure");
    }

    @Test
	// Tests: X=Y, Y=a		
	// Result: Should bind X->a and Y->a
    void testMultipleEquationsConsistent() {
        UnifyResult result = Unifier.unify(List.of(
                new Equation(x, y),
                new Equation(y, a)
        ));
        assertBinding(result, x, a);
        assertBinding(result, y, a);
        assertEquals(2, result.getSubstitution().entrySet().size());
    }

    @Test
	// Tests order independence: a=Y, X=Y		
	// Result: Should bind Y->a and X->a
    void testMultipleEquationsOrderIndependence() {
        UnifyResult result = Unifier.unify(List.of(
                new Equation(a, y),
                new Equation(x, y)
        ));
        assertBinding(result, y, a);
        assertBinding(result, x, a);
        assertEquals(2, result.getSubstitution().entrySet().size());
    }

    @Test
	// Tests conflicting equations: X=a, b=X		
	// Result: Should fail
    void testMultipleEquationsConflict() {
        UnifyResult result = Unifier.unify(List.of(
                new Equation(x, a),
                new Equation(b, x)
        ));
        assertFalse(result.isSuccess(), "Conflicting equations should fail");
        assertTrue(result.getSubstitution().entrySet().isEmpty(),
                "No bindings should remain after failure");
    }

    @Test
	// Tests trace content for f(a,X)=f(a,b)
    void testTraceContainsStepInfo() {
        Equation eq = new Equation(f_ax, f_ab);
        UnifyResult result = Unifier.unify(List.of(eq));
        List<String> trace = result.getTrace();
        assertFalse(trace.isEmpty(), "Trace should not be empty");
	// Check that the first step logs Step 1 and context
        assertTrue(trace.get(0).startsWith("Step 1:"), "Trace should start with step number");
        assertTrue(trace.stream().anyMatch(s -> s.contains("Remaining equations:")),
                "Trace should list remaining equations");
        assertTrue(trace.stream().anyMatch(s -> s.contains("Current substitution:")),
                "Trace should list current substitution");
    }

}