package com.github.geje1017.logic;

import java.util.List;

/**
 * Encapsulates the result of a unification operation,
 * including whether it succeeded, the resulting substitution,
 * and the detailed trace of steps.
 */
public class UnifyResult {

    private final boolean success;
    private final Substitution substitution;
    private final List<String> trace;

    /**
     * Private constructor used by factory methods.
     * @param success whether unification succeeded
     * @param substitution the computed substitution (or empty on failure)
     * @param trace the trace of applied unification steps
     */
    private UnifyResult(boolean success, Substitution substitution, List<String> trace) {
        this.success = success;
        this.substitution = substitution;
        this.trace = List.copyOf(trace);
    }

    /**
     * Creates a UnifyResult for a successful unification.
     * @param substitution the computed substitution (MGU)
     * @param trace the trace of applied steps
     * @return a successful UnifyResult
     */
    public static UnifyResult success(Substitution substitution, List<String> trace) {
        return new UnifyResult(true, substitution, trace);
    }

    /**
     * Creates a UnifyResult for a failed unification.
     * @param trace the trace recorded until failure
     * @return a failed UnifyResult
     */
    public static UnifyResult failure(List<String> trace) {
        return new UnifyResult(false, new Substitution(), trace);
    }

    /**
     * Checks if unification was successful.
     * @return true if unification succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the substitution computed by unification.
     * @return the substitution (or empty on failure)
     */
    public Substitution getSubstitution() {
        return substitution;
    }

    /**
     * Returns the detailed trace of unification steps.
     * @return an unmodifiable list of trace messages
     */
    public List<String> getTrace() {
        return trace;
    }

    /**
     * Returns a formatted string summarizing the result,
     * including success status, substitution, and trace.
     * @return the string representation of this result
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Unification ")
                .append(success ? "SUCCEEDED" : "FAILED")
                .append("\n")
                .append("Substitution: ")
                .append(substitution)
                .append("\nTrace:\n");
        for (String stepMsg : trace) {
            sb.append("  - ").append(stepMsg).append("\n");
        }
        return sb.toString();
    }
}