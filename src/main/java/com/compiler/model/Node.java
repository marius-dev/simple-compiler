package com.compiler.model;

import com.fasterxml.jackson.annotation.*;

/**
 * Created by marius.iliescu on 5/19/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Node {
    public NodeData data;

    @JsonBackReference
    public Node left;
    @JsonBackReference
    public Node right;

    @JsonIgnore
    public Node up;

    public Node down;

    public int level;

    @JsonCreator
    public Node(@JsonProperty("data")  NodeData data) {
        this.data = data;
    }
}
