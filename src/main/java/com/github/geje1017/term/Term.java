package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;
import java.util.Set;

/**
 * Represents a term in the term algebra. A term can be a variable,
 * a constant, or a function application.
 */
public sealed interface Term permits Variable, Constant, Function {

    /**
     * Applies the given substitution to this term, returning a new term
     * with all variables replaced according to the substitution.
     * @param substitution the substitution to apply
     * @return the term resulting from applying the substitution
     */
    Term instantiate(Substitution substitution);

    /**
     * Returns the set of variables contained in this term.
     * @return an unmodifiable set of variables present in this term
     */
    Set<Variable> getContainedVariables();
}