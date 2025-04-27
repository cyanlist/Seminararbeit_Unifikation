package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;
import java.util.Set;
import java.util.Objects;

/**
 * Represents a variable in a term.
 * Variables are identified by a string name.
 */
public final class Variable implements Term {

    private final String name;

    /**
     * Constructs a new variable with the given name.
     * @param name the name of the variable
     */
    public Variable(String name) {
        this.name = name.toUpperCase();
    }

    /**
     * Applies the substitution by looking up this variable in the map.
     * Returns the bound term or this variable if unbound.
     * @param substitution the substitution to apply
     * @return the substituted term or this variable if no binding exists
     */
    @Override
    public Term instantiate(Substitution substitution) {
        return substitution.lookup(this);
    }

    /**
     * Returns a set containing only this variable.
     * @return a singleton set of this variable
     */
    @Override
    public Set<Variable> getContainedVariables() {
        return Set.of(this);
    }

    /**
     * Returns the name of this variable as its string representation.
     * @return the variable name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks equality based on variable name.
     * @param o the object to compare
     * @return true if o is a Variable with the same name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable other = (Variable) o;
        return Objects.equals(name, other.name);
    }

    /**
     * Computes hash code based on variable name.
     * @return the hash code of the name
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}