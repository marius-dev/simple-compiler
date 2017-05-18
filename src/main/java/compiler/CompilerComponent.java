package compiler;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
