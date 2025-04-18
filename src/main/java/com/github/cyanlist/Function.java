package com.github.cyanlist;

import java.util.*;
import java.util.stream.Collectors;

public class Function extends Term{

    private final List<Term> arguments;

    public Function(String name, Term argument) {
        super(name);
        this.arguments = Collections.singletonList(argument);
    }

    public Function(String name, Term firstArgument, Term secondArgument) {
        super(name);
        this.arguments = Arrays.asList(firstArgument, secondArgument);
    }

    public Term getFirstArgument() {
        return this.arguments.get(0);
    }

    public Optional<Term> getSecondArgument() {
        return this.getArity() == 2 ?
                Optional.of(this.arguments.get(1)) :
                Optional.empty();
    }

    public List<Term> getArguments() {
        return this.arguments;
    }

    public int getArity() {
        return this.getArguments().size();
    }

    public boolean isCompatibleWith(Function other) {
        return this.getName().equals(other.getName()) &&
                this.getArity() == other.getArity();
    }

    @Override
    public String toString() {
        return this.getName() + this.getArguments().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "(", ")"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Function other)) return false;
        return this.getName().equals(other.getName()) &&
                this.getArguments().equals(other.getArguments());
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(this.getName(), this.getArguments());
//    }

}

