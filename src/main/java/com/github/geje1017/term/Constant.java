package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;
import java.util.Set;
import java.util.Objects;

/**
 * Represents a constant symbol in a term.
 * Constants have no variables and are unaffected by substitutions.
 */
public final class Constant implements Term {

    private final String symbol;

    /**
     * Constructs a constant with the given symbol.
     * @param symbol the constant symbol
     */
    public Constant(String symbol) {
        this.symbol = symbol.toLowerCase();
    }

    /**
     * Returns this constant unchanged because substitutions do not affect constants.
     * @param substitution the substitution (ignored)
     * @return this constant
     */
    @Override
    public Term instantiate(Substitution substitution) {
        return this;
    }

    /**
     * Returns an empty set because constants contain no variables.
     * @return an empty set
     */
    @Override
    public Set<Variable> getContainedVariables() {
        return Set.of();
    }

    /**
     * Returns the symbol of this constant.
     * @return the constant symbol
     */
    @Override
    public String toString() {
        return symbol;
    }

    /**
     * Checks equality based on the constant symbol.
     * @param o the object to compare
     * @return true if o is a Constant with the same symbol
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constant)) return false;
        Constant other = (Constant) o;
        return Objects.equals(symbol, other.symbol);
    }

    /**
     * Computes hash code based on the constant symbol.
     * @return the hash code of the symbol
     */
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}