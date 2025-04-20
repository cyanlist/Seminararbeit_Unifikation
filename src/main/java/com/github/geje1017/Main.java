package com.github.geje1017;

import com.github.geje1017.logic.Unifier;
import com.github.geje1017.logic.UnifierTrace;
import com.github.geje1017.term.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Variable X = new Variable("X");
        Variable Y = new Variable("Y");

        Term t3 = new Function("g", X);

        Term t1 = new Function("f", t3, X);
        Term t2 = new Function("f", Y, new Constant("a"));

        System.out.println(t1 + ", " + t2);
        System.out.println(Unifier.unify(List.of(new Equation(t1, t2))));
    }
}