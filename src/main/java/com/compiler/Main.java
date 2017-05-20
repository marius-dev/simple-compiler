package com.compiler;

import com.compiler.compiler.CompilerComponent;
import com.compiler.parser.ParserComponent;
import com.compiler.scaner.ScannerComponent;

public class Main {
    public static void main(String [] args)
    {
        ScannerComponent scanner = new ScannerComponent("file.txt");
        ParserComponent parser = new ParserComponent(scanner);
        CompilerComponent compiler = new CompilerComponent(parser);

        compiler.compile();
    }
}
