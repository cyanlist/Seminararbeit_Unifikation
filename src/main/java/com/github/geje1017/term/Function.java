package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;

import java.util.*;
import java.util.stream.*;

// TODO: Doc-Kommentare hinzufügen
// TODO: Abstrakte Klasse hinzufügen
public final class Function implements Term {

    // TODO: Sichern, dass die Funktion nur max. zwei Parameter aufnehmen kann
    private final int MAX_ARITY = 2;

    final String name;
    private final List<Term> arguments;

    public Function(String name, Term... arguments){
        this.name = name;
        this.arguments = List.of(arguments);
    }

    public String getName() {
        return this.name;
    }

    private List<Term> getArguments() {
        return this.arguments;
    }

    @Override public Term instantiatedWith(Substitution substitution) {
        return new Function(this.getName(),
                this.getArguments()
                        .stream()
                        .map(t -> t.instantiatedWith(substitution)).toArray(Term[]::new));
    }

    @Override public Set<Variable> getContainedVariables() {
        return this.getArguments()
                .stream()
                .flatMap(t -> t.getContainedVariables().stream()).collect(Collectors.toSet());
    }

    @Override public String toString() {
        return this.getName() + this.getArguments().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "(", ")"));
    }

    @Override public boolean equals(Object o){
        return o instanceof Function f &&
                this.getName().equals(f.getName()) &&
                this.getArguments().equals(f.getArguments());
    }
    @Override public int hashCode(){
        return Objects.hash(this.getName(), this.getArguments());
    }

    public int getArity() {
        return this.getArguments().size();
    }

    public Term getArgumentOnPosition(int i){
        return this.getArguments().get(i);
    }

    public boolean isCompatibleWith(Function other) {
        return this.getName().equals(other.getName()) &&
                this.getArity() == other.getArity();
    }
}

