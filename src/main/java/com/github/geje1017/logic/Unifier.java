// Unifier.java
package com.github.geje1017.logic;

import com.github.geje1017.term.Equation;
import com.github.geje1017.term.Function;
import com.github.geje1017.term.Term;
import com.github.geje1017.term.Variable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * Provides static methods to unify a set of equations between terms,
 * producing the most general unifier (MGU) and a detailed trace of steps.
 * Occurs-check logic is performed within this class.
 */
public abstract class Unifier {

    /**
     * Exception thrown when unification fails due to a term conflict
     * or occurs-check violation.
     */
    public static class ClashException extends Exception {
        /**
         * Constructs a new ClashException with the specified detail message.
         * @param message the detail message
         */
        public ClashException(String message) { super(message); }
    }

    /**
     * Unifies the provided collection of equations, returning a UnifyResult
     * containing a success flag, substitution, and a trace of steps.
     * Each step logs the remaining equations and current substitution.
     * @param equations the initial set of equations to unify
     * @return a UnifyResult with success status, MGU substitution, and trace
     */
    public static UnifyResult unify(Collection<Equation> equations) {
        Deque<Equation> workQueue = new ArrayDeque<>(equations);
        Substitution substitution = new Substitution();
        List<String> trace = new ArrayList<>();
        int step = 0;

        try {
            while (!workQueue.isEmpty()) {
                step++;
                trace.add(String.format("Step %d:", step));
                trace.add("  Remaining equations: " + workQueue);
                trace.add("  Current substitution: " + substitution);
                substitution = process(workQueue, substitution, trace);
            }
            trace.add("Unification completed successfully.");
            return UnifyResult.success(substitution, trace);
        } catch (ClashException e) {
            trace.add("Unification error at step " + step + ": " + e.getMessage());
            return UnifyResult.failure(trace);
        }
    }

    /**
     * Processes a single equation according to the unification rules:
     * Delete, Swap, Eliminate, Decompose.
     * @param work the queue of remaining equations
     * @param substitution the current substitution
     * @param trace the list accumulating trace messages
     * @return the updated substitution after processing this equation
     * @throws ClashException if a conflict or occurs-check failure occurs
     */
    private static Substitution process(Deque<Equation> work,
                                        Substitution substitution,
                                        List<String> trace) throws ClashException {
        Equation eq = work.pop();
        Term left  = substitution.apply(eq.left());
        Term right = substitution.apply(eq.right());

        trace.add(String.format("Processing equation: %s ≐ %s", left, right));

        if (isDelete(left, right)) {
            trace.add("  → DELETE (identical terms)");
            return substitution;
        } else if (isSwap(left, right)) {
            trace.add("  → SWAP (variable on right side)");
            work.push(new Equation(right, left));
            return substitution;
        } else if (isEliminate(left)) {
            trace.add(String.format("  → ELIMINATE: %s ↦ %s", left, right));
            return handleEliminate((Variable) left, right, substitution);
        } else if (left instanceof Function lf && right instanceof Function rf) {
            checkFunctionName(lf, rf);
            checkFunctionArity(lf, rf);
            trace.add("  → DECOMPOSE (decompose function arguments)");
            for (int i = 0; i < lf.getArity(); i++) {
                work.push(new Equation(
                        lf.getArgumentOnPosition(i),
                        rf.getArgumentOnPosition(i)
                ));
            }
            return substitution;
        } else {
            throw new ClashException(
                    String.format("Term conflict: cannot unify %s with %s", left, right)
            );
        }
    }

    /**
     * Handles the eliminate rule by performing an occurs-check and
     * extending the current substitution with the new binding.
     * @param variable the variable to bind
     * @param term the term to bind the variable to
     * @param substitution the current substitution
     * @return the extended substitution
     * @throws ClashException if the occurs-check fails
     */
    private static Substitution handleEliminate(Variable variable,
                                                Term term,
                                                Substitution substitution) throws ClashException {
        checkOccurrence(variable, term);
        return substitution.extend(variable, term);
    }

    /**
     * Checks that the given variable does not occur in the given term,
     * preventing circular substitutions.
     * @param variable the variable to check
     * @param term the term in which to search for the variable
     * @throws ClashException if the variable occurs in the term
     */
    private static void checkOccurrence(Variable variable, Term term) throws ClashException {
        if (term.getContainedVariables().contains(variable)) {
            throw new ClashException(
                    String.format("Occurs-check failed: variable '%s' occurs in term '%s'", variable, term)
            );
        }
    }

    private static boolean isDelete(Term left, Term right) {
        return left.equals(right);
    }

    private static boolean isSwap(Term left, Term right) {
        return !(left instanceof Variable) && right instanceof Variable;
    }

    private static boolean isEliminate(Term left) {
        return left instanceof Variable;
    }

    /**
     * Checks that two functions have matching names.
     * @param lf the left function
     * @param rf the right function
     * @throws ClashException if names differ
     */
    private static void checkFunctionName(Function lf,
                                          Function rf) throws ClashException {
        if (!lf.getName().equals(rf.getName())) {
            throw new ClashException(
                    String.format("Function name mismatch: '%s' vs '%s'", lf.getName(), rf.getName())
            );
        }
    }

    /**
     * Checks that two functions have matching arity.
     * @param lf the left function
     * @param rf the right function
     * @throws ClashException if arity differ
     */
    private static void checkFunctionArity(Function lf,
                                           Function rf) throws ClashException {
        if (lf.getArity() != rf.getArity()) {
            throw new ClashException(
                    String.format(
                            "Function arity mismatch: '%s' has %d arguments, '%s' has %d arguments",
                            lf.getName(), lf.getArity(), rf.getName(), rf.getArity()
                    )
            );
        }
    }
}