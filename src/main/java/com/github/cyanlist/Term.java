package com.github.cyanlist;

import java.util.Set;

public sealed interface Term permits Variable, Constant, Function {

    /** Wendet die Substitution σ auf diesen Term an und liefert einen neuen Term zurück. */
    Term apply(Substitution σ);

    /** Liefert alle Variablen, die in diesem Term vorkommen (für Occurs‑Check). */
    Set<Variable> vars();
}