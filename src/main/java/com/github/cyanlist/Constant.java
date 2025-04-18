package com.github.cyanlist;

import java.util.*;

public final class Constant implements Term {
    private final String symbol;
    public Constant(String symbol) { this.symbol = symbol; }

    @Override public Term apply(Substitution σ) { return this; } // Konstanten bleiben konstant
    @Override public Set<Variable> vars() { return Set.of(); }

    @Override public String toString() { return symbol; }
    @Override public boolean equals(Object o){ return o instanceof Constant c && symbol.equals(c.symbol); }
    @Override public int hashCode() { return Objects.hash(symbol); }
}