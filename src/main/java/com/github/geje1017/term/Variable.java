package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;

import java.util.*;

// TODO: Doc-Kommentare hinzufügen
// TODO: Abstrakte Klasse hinzufügen
public final class Variable implements Term {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override public Term instantiatedWith(Substitution substitution) {
        return substitution.getOrDefault(this, this);
    }

    @Override public Set<Variable> getContainedVariables() {
        return Set.of(this);
    }

    @Override public String toString() {
        return name;
    }

    @Override public boolean equals(Object o){
        return o instanceof Variable v &&
                name.equals(v.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }
}
