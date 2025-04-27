package com.github.geje1017.logic;

import com.github.geje1017.term.Term;
import com.github.geje1017.term.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents an immutable substitution mapping variables to terms (σ: X ↦ t).
 * All modification methods return new instances.
 * Occurs-check is performed externally in the Unifier.
 */
public final class Substitution {

    private final Map<Variable, Term> map;

    /**
     * Constructs an empty substitution.
     */
    public Substitution() {
        this.map = Map.of();
    }

    /**
     * Constructs a substitution with the given map of bindings.
     * @param map a map of variable-to-term bindings
     */
    private Substitution(Map<Variable, Term> map) {
        this.map = Map.copyOf(map);
    }

    /**
     * Applies this substitution to the provided term.
     * @param term the term to which the substitution is applied
     * @return the instantiated term after applying this substitution
     */
    public Term apply(Term term) {
        return term.instantiate(this);
    }

    /**
     * Retrieves the term bound to the specified variable, or returns the variable
     * itself if it is not bound in this substitution.
     * @param var the variable to look up
     * @return the bound term, or the variable if unbound
     */
    public Term lookup(Variable var) {
        return map.getOrDefault(var, var);
    }

    /**
     * Returns a new substitution extended by binding the given variable to the given term.
     * @param v the variable to bind
     * @param t the term to bind the variable to
     * @return a new Substitution containing all previous bindings plus v ↦ t
     */
    public Substitution extend(Variable v, Term t) {
        Substitution sigma = new Substitution(Map.of(v, t));
        return sigma.compose(this);
    }

    /**
     * Composes this substitution with another substitution τ, returning (σ ∘ τ).
     * The result applied to a variable x is σ(τ(x)).
     * @param other the substitution τ to apply first
     * @return a new Substitution representing σ ∘ τ
     */
    public Substitution compose(Substitution other) {
        Map<Variable, Term> result = new HashMap<>();
        other.map.forEach((v, t) -> result.put(v, t.instantiate(this)));
        this.map.forEach(result::putIfAbsent);
        return new Substitution(result);
    }

    /**
     * Returns an unmodifiable string representation of this substitution,
     * replacing '=' with '↦'.
     * @return the string representation of the substitution
     */
    @Override
    public String toString() {
        return map.toString().replace("=", "↦");
    }

    /**
     * Returns a set of entries (variable ↦ term) contained in this substitution.
     * @return an unmodifiable set of the substitution's entries
     */
    public Set<Map.Entry<Variable, Term>> entrySet() {
        return Map.copyOf(map).entrySet();
    }
}