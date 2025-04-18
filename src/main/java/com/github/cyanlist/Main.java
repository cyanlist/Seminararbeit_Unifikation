package com.github.cyanlist;

import java.util.*;
import java.util.stream.*;

import static com.github.cyanlist.Unifier.unify;

public class Main {
    public static void main(String[] args) {
        Variable X = new Variable("X");
        Variable Y = new Variable("Y");

        Term t1 = new Function("f", X, new Constant("a"));
        Term t2 = new Function("f", new Constant("b"), Y);

        UnifierTrace.Result res = UnifierTrace.unifyTrace(
                List.of(new Equation(t1, t2)));

        res.steps().forEach(System.out::println);
        System.out.println("MGU: " + res.mgu());
    }
}