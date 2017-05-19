package model;

/**
 * Created by marius.iliescu on 5/19/2017.
 */
public class Node {
    public String name;
    public String type;
    public String value;

    public Node left;
    public Node right;
    public Node up;
    public Node down;

    public int level;


    public Node(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
