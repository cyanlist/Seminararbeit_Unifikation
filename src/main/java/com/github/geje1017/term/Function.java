package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a function application in a term, e.g., f(t1, t2, ...).
 */
public final class Function implements Term {

    private final String name;
    private final List<Term> arguments;

    /**
     * Constructs a function with the given name and arguments.
     * @param name the function name
     * @param arguments the argument terms
     */
    public Function(String name, Term... arguments) {
        this.name = name;
        this.arguments = List.of(arguments);
    }

    /**
     * Returns the function's name.
     * @return the function name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of arguments of this function.
     * @return the arity of the function
     */
    public int getArity() {
        return arguments.size();
    }

    /**
     * Returns the argument at the specified position.
     * @param index the zero-based index of the argument
     * @return the term at the given index
     */
    public Term getArgumentOnPosition(int index) {
        return arguments.get(index);
    }

    /**
     * Applies the substitution to each argument and returns a new Function.
     * @param substitution the substitution to apply
     * @return a new Function with substituted arguments
     */
    @Override
    public Term instantiate(Substitution substitution) {
        Term[] instantiated = arguments.stream()
                .map(arg -> arg.instantiate(substitution))
                .toArray(Term[]::new);
        return new Function(name, instantiated);
    }

    /**
     * Collects all variables contained in the argument terms.
     * @return a set of variables appearing in this function
     */
    @Override
    public Set<Variable> getContainedVariables() {
        return arguments.stream()
                .flatMap(arg -> arg.getContainedVariables().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Returns a string representation like f(t1,t2,...).
     * @return the string form of the function application
     */
    @Override
    public String toString() {
        String args = arguments.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "(", ")"));
        return name + args;
    }

    /**
     * Checks equality based on function name and argument list.
     * @param o the object to compare
     * @return true if o is a Function with the same name and arguments
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Function)) return false;
        Function other = (Function) o;
        return Objects.equals(name, other.name)
                && Objects.equals(arguments, other.arguments);
    }

    /**
     * Computes hash code based on name and arguments.
     * @return the hash code of the function
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }

    /**
     * Checks if this function is compatible with another (same name and arity).
     * @param other the function to compare
     * @return true if names and arities are identical
     */
    public boolean isCompatibleWith(Function other) {
        return name.equals(other.name)
                && arguments.size() == other.arguments.size();
    }
}
