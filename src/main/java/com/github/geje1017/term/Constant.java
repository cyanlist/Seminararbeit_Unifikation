package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;

import java.util.*;

// TODO: Doc-Kommentare hinzufügen
// TODO: Abstrakte Klasse hinzufügen
public final class Constant implements Term {

    private final String symbol;

    public Constant(String symbol) { this.symbol = symbol; }

    @Override public Term instantiatedWith(Substitution σ) { return this; }
    @Override public Set<Variable> getContainedVariables() { return Set.of(); }

    @Override public String toString() { return symbol; }
    @Override public boolean equals(Object o){ return o instanceof Constant c && symbol.equals(c.symbol); }
    @Override public int hashCode() { return Objects.hash(symbol); }
}