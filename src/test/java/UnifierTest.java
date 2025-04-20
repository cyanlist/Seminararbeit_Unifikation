import com.github.geje1017.logic.Substitution;
import com.github.geje1017.logic.UnifierTrace;
import com.github.geje1017.term.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Besser Dokumentieren
// TODO: Ergänzen
public class UnifierTest {

    /* ------------------------------------------------------------
     * Hilfs‑Funktion: ein Ein‑Gleichungs‑Satz → UnifierTrace.Result
     * ------------------------------------------------------------
     */
    private UnifierTrace.Result unifyOnce(Term s, Term t) {
        return UnifierTrace.unifyTrace(List.of(new Equation(s, t)));
    }

    /* ========== 1. Erfolgsfälle ================================= */

    /*
     *  ...:        X = a
     *  Ergebnis:   ...
     */
    @Test
    void varAgainstConstant_shouldUnify() {
        Variable X = new Variable("X");
        Term a = new Constant("a");

        var res = unifyOnce(X, a);

        assertTrue(res.mgu().isPresent(), "Unifikation sollte gelingen");
        Substitution expected = new Substitution();
        expected.put(X, a);
        assertEquals(expected, res.mgu().get(), "σ = {X ↦ a}");
    }

    @Test
    void sameConstant_shouldYieldEmptySubst() {
        Term a1 = new Constant("a");
        Term a2 = new Constant("a");

        var res = unifyOnce(a1, a2);

        assertTrue(res.mgu().isPresent(), "a = a ist immer erfüllbar");
        assertTrue(res.mgu().get().isEmpty(), "MGU sollte leer sein");
    }

    @Test
    void varAgainstVar_shouldBindLeftToRight() {
        Variable X = new Variable("X");
        Variable Y = new Variable("Y");

        var res = unifyOnce(X, Y);

        Substitution expected = new Substitution();
        expected.put(X, Y); // Eliminate: X = Y  ⇒  {X ↦ Y}
        assertEquals(Optional.of(expected), res.mgu());
    }

    @Test
    void simpleFunctionDecompose_shouldUnify() {
        Variable X = new Variable("X");

        Term s = new Function("f", X);      // f(X)
        Term t = new Function("f", new Constant("b")); // f(b)

        var res = unifyOnce(s, t);

        Substitution expected = new Substitution();
        expected.put(X, new Constant("b"));
        assertEquals(Optional.of(expected), res.mgu());
    }

    @Test
    void nestedTerms_shouldProduceTwoBindings() {
        Variable X = new Variable("X");
        Variable Y = new Variable("Y");
        Variable Z = new Variable("Z");

        Term s = new Function("f", X, new Function("g", Y));          // f(X, g(Y))
        Term t = new Function("f", new Function("g", Z), new Function("g", new Constant("a"))); // f(g(Z), g(a))

        var res = unifyOnce(s, t);

        assertTrue(res.mgu().isPresent());
        Substitution expected = new Substitution();
        expected.put(X, new Function("g", Z)); // X ↦ g(Z)
        expected.put(Y, new Constant("a"));    // Y ↦ a
        assertEquals(expected, res.mgu().get());
    }

    @Test
    void equalPrefixDifferingLastVar_shouldUnify() {
        Variable X = new Variable("X");
        Variable Y = new Variable("Y");

        Term s = new Function("f", new Constant("b"), X);
        Term t = new Function("f", new Constant("b"), Y);

        var res = unifyOnce(s, t);

        Substitution expected = new Substitution();
        expected.put(X, Y); // {X ↦ Y}
        assertEquals(Optional.of(expected), res.mgu());
    }

    @Test
    void swapVarsWithConstants_shouldBindBoth() {
        Variable X = new Variable("X");
        Variable Y = new Variable("Y");

        Term s = new Function("f", X, new Constant("a")); // f(X, a)
        Term t = new Function("f", new Constant("b"), Y); // f(b, Y)

        var res = unifyOnce(s, t);

        Substitution expected = new Substitution();
        expected.put(X, new Constant("b"));
        expected.put(Y, new Constant("a"));
        assertEquals(Optional.of(expected), res.mgu());
    }

    /* ========== 2. Fehl‑ & Clash‑Fälle ========================== */

    @Test
    void constantClash_shouldFail() {
        Term a = new Constant("a");
        Term b = new Constant("b");

        var res = unifyOnce(a, b);

        assertTrue(res.mgu().isEmpty(), "a ≠ b ⇒ keine Unifikation");
    }

    @Test
    void functorMismatch_shouldFail() {
        Variable X = new Variable("X");

        Term s = new Function("f", X);
        Term t = new Function("g", X); // anderer Funktor

        var res = unifyOnce(s, t);

        assertTrue(res.mgu().isEmpty(), "Unterschiedliche Funktorsymbole ⇒ Clash");
    }

    @Test
    void occursCheck_shouldFail() {
        Variable X = new Variable("X");

        Term s = X;
        Term t = new Function("f", X); // X in f(X)  ⇒ Zyklus

        var res = unifyOnce(s, t);

        assertTrue(res.mgu().isEmpty(), "Occurs‑Check muss fehlschlagen");
    }
}