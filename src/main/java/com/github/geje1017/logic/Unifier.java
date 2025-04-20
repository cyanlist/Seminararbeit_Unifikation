package com.github.geje1017.logic;

import com.github.geje1017.term.Equation;
import com.github.geje1017.term.Function;
import com.github.geje1017.term.Term;
import com.github.geje1017.term.Variable;

import java.util.*;

/**
 * Führt die Unifikation (Most‑General Unifier) über ein Gleichungssystem durch.
 */
public abstract class Unifier {

    /**
     * Exception, die geworfen wird, wenn im Unifikationsprozess
     * ein Clash (Unvereinbarkeit) auftritt.
     */
    public static class ClashException extends Exception {
        public ClashException(String message) {
            super(message);
        }
    }

    /**
     * Berechnet den most‑general unifier für eine Menge von Gleichungen.
     *
     * @param equations die Ausgangs‑Gleichungen
     * @return {@link UnifyResult#success(Substitution)} bei Erfolg,
     *         sonst {@link UnifyResult#failure()}
     */
    public static UnifyResult unify(Collection<Equation> equations) {
        Deque<Equation> equationsToBeChecked = new ArrayDeque<>(equations);
        Substitution substitutionTable = new Substitution();

        try {
            while (!equationsToBeChecked.isEmpty()) {
                process(equationsToBeChecked, substitutionTable);
            }
            return UnifyResult.success(substitutionTable);
        } catch (ClashException e) {
            return UnifyResult.failure();
        }
    }

    // TODO: Pattern Matching herausfinden

    /**
     * Dispatcher für die fünf Unifikationsregeln.
     *
     * @param work         Stapel der noch zu bearbeitenden Gleichungen
     * @param substitution aktuelle Substitutionstabelle
     * @throws ClashException bei Regel (5) oder Occurs‑Check‑Fehler
     */
    private static void process(Deque<Equation> work,
                                Substitution substitution) throws ClashException {

        Equation equation = work.pop();
        Term leftTerm  = equation.left().instantiatedWith(substitution);
        Term rightTerm = equation.right().instantiatedWith(substitution);

        if (isDelete(leftTerm, rightTerm)) {
            handleDelete();
        }
        else if (isSwap(leftTerm, rightTerm)) {
            handleSwap(leftTerm, rightTerm, work);
        }
        else if (isEliminate(leftTerm)) {
            handleEliminate((Variable) leftTerm, rightTerm, substitution);
        }
        else if (isDecompose(leftTerm, rightTerm)) {
            handleDecompose((Function) leftTerm, (Function) rightTerm, work);
        }
        else {
            handleClash(leftTerm, rightTerm);
        }
    }

    /** DELETE, true wenn beide Terme gleich sind. */
    private static boolean isDelete(Term left, Term right) {
        return left.equals(right);
    }

    /** DELETE‑Regel: nichts tun. */
    private static void handleDelete() {
        // symbolisch; nichts zu tun hier
    }

    /** SWAP/ORIENT, true wenn links kein Variable und rechts eine Variable ist. */
    private static boolean isSwap(Term left, Term right) {
        return !(left instanceof Variable) && right instanceof Variable;
    }

    /**
     * SWAP/ORIENT‑Regel: tauscht Termpaar.
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
    private static void handleEliminate(Variable variable, Term term, Substitution substitution) throws ClashException {
        checkOccurrence(variable, term);
        substitution.put(variable, term);
    }

    /** DECOMPOSE, true wenn beide Funktionen kompatibel sind. */
    private static boolean isDecompose(Term left, Term right) {
        return left instanceof Function lf
                && right instanceof Function rf
                && lf.isCompatibleWith(rf);
    }

    /**
     * DECOMPOSE‑Regel: zerlegt kompatible Funktionen in Argument‑Gleichungen.
     */
    private static void handleDecompose(Function leftFunction, Function rightFunction, Deque<Equation> equations) {
        for (int i = 0; i < leftFunction.getArity(); i++) {
            equations.push(new Equation(
                    leftFunction.getArgumentOnPosition(i),
                    rightFunction.getArgumentOnPosition(i)
            ));
        }
    }

    /**
     * CLASH: keine Regel anwendbar → Fehler.
     *
     * @throws ClashException immer
     */
    private static void handleClash(Term left, Term right) throws ClashException {
        throw new ClashException(left + " und " + right + " sind nicht unfizierbar");
    }

    /**
     * Führt den Occurs‑Check durch und wirft bei Zirkularität eine Exception.
     *
     * @throws ClashException wenn variable in term enthalten ist
     */
    private static void checkOccurrence(Variable variable, Term term) throws ClashException {
        if (term.getContainedVariables().contains(variable)) {
            throw new ClashException("Occurs‑Check fehlgeschlagen: " + variable + " kommt in " + term + " vor");
        }
    }
}
