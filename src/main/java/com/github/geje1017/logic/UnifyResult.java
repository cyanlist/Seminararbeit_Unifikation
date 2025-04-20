package com.github.geje1017.logic;

public class UnifyResult {

    /**
     * Ergebnis des Unifikationsversuchs.
     *
     *  success  – true  ⇒ MGU vorhanden, false ⇒ kein Unifikator
     *  mgu      – die gefundenen Ersetzungen (null, falls success == false)
     */


        private final boolean success;
        private final Substitution mgu;

        private UnifyResult(boolean success, Substitution mgu) {
            this.success = success;
            this.mgu     = mgu;
        }

        public static UnifyResult success(Substitution σ) {
            return new UnifyResult(true, σ);
        }

        public static UnifyResult failure() {
            return new UnifyResult(false, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public Substitution mgu() {
            return mgu;
        }

        @Override
        public String toString() {
            return success ? "Success: " + mgu : "Failure";
        }



}
