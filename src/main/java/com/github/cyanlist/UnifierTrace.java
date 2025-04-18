package com.github.cyanlist;

import java.util.*;
import java.util.stream.Collectors;

/* ------------------------------------------------------------
 * UnifierTrace  – Martelli/Montanari mit Schrittprotokoll
 * ------------------------------------------------------------
 */
public final class UnifierTrace {

    /** Ergebniscontainer (Substitution + Step‑Log). */
    public record Result(Optional<Substitution> mgu,
                         List<String> steps) {}

    /** Öffentliche API: unify … und logge alle Schritte. */
    public static Result unifyTrace(Collection<Equation> startEqns) {

        Deque<Equation> γ = new ArrayDeque<>(startEqns);   // Arbeitsliste
        Substitution σ   = new Substitution();             // aktuelle Subst.
        List<String> log = new ArrayList<>();              // Schrittprotokoll

        int step = 0;                                      // Zähler

        /* Hilfsfunktion: momentane Lage hübsch loggen */
        int finalStep = step;
        Deque<Equation> finalΓ = γ;
        Runnable dump = () -> log.add(
                "%02d: γ = %s,  σ = %s"
                        .formatted(finalStep, finalΓ, σ));

        dump.run();                                        // Initialzustand

        while (!γ.isEmpty()) {
            Equation e = γ.pop();
            Term s = e.left().apply(σ);
            Term t = e.right().apply(σ);

            /* 1. DELETE -------------------------------------------------- */
            if (s.equals(t)) {
                step++; log.add("-- Delete  (%s = %s)".formatted(s, t));
                dump.run();
                continue;
            }

            /* 2. ORIENT -------------------------------------------------- */
            if (!(s instanceof Variable) && t instanceof Variable) {
                step++; log.add("-- Orient  (%s ↔ %s)".formatted(s, t));
                γ.push(new Equation(t, s));
                dump.run();
                continue;
            }

            /* 3. ELIMINATE ---------------------------------------------- */
            if (s instanceof Variable X) {
                if (t.vars().contains(X)) {
                    step++; log.add("-- Occurs‑Check FAIL  (%s ∈ %s)".formatted(X, t));
                    return new Result(Optional.empty(), log);
                }
                step++; log.add("-- Eliminate  (%s ↦ %s)".formatted(X, t));

                σ.put(X, t);                         // Subst. erweitern

                /* γσ anwenden */
                Substitution single = new Substitution();
                single.put(X, t);
                γ = γ.stream()
                        .map(eq -> new Equation(eq.left().apply(single),
                                eq.right().apply(single)))
                        .collect(Collectors.toCollection(ArrayDeque::new));

                dump.run();
                continue;
            }

            /* 4. DECOMPOSE ---------------------------------------------- */
            if (s instanceof Function f && t instanceof Function g &&
                    f.functor().equals(g.functor()) && f.arity() == g.arity()) {

                step++; log.add("-- Decompose  %s  vs  %s".formatted(f, g));
                for (int i = 0; i < f.arity(); i++)
                    γ.push(new Equation(f.arg(i), g.arg(i)));
                dump.run();
                continue;
            }

            /* 5. CLASH --------------------------------------------------- */
            step++; log.add("-- Clash  (%s  vs  %s)".formatted(s, t));
            return new Result(Optional.empty(), log);
        }
        step++; log.add("-- SUCCESS");
        return new Result(Optional.of(σ), log);
    }
}

