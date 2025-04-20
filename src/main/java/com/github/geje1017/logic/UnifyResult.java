package com.github.geje1017.logic;

import java.util.*;

/**
 * Repräsentiert das Ergebnis der Unifikation,
 * inkl. einer Liste von Schritten (Trace).
 */
public class UnifyResult {

    private final boolean success;
    private final Substitution substitution;
    private final List<String> trace;

    /**
     * Privater Konstruktor.
     */
    private UnifyResult(boolean success,
                        Substitution substitution,
                        List<String> trace) {
        this.success = success;
        this.substitution = substitution;
        // Unveränderliche Kopie, damit der Trace nicht nachträglich modifiziert wird
        this.trace = Collections.unmodifiableList(new ArrayList<>(trace));
    }

    /**
     * Factory‑Methode für einen erfolgreichen Unifier‑Result.
     *
     * @param substitution die gefundene Substitution
     * @param trace        die Liste der angewendeten Schritte
     */
    public static UnifyResult success(Substitution substitution,
                                      List<String> trace) {
        return new UnifyResult(true, substitution, trace);
    }

    /**
     * Factory‑Methode für einen fehlgeschlagenen Unifier‑Result.
     *
     * @param trace die Liste der bis zum Clash aufgezeichneten Schritte
     */
    public static UnifyResult failure(List<String> trace) {
        // Im Fehlerfall geben wir eine leere Substitution zurück
        return new UnifyResult(false, new Substitution(), trace);
    }

    /** @return true, wenn unifizierbar */
    public boolean isSuccess() {
        return success;
    }

    /** @return die ermittelte Substitution (oder leer bei Fehler) */
    public Substitution getSubstitution() {
        return substitution;
    }

    /** @return den Schritt‑für‑Schritt‑Trace */
    public List<String> getTrace() {
        return trace;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UnifyResult: ").append(success ? "SUCCESS" : "FAILURE").append("\n");
        sb.append("Substitution:\n");
        sb.append("  ").append(substitution).append("\n");
        sb.append("Trace:\n");
        for (String step : trace) {
            sb.append("  - ").append(step).append("\n");
        }
        return sb.toString();
    }
}
