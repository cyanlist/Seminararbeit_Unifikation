package com.github.cyanlist;

import java.util.*;

public final class Variable implements Term {
    private final String name;
    public Variable(String name) { this.name = name; }

    @Override public Term apply(Substitution σ) {
        return σ.getOrDefault(this, this);
    }

    @Override public Set<Variable> vars() { return Set.of(this); }

    @Override public String toString() { return name; }
    @Override public boolean equals(Object o){ return o instanceof Variable v && name.equals(v.name); }
    @Override public int hashCode() { return Objects.hash(name); }
}
