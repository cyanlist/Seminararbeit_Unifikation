package com.github.geje1017.logic;

import com.github.geje1017.term.Equation;
import com.github.geje1017.term.Function;
import com.github.geje1017.term.Term;
import com.github.geje1017.term.Variable;

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

    /** unify und logge alle Schritte. */
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
            Term s = e.left().instantiatedWith(σ);
            Term t = e.right().instantiatedWith(σ);

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
                if (t.getContainedVariables().contains(X)) {
                    step++; log.add("-- Occurs‑Check FAIL  (%s ∈ %s)".formatted(X, t));
                    return new Result(Optional.empty(), log);
                }
                step++; log.add("-- Eliminate  (%s ↦ %s)".formatted(X, t));

                σ.put(X, t);                         // Subst. erweitern

                /* γσ anwenden */
                Substitution single = new Substitution();
                single.put(X, t);
                γ = γ.stream()
                        .map(eq -> new Equation(eq.left().instantiatedWith(single),
                                eq.right().instantiatedWith(single)))
                        .collect(Collectors.toCollection(ArrayDeque::new));

                dump.run();
                continue;
            }

            /* 4. DECOMPOSE ---------------------------------------------- */
            if (s instanceof Function f && t instanceof Function g &&
                    f.getName().equals(g.getName()) && f.getArity() == g.getArity()) {

                step++; log.add("-- Decompose  %s  vs  %s".formatted(f, g));
                for (int i = 0; i < f.getArity(); i++)
                    γ.push(new Equation(f.getArgumentOnPosition(i), g.getArgumentOnPosition(i)));
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

