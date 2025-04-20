package com.github.geje1017.term;

// TODO: Doc-Kommentare hinzufügen
public record Equation(Term left, Term right) {

    @Override
    public String toString() {
        return this.left() + " = " + this.right();
    }

}