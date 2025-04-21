package com.github.geje1017.logic;

import com.github.geje1017.term.Equation;
import com.github.geje1017.term.Function;
import com.github.geje1017.term.Term;
import com.github.geje1017.term.Variable;

import java.util.*;

/**
 * Führt die Unifikation (Most‑General Unifier) über ein Gleichungssystem durch.
 * Zeichnet jeden Schritt im Trace auf und liefert ihn in UnifyResult zurück.
 */
public abstract class Unifier {

    public static class ClashException extends Exception {
        public ClashException(String message) {
            super(message);
        }
    }

    /**
     * Berechnet den most‑general unifier für eine Menge von Gleichungen
     * und erzeugt einen Trace aller angewendeten Regeln.
     *
     * @param equations die Ausgangs‑Gleichungen
     * @return UnifyResult mit Substitution und Schritt‑für‑Schritt‑Trace
     */
    public static UnifyResult unify(Collection<Equation> equations) {
        Deque<Equation> equationsToBeChecked = new ArrayDeque<>(equations);
        Substitution substitutionTable = new Substitution();
        List<String> trace = new ArrayList<>();

        try {
            while (!equationsToBeChecked.isEmpty()) {
                process(equationsToBeChecked, substitutionTable, trace);
            }
            return UnifyResult.success(substitutionTable, trace);
        } catch (ClashException e) {
            trace.add("CLASH: " + e.getMessage());
            return UnifyResult.failure(trace);
        }
    }

    /**
     * Dispatcher für die fünf Unifikationsregeln.
     *
     * @param work               Stapel der noch zu bearbeitenden Gleichungen
     * @param substitution       aktuelle Substitutionstabelle
     * @param trace              Liste zur Aufzeichnung der Schritte
     * @throws ClashException   bei Regel (5) oder Occurs‑Check‑Fehler
     */
    private static void process(Deque<Equation> work,
                                Substitution substitution,
                                List<String> trace) throws ClashException {
        Equation equation = work.pop();
        Term leftTerm  = equation.left().instantiatedWith(substitution);
        Term rightTerm = equation.right().instantiatedWith(substitution);

        trace.add("Verarbeite Gleichung: " + leftTerm + " ≐ " + rightTerm);

        if (isDelete(leftTerm, rightTerm)) {
            trace.add("  → DELETE");
            handleDelete();
        }
        else if (isSwap(leftTerm, rightTerm)) {
            trace.add("  → SWAP");
            handleSwap(leftTerm, rightTerm, work);
        }
        else if (isEliminate(leftTerm)) {
            trace.add("  → ELIMINATE: " + leftTerm + " ↦ " + rightTerm);
            handleEliminate((Variable) leftTerm, rightTerm, substitution);
        }
        else if (leftTerm instanceof Function lf && rightTerm instanceof Function rf) {
            checkFunctionName(lf, rf);
            checkFunctionArity(lf, rf);
            trace.add("  → DECOMPOSE");
            handleDecompose(lf, rf, work);
        }
        else {
            // CLASH
            throw new ClashException(leftTerm + " und " + rightTerm + " sind nicht unifizierbar");
        }
    }

    /** DELETE, true wenn beide Terme gleich sind. */
    private static boolean isDelete(Term left, Term right) {
        return left.equals(right);
    }

    /** DELETE‑Regel: nichts tun. */
    private static void handleDelete() {
        // No-op
    }

    /** SWAP/ORIENT, true wenn links kein Variable und rechts eine Variable ist. */
    private static boolean isSwap(Term left, Term right) {
        return !(left instanceof Variable) && right instanceof Variable;
    }

    /**
     * SWAP/ORIENT‑Regel: tauscht Term‑Paar.
     */
    private static void handleSwap(Term left, Term right, Deque<Equation> work) {
        work.push(new Equation(right, left));
    }

    /** ELIMINATE, true wenn links eine Variable ist. */
    private static boolean isEliminate(Term left) {
        return left instanceof Variable;
    }

    /**
     * ELIMINATE‑Regel: bindet Variable → Term nach Occurs‑Check.
     *
     * @throws ClashException wenn Occurs‑Check fehlschlägt
     */
    private static void handleEliminate(Variable variable,
                                        Term term,
                                        Substitution substitution) throws ClashException {
        checkOccurrence(variable, term);
        substitution.put(variable, term);
    }

    /** DECOMPOSE, true wenn beide Funktionen kompatibel sind. */
    private static boolean isDecompose(Term left, Term right) {
        return left instanceof Function lf
                && right instanceof Function rf;
    }

    /**
     * DECOMPOSE‑Regel: zerlegt kompatible Funktionen in Argument‑Gleichungen.
     */
    private static void handleDecompose(Function leftFunction,
                                        Function rightFunction,
                                        Deque<Equation> equations) {
        for (int i = 0; i < leftFunction.getArity(); i++) {
            equations.push(new Equation(
                    leftFunction.getArgumentOnPosition(i),
                    rightFunction.getArgumentOnPosition(i)
            ));
        }
    }

    /**
     * Führt den Occurs‑Check durch und wirft bei Zirkularität eine Exception.
     *
     * @throws ClashException wenn variable in term enthalten ist
     */
    private static void checkOccurrence(Variable variable,
                                        Term term) throws ClashException {
        if (term.getContainedVariables().contains(variable)) {
            throw new ClashException(
                    "Occurs‑Check fehlgeschlagen: " +
                            variable + " kommt in " + term + " vor"
            );
        }
    }

    /**
     * Check: Funktionsnamen müssen identisch sein.
     *
     * @throws ClashException wenn die Namen unterschiedlich sind
     */
    private static void checkFunctionName(Function lf, Function rf) throws ClashException {
        String nameL = lf.getName();
        String nameR = rf.getName();
        if (!nameL.equals(nameR)) {
            throw new ClashException(
                    "Funktionen haben unterschiedliche Namen: " +
                            nameL + " vs. " + nameR
            );
        }
    }

    /**
     * Check: Funktionsarikitäten (Wertigkeiten) müssen übereinstimmen.
     *
     * @throws ClashException wenn die Arity unterschiedlich sind
     */
    private static void checkFunctionArity(Function lf, Function rf) throws ClashException {
        int arL = lf.getArity();
        int arR = rf.getArity();
        if (arL != arR) {
            throw new ClashException(
                    "Funktionen haben unterschiedliche Wertigkeiten: " +
                            lf.getName() + "(" + arL + ") vs. " +
                            rf.getName() + "(" + arR + ")"
            );
        }
    }

}
