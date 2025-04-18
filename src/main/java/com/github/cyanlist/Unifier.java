package com.github.cyanlist;

import java.util.*;
import java.util.stream.*;

/* ---------- 4. Der eigentliche Unifikator ---------- */
public final class Unifier {

    public static Optional<Substitution> unify(Collection<Equation> equations){
        Deque<Equation> γ = new ArrayDeque<>(equations);
        Substitution σ   = new Substitution();

        while (!γ.isEmpty()) {
            Equation e = γ.pop();
            Term s = e.left().apply(σ);
            Term t = e.right().apply(σ);

            /* 1. Delete */
            if (s.equals(t)) continue;

            /* 2. Orient */
            if (!(s instanceof Variable) && t instanceof Variable) {
                γ.push(new Equation(t, s));
                continue;
            }

            /* 3. Eliminate */
            if (s instanceof Variable X) {
                if (t.vars().contains(X)) return Optional.empty(); // Occurs‑Check

                σ.put(X, t);  // X ↦ t in die Substitution

                /* γ := γσ  (auf alle Restgleichungen anwenden) */
                Substitution single = new Substitution();
                single.put(X, t);

                Deque<Equation> tmp = new ArrayDeque<>();
                for (Equation eq : γ) {
                    tmp.add(new Equation(eq.left().apply(single),
                            eq.right().apply(single)));
                }
                γ = tmp;
                continue;
            }

            /* 4. Decompose */
            if (s instanceof Function f && t instanceof Function g &&
                    f.functor().equals(g.functor()) && f.arity() == g.arity()) {

                for (int i = 0; i < f.arity(); i++) {
                    γ.push(new Equation(f.arg(i), g.arg(i)));
                }
                continue;
            }

            /* 5. Clash */
            return Optional.empty();
        }
        return Optional.of(σ);
    }
}
