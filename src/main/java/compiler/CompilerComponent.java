package compiler;

import model.Triple;
import parser.ParserComponent;

import java.io.IOException;

public class CompilerComponent {
    private ParserComponent parser;

    public CompilerComponent(ParserComponent parser) {
        this.parser = parser;
    }

    public void compile(){
        try {
            parser.parse();
            if(!parser.hasErrors()) {
                System.out.println("index - [ operand, argument1, argument2 ]\n");
                for (Triple t : parser.getTriples()) {
                    System.out.println(" - [ " + t.getOp() + ", " + t.getArg1() + ",  " + t.getArg2() + " ]\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
