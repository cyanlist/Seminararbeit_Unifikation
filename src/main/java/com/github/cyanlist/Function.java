package com.github.cyanlist;

import java.util.*;
import java.util.stream.*;

public final class Function implements Term {
    final String functor;
    private final List<Term> args;

    public Function(String functor, Term... args){
        this.functor = functor;
        this.args    = List.of(args); // immutable copy
    }

    /** Liefert den Namen des Funktors (z. B. "f", "+", "succ"). */
    public String functor() {
        return functor;
    }

    @Override public Term apply(Substitution σ) {
        return new Function(functor,
                args.stream().map(t -> t.apply(σ)).toArray(Term[]::new));
    }

    @Override public Set<Variable> vars() {
        return args.stream().flatMap(t -> t.vars().stream()).collect(Collectors.toSet());
    }

    @Override public String toString() {
        return functor + args.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "(", ")"));
    }

    @Override public boolean equals(Object o){
        return o instanceof Function f &&
                functor.equals(f.functor) &&
                args.equals(f.args);
    }
    @Override public int hashCode(){ return Objects.hash(functor, args); }

    /* Convenience */
    public int arity() { return args.size(); }
    public Term arg(int i){ return args.get(i); }
}

