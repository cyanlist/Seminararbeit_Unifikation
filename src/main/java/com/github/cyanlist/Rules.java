package com.github.cyanlist;

public class Rules {

    public static Equation decompose(Function left, Function right) {
        if (!left.isCompatibleWith(right)) {
            throw new IllegalArgumentException(
                    String.format("Cannot decompose %s and %s; different symbols.", left, right)
            );
        }
        return new Equation(left.getFirstArgument(), right.getFirstArgument());
    }

}
