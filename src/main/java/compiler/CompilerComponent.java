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
        parser.parse();
        if(parser.getErrorMessages().isEmpty()){

        }else{
            for (String message: parser.getErrorMessages()) {
                System.out.println(message);
            }
        }
    }
}
