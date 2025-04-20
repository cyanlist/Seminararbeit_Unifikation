package com.github.geje1017.term;

import com.github.geje1017.logic.Substitution;

import java.util.Set;

// TODO: Doc-Kommentare hinzufügen
public sealed interface Term permits Variable, Constant, Function {

    Term instantiatedWith(Substitution substitution);

    Set<Variable> getContainedVariables();
}