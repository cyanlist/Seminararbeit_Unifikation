package com.github.cyanlist;

import java.util.HashMap;

public final class Substitution extends HashMap<Variable, Term> {

    /** σ ◦ τ –­ Komposition zweier Substitutionen (σ zuerst, dann τ). */
    public static Substitution compose(Substitution σ, Substitution τ){
        Substitution result = new Substitution();

        /* Alle Abbildungen aus τ, aber nach σ angewandt */
        τ.forEach((v,t) -> result.put(v, t.apply(σ)));

        /* plus alles aus σ, das noch nicht in τ ersetzt wurde */
        σ.forEach(result::putIfAbsent);
        return result;
    }
}
