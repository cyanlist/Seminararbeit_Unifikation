package com.github.cyanlist;

public class Main {
    public static void main(String[] args) {

        Constant test1 = new Constant("a");
        Variable test2 = new Variable("a");

        Function test3 = new Function("f", test2);
        Function test7 = new Function("f", test1, test3);

        Substitution test4 = new Substitution();
        test4.addEquation(test1, test2);
        test4.addEquation(test2, test3);
        test4.addEquation(test1, test3);

        Equation test5 = new Equation(test3, test3);
        Equation test6 = Rules.decompose(test3, test3);

        System.out.println(test1);
        System.out.println(test2);
        System.out.println(test3);
        System.out.println(test7);

        System.out.println(test4);

        System.out.println(test5);
        System.out.println(test6);
    }
}