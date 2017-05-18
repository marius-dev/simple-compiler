import compiler.CompilerComponent;
import parser.ParserComponent;
import scaner.ScannerComponent;

public class Main {
    public static void main(String [] args)
    {
        ScannerComponent scanner = new ScannerComponent("file.txt");
        ParserComponent parser = new ParserComponent(scanner);
        CompilerComponent compiler = new CompilerComponent(parser);

        compiler.compile();
    }
}
