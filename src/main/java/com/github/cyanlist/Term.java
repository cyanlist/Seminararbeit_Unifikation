package com.github.cyanlist;

import java.util.Map;

public abstract class Term {

    protected String name;

    public Term(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
