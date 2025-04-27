package com.github.geje1017.term;

/**
 * Represents an equation between two terms, left = right.
 */
public record Equation(Term left, Term right) {

    /**
     * Returns a human-readable form "left = right".
     * @return the string representation of the equation
     */
    @Override
    public String toString() {
        return left + " = " + right;
    }
}