package parser;

import model.Token;
import model.TokenType;
import model.Triple;
import scaner.ScannerComponent;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class ParserComponent {

    private ScannerComponent scanner;
    private Token currentToken;
    private ArrayList<Triple> triples;

    public ParserComponent(ScannerComponent scanner) {
        this.scanner = scanner;
        this.triples = new ArrayList<>();
    }

    public String parse() throws IOException {
        String begin = "";

        currentToken = scanner.getNextToken();

        if (currentToken.getType() == TokenType.BEGIN) {
            currentToken = scanner.getNextToken();
            begin = instructionList();

            if (currentToken.getType() != TokenType.END) {
                this.displayError(3);
            }
        } else {
            this.displayError(2);
        }
        begin = GenTrip("begin", addParentheses(begin), "end");

        Token t1 = currentToken;
        currentToken = scanner.getNextToken();

        if (t1.getType() == TokenType.END && currentToken.getType() != TokenType.EOF) {
            currentToken = scanner.getNextToken();
            if (currentToken.getType() != TokenType.EOF) {
                this.displayError(4);
            }
        }
        return begin;
    }

    //definirea unei liste de instructiuni
    // lista_instructiuni -> instruction || ';'
    private String instructionList() throws IOException {
        String begin = instruction();

        while (currentToken.getType() == TokenType.SEMI && currentToken.getType() != TokenType.END) {
            currentToken = scanner.getNextToken();
            begin = instruction();
        }

        if(currentToken.getType() != TokenType.SEMI  && currentToken.getType() != TokenType.END ){
            this.displayError(10);
            this.scanner.goToPosition(this.scanner.getLastTokenPosition());
            this.currentToken = scanner.getNextToken();
        }

        return begin;
    }

    //definirea unei instructiuni
    /* instruction -> 'if' expression 'then' instruction |
     * 'if' expression 'then' instruction 'else' instruction |
     * 'v' '=' expression.
    */
    private String instruction() throws IOException {

        String temp1 = "", temp3, temp2, tempElse, backUp;

        if (currentToken.getType() == TokenType.IF) {
            currentToken = scanner.getNextToken();
            temp1 = expression();

            backUp = temp1;
            if (currentToken.getType() == TokenType.THEN) {
                currentToken = scanner.getNextToken();
            } else {
                this.displayError(5);
                this.scanner.goToPosition(this.scanner.getLastTokenPosition());
                this.currentToken = scanner.getNextToken();
            }

            temp2 = instruction();
            temp1 = GenTrip("if", addParentheses(temp1), addParentheses(temp2));
            if (currentToken.getType() == TokenType.ELSE) {
                currentToken = scanner.getNextToken();
                tempElse = instruction();
                temp1 = GenTrip("if", addParentheses(backUp), addParentheses(tempElse));
            }
        } else if (currentToken.getType() == TokenType.ID) {
            String identificator = currentToken.getValue();
            currentToken = scanner.getNextToken();

            if (currentToken.getType() != TokenType.EQUAL) {
                this.displayError(6);
                this.scanner.goToPosition(this.scanner.getLastTokenPosition());
                this.currentToken = scanner.getNextToken();
            }

            currentToken = scanner.getNextToken();
            temp3 = expression();

            temp1 = GenTrip("=", identificator, addParentheses(temp3));

        } else {
            this.displayError(7);
        }
        return temp1;
    }

    //definirea unei expresii
    // expression -> term || '+'
    private String expression() throws IOException {
        String t1, t2;

        t1 = term();

        currentToken = scanner.getNextToken();
//todocheck substr
        while (currentToken.getType() == TokenType.PLUS) {
            currentToken = scanner.getNextToken();
            t2 = term();
            if (t1.substring(0, 4).equals("Constanta ") && (t2.substring(0, 4).equals("Constanta "))) {
                t1 = GenTrip("+", t1.substring(4, t1.length() - 4), t2.substring(4, t1.length() - 4));
            } else if (t1.substring(0, 4).equals("Constanta ")) {
                t1 = GenTrip("+", t1.substring(4, t1.length() - 4), addParentheses(t2));
            } else if (t2.substring(0, 4).equals("Constanta ")) {
                t1 = GenTrip("+", addParentheses(t1), t2.substring(4, t1.length() - 4));
            } else {
                t1 = GenTrip("+", addParentheses(t1), addParentheses(t2));
            }
            currentToken = scanner.getNextToken();
        }

        return t1;
    }


    //definirea unui term
    // term -> 'c' | '(' expression ')'
    private String term() throws IOException {
        String t1 = "Constanta ";

        if (
                (currentToken.getType() == TokenType.REAL) ||
                        (currentToken.getType() == TokenType.SIR) ||
                        (currentToken.getType() == TokenType.INT) ||
                        (currentToken.getType() == TokenType.OCT) ||
                        (currentToken.getType() == TokenType.HEX) ||
                        (currentToken.getType() == TokenType.CH_CON) ||
                        (currentToken.getType() == TokenType.BIN)) {
            t1 += currentToken.getValue();
        } else if (currentToken.getType() == TokenType.BAD) {
            this.displayError(8);
        } else if (currentToken.getType() == TokenType.L_PAREN) {
            currentToken = scanner.getNextToken();

            t1 = expression();

            if (currentToken.getType() != TokenType.R_PAREN) {
                this.displayError(9);
                this.scanner.goToPosition(this.scanner.getLastTokenPosition());
                this.currentToken = scanner.getNextToken();
            }
        }

        return t1;
    }

    //generarea efectiva a unui triplet
    private String GenTrip(String cod_op, String cod_arg1, String cod_arg2) {
        Triple t = new Triple(cod_op, cod_arg1, cod_arg2);
        this.triples.add(t);

        return "" + this.triples.size();
    }

    //converteste intregul 'i' in sirul "(i)"
    private String addParentheses(String i) {
        return "(" + i + ")";
    }


    private void displayError(int errorCode) {
        String message = "";
        switch (errorCode) {
            case 1:
                message = "Nu se poate deschide fisierul!";
                break;
            case 2:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste \"begin\" !";
                break;
            case 3:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste \"end\" !";
                break;
            case 4:
                message = "Eroare la linia " + this.currentToken.getLine() + ": atomul \"" + this.currentToken.getValue() + "\" este ilegal !";
                break;
            case 5:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste \"then\" !";
                break;
            case 6:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste \"=\" !";
                break;
            case 7:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste \"if\" sau un nume de variabila !";
                break;
            case 8:
                message = "Eroare la linia " + this.currentToken.getLine() + ": numarul \"" + this.currentToken.getValue() + "\" este incorect !";
                break;
            case 9:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste \")\" !";
                break;
            case 10:
                message = "Eroare la linia " + this.currentToken.getLine() + ": lipseste ; !";
                break;
        }

        System.out.println(message);
    }


}
