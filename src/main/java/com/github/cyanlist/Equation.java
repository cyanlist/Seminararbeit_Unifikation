package com.github.cyanlist;

public class Equation {

    private final Term left;
    private final Term right;

    public Equation(Term left, Term right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "Equation{" + "left=" + this.getLeft() + ", right=" + this.getRight() + '}';
    }

    // Getter- and setter-Methods
    public Term getLeft() { return left; }
    public Term getRight() { return right; }
}
