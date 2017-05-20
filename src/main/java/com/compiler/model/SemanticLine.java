package com.compiler.model;

/**
 * Created by marius.iliescu on 5/19/2017.
 */
public class SemanticLine {
    public String name;
    public String dataType;
    public String size;

    public SemanticLine() {
    }

    public SemanticLine(SemanticLine line) {
        this.name = line.name;
        this.dataType = line.dataType;
        this.size = line.size;
    }
}
