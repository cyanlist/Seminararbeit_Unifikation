import com.github.geje1017.logic.Unifier;
import com.github.geje1017.logic.UnifyResult;
import com.github.geje1017.term.*;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

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
    private List<Equation> equations;

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

        equations = new ArrayList<>();
    }

    @Test
    void testEquatingSameConstant() {
        Equation e1 = new Equation(a, a);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingDifferentConstants() {
        Equation e1 = new Equation(a, b);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertFalse(result.isSuccess());
    }

    @Test
    void testEquatingSameVariable() {
        Equation e1 = new Equation(x, x);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingVariableAndConstant() {
        Equation e1 = new Equation(x, a);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingConstantAndVariable() {
        Equation e1 = new Equation(a, x);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingDifferentVariables() {
        Equation e1 = new Equation(x, y);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingFunctions() {
        Equation e1 = new Equation(f_ax, f_ab);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingFunctionsWithVariables() {
        Equation e1 = new Equation(f_x, f_y);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingFunctionsWithVariablesAndDifferentNames() {
        Equation e1 = new Equation(f_x, g_y);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertFalse(result.isSuccess());
    }

    @Test
    void testEquatingFunctionsWithDifferentArity() {
        Equation e1 = new Equation(f_x, f_ab);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertFalse(result.isSuccess());
    }

    @Test
    void testEquatingNestedFunctions() {
        Equation e1 = new Equation(f_g_x, f_y);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testEquatingNestedFunctionsAndArity() {
        Equation e1 = new Equation(f_g_x_x, f_y_a);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertTrue(result.isSuccess());
    }

    @Test
    void testOccurrenceCheck() {
        Equation e1 = new Equation(x, f_x);
        UnifyResult result = Unifier.unify(List.of(e1));

        assertFalse(result.isSuccess());
    }

    @Test
    void testUnifyingMultipleEquations() {
        Equation e1 = new Equation(x, y);
        Equation e2 = new Equation(y, a);
        UnifyResult result = Unifier.unify(List.of(e1, e2));

        assertTrue(result.isSuccess());
    }

    @Test
    void testUnifyingMultipleEquations2() {
        Equation e1 = new Equation(a, y);
        Equation e2 = new Equation(x, y);
        UnifyResult result = Unifier.unify(List.of(e1, e2));

        assertTrue(result.isSuccess());
    }

    @Test
    void testUnifyingMultipleEquations3() {
        Equation e1 = new Equation(x, a);
        Equation e2 = new Equation(b, x);
        UnifyResult result = Unifier.unify(List.of(e1, e2));

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("DELETE: identische Terme verschwinden ohne Fehler")
    void testDeleteRule() {
        this.equations.add(new Equation(f_x, f_x));
        UnifyResult result = Unifier.unify(equations);

        assertTrue(result.isSuccess(), "Unifikation sollte erfolgreich sein");
        assertTrue(result.getTrace().stream().anyMatch(s -> s.contains("DELETE")),
                "Trace muss DELETE enthalten");
        assertTrue(result.getSubstitution().isEmpty(), "Keine Substitution erwartet");
    }
}