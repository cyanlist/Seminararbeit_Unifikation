package com.github.geje1017.logic;

import com.github.geje1017.term.Term;
import com.github.geje1017.term.Variable;

import java.util.*;

// TODO: Doc-Kommentare hinzufügen
// TODO: Wird das überhaupt benötigt?
/**
 * Domain‑specific map X↦t  (logical substitution).
 * Immutable view; all mutating methods delegate to the internal map.
 */
public final class Substitution {

    private final Map<Variable, Term> map;

    public Substitution() {
        this.map = new HashMap<>();
    }

    /* ---------- domain helpers ---------- */

    /** σ ○ τ  right‑to‑left composition. */
    public static Substitution compose(Substitution sigma, Substitution tau) {
        Substitution result = new Substitution();
        tau.map.forEach((v, t) -> result.map.put(v, t.instantiatedWith(sigma)));
        sigma.map.forEach(result.map::putIfAbsent);
        return result;
    }

    public Term getOrDefault(Variable v, Term d) { return map.getOrDefault(v, d); }
    public void put(Variable v, Term t) { map.put(v, t); }
    public boolean containsKey(Variable v) { return map.containsKey(v); }
    public boolean isEmpty() { return map.isEmpty(); }

    public Set<Map.Entry<Variable,Term>> entrySet() { return Map.copyOf(map).entrySet(); }

    @Override public String toString() {
        return map.toString().replace("=", "↦");
    }
}
