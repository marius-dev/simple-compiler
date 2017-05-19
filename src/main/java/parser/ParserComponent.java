package parser;

import model.*;
import scaner.ScannerComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static model.TokenType.*;

/**
 *
 */
public class ParserComponent {

    private ScannerComponent scanner;
    private Token currentToken;
    private ArrayList<String> errorMessages;

    private AbstractSyntaxTree ar;
    private SemanticTable tSym;
    private Node currentNode;
    private ListNode currentListNode;
    private SemanticLine newLine;

    public ParserComponent(ScannerComponent scanner) {
        this.scanner = scanner;
        this.errorMessages = new ArrayList<>();

    }


    private void generate(String n, String type, String val, int number) {
        Node k, p;
        k = new Node(n, type, val);

        if (ar.root == null) {
            ar.root = k;
            ar.root.level = 0;
            currentNode = k;
        } else {
            if (number == 0) {
                if (currentNode.down == null) {
                    k.up = currentNode;
                    currentNode.down = k;
                    currentNode = k;
                } else {
                    k.up = currentNode;
                    p = currentNode.down;
                    while (p.right != null)
                        p = p.right;
                    k.left = p;
                    p.right = k;
                    currentNode = k;
                }
            } else if (number == 1) {
                k.up = currentNode;
                currentNode.down = k;
            } else if (number == 2) {
                k.up = currentNode;
                p = currentNode.down;
                while (p.right != null)
                    p = p.right;
                k.left = p;
                p.right = k;
            }
        }
    }

    private void generateTable() {
        ListNode k;
        if (tSym.first == null) {
            k = new ListNode();
            k.line = newLine;
            k.next = null;
            tSym.first = k;
            //newLine = tSym.first;
        } else {
            currentListNode = tSym.first;
            while (currentListNode.line.name.compareTo(newLine.name) != 0) {//todo check strcmp
                if (currentListNode.next == null) {
                    break;
                }
                currentListNode = currentListNode.next;
            }

            if (currentListNode.line.name.compareTo(newLine.name) == 0) {
                smbTableError(0);
            } else {
                k = new ListNode();
                k.line = newLine;
                k.next = null;
                currentListNode = k;
                //			cu_l=cu_l->next;
            }
        }
    }

    private void search() {
        currentListNode = tSym.first;
        while (currentListNode.line.name.compareTo(newLine.name) != 0) {
            if (currentListNode.next == null)
                break;
            currentListNode = currentListNode.next;
        }
        if ((currentListNode.line.name.compareTo(newLine.name) != 0) || !Objects.equals(currentListNode.line.size, newLine.size)) {
            smbTableError(1);
        }
    }

    private void smbTableError(int i) {
        String message = "";

        if (i == 0) {
            message = "\nEroare de semantica: redeclararea variabilei/functiei " + newLine.name;
        } else if (i == 1) {
            message = "\nEroare de semantica: variabila/functia " + newLine.name + " a fost folosita fara a fi declarata\n";
        }

        this.errorMessages.add(message);
    }


    public void parse() {

        currentToken = this.getNextToken();

        while (this.isDataType(currentToken)) {

            match(currentToken.getType());
            newLine.dataType = currentToken.getValue();

            id();
            currentToken = this.getNextToken();

            if ((currentToken.getType() == TokenType.SEMICOLON) || ((currentToken.getType() == TokenType.L_SQ_BRACKET))) {
                variableDeclaration();
            } else if (currentToken.getType() == TokenType.L_ROUND_BRACKET) {
                functionDeclaration();
            } else {
                syntax_error(currentToken, "variable Declaration or function Declaration");
            }

            currentToken = this.getNextToken();
        }

        if (currentToken.getType() == TokenType.EOF) {
            syntax_error(currentToken, "end of file");
        } else {
            match(EOF);
        }
    }

    private void variableDeclaration() {

        match(currentToken.getType());

        if (currentToken.getType() == L_SQ_BRACKET) {

            currentToken = this.getNextToken();

            if (this.isNumber(currentToken)) {

                match(currentToken.getType());
                newLine.size = currentToken.getValue();

                currentToken = this.getNextToken();
                if (currentToken.getType() == R_SQ_BRACKET) {

                    match(R_SQ_BRACKET);
                    currentToken = this.getNextToken();

                    if (currentToken.getType() == SEMICOLON) {
                        match(SEMICOLON);
                        generate("variableDeclaration", "TEMP", "", 0);
                        generate("type", "TYPE", newLine.dataType, 1);
                        generate("ID", "ID", newLine.name, 2);
                        generate("SIZE", "INTLIT", newLine.size, 2);
                        currentNode = currentNode.up;
                        generateTable();
                    } else {
                        syntax_error(currentToken, "SEMICOL");
                    }
                } else {
                    syntax_error(currentToken, "RBRK");
                }
            } else {
                syntax_error(currentToken, "INTLITERAL");
            }
        } else {
            generate("variableDeclaration", "TEMP", "", 0);
            generate("type", "TYPE", newLine.dataType, 1);
            generate("ID", "ID", newLine.name, 2);
            newLine.size = "";
            currentNode = currentNode.up;
            generateTable();
        }
    }

    private void functionDeclaration() {
        generate("functionDeclaration", "TEMP", "", 0);
        generate("type", "TYPE", newLine.dataType, 1);
        generate("ID", "ID", newLine.name, 2);
        newLine.size = "";

        generateTable();
        parameters();
        block();

        currentNode = currentNode.up;
    }

    private void parameters() {
        match(L_ROUND_BRACKET);
        currentToken = this.getNextToken();

        if (currentToken.getType() == TokenType.R_ROUND_BRACKET)
            match(TokenType.R_ROUND_BRACKET);

        else if (this.isDataType(currentToken)) {

            generate("parameters", "TEMP", "", 0);
            formalsList();
            currentNode = currentNode.up;

        } else {
            syntax_error(currentToken, "RPAR or formalsList");
        }
    }

    private void formalsList() {

        formalDecl();
        currentToken = this.getNextToken();

        while (currentToken.getType() == COMMA) {
            match(COMMA);
            currentToken = this.getNextToken();
            if (this.isDataType(currentToken)) {
                formalDecl();
            } else {
                syntax_error(currentToken, "type");
            }
        }
    }

    private void formalDecl() {
        match(currentToken.getType());

        newLine.dataType = currentToken.getValue();
        id();
        generate("formalDecl", "TEMP", "", 0);
        generate("type", "TYPE", newLine.dataType, 1);
        generate("ID", "ID", newLine.dataType, 2);
        newLine.size = "";
        generateTable();
        currentNode = currentNode.up;
    }

    private void block() {

        generate("block", "TEMP", "", 0);
        currentToken = this.getNextToken();

        if (currentToken.getType() == TokenType.L_CURLY_BRACKET) {
            match(TokenType.L_CURLY_BRACKET);
            currentToken = declList();
            currentToken = stmtList();
            //		tok=scanner();
            if (currentToken.getType() == TokenType.R_CURLY_BRACKET) {
                match(R_CURLY_BRACKET);
            } else {
                syntax_error(currentToken, "R_CURLY_BRACKET");
            }
        } else {
            syntax_error(currentToken, "L_CURLY_BRACKET");
        }

        currentNode = currentNode.up;
    }

    private boolean opc() {
        if (
                (currentToken.getType() == TokenType.EQUAL_OP) ||
                        (currentToken.getType() == TokenType.DIFF_OP) ||
                        (currentToken.getType() == TokenType.LESS_THAN) ||
                        (currentToken.getType() == TokenType.GREATER_THAN) ||
                        (currentToken.getType() == TokenType.LESS_EQ) ||
                        (currentToken.getType() == TokenType.GREAT_EQ)
                ) {
            switch (currentToken.getType()) {
                case EQUAL_OP:
                    rotate("EQUAL_OP");
                    break;
                case DIFF_OP:
                    rotate("DIFF_OP");
                    break;
                case LESS_THAN:
                    rotate("LESS");
                    break;
                case GREATER_THAN:
                    rotate("GREAT");
                    break;
                case LESS_EQ:
                    rotate("LESS_EQ");
                    break;
                case GREAT_EQ:
                    rotate("GREAT_EQ");
                    break;
            }
            match(currentToken.getType());
            return true;
        }
        return false;
    }

    private Token stmtList() {
        while (
                (currentToken.getType() == CIN) ||
                        (currentToken.getType() == COUT) ||
                        (currentToken.getType() == ID) ||
                        (currentToken.getType() == IF) ||
                        (currentToken.getType() == WHILE) ||
                        (currentToken.getType() == RETURN)
                ) {
            generate("stmt", "TEMP", "", 0);
            currentToken = stmt();
            //		tok=scanner();
            currentNode = currentNode.up;
        }

        return currentToken;
    }

    private Token declList() {

        currentToken = this.getNextToken();
        while (this.isDataType(currentToken)) {
            match(currentToken.getType());

            newLine.dataType = currentToken.getValue();

            id();

            currentToken = this.getNextToken();
            if ((currentToken.getType() == SEMICOLON) || (currentToken.getType() == L_SQ_BRACKET)) {
                variableDeclaration();
            } else {
                syntax_error(currentToken, "SEMICOL or LBRK");
            }
            currentToken = this.getNextToken();
        }

        return currentToken;
    }

    private void functionCallStm() {
        currentToken = this.getNextToken();
        if (currentToken.getType() == TokenType.R_ROUND_BRACKET)
            match(TokenType.R_ROUND_BRACKET);
        else {
            currentToken = actualList();
            //		tok=scanner();
            if (currentToken.getType() == TokenType.R_ROUND_BRACKET) {
                match(TokenType.R_ROUND_BRACKET);
            } else {
                syntax_error(currentToken, "RPAR");
            }
        }
    }

    private Token stmt() {
        match(currentToken.getType());
        if (currentToken.getType() == CIN) {
            currentToken = this.getNextToken();
            if (currentToken.getType() == READ) {
                match(READ);
                id();
                currentToken = this.getNextToken();
                if (currentToken.getType() == SEMICOLON) {
                    generate("CIN", "CIN", "", 1);
                    generate("READ", "READ", "", 2);
                    generate("ID", "ID", newLine.name, 2);
                    newLine.size = "";
                    search();
                    match(SEMICOLON);
                } else if (currentToken.getType() == L_SQ_BRACKET) {
                    generate("CIN", "CIN", "", 1);
                    generate("READ", "READ", "", 2);
                    generate("ID", "ID", newLine.name, 2);
                    generate("exp", "TEMP", "", 0);
                    search();
                    match(L_SQ_BRACKET);
                    currentToken = this.getNextToken();
                    currentToken = exp();
                    currentNode = currentNode.up;
                    if (currentToken.getType() == R_SQ_BRACKET) {
                        match(R_SQ_BRACKET);
                        currentToken = this.getNextToken();
                        if (currentToken.getType() == SEMICOLON) {
                            match(SEMICOLON);
                        } else {
                            syntax_error(currentToken, "SEMICOL");
                        }
                    } else {
                        syntax_error(currentToken, "RBRK");
                    }
                } else {
                    syntax_error(currentToken, "SEMICOL or LBRK");
                }
            } else {
                syntax_error(currentToken, "READ");
            }
        } else if (currentToken.getType() == COUT) {

            currentToken = this.getNextToken();

            if (currentToken.getType() == WRITE) {
                match(WRITE);
                generate("COUT", "COUT", "", 1);
                generate("WRITE", "WRITE", "", 2);
                generate("exp", "TEMP", "", 0);

                currentToken = this.getNextToken();
                currentToken = exp();
                currentNode = currentNode.up;

                if (currentToken.getType() == SEMICOLON) {
                    match(SEMICOLON);
                } else {
                    syntax_error(currentToken, "SEMICOL");
                }
            } else {
                syntax_error(currentToken, "WRITE");
            }
        } else if (currentToken.getType() == ID) {
            match(ID);

            newLine.name = currentToken.getValue();
            search();
            currentToken = this.getNextToken();

            if (currentToken.getType() == TokenType.ASSIGN_OP) {
                match(TokenType.ASSIGN_OP);
                generate("ASSIGN", "ASSIGN", "", 0);
                generate("ID", "ID", newLine.name, 1);
                generate("exp", "TEMP", "", 0);

                currentToken = this.getNextToken();
                currentToken = exp();
                currentNode = currentNode.up;
                currentNode = currentNode.up;

                if (currentToken.getType() == SEMICOLON)
                    match(SEMICOLON);
                else
                    syntax_error(currentToken, "SEMICOL");
            } else if (currentToken.getType() == L_ROUND_BRACKET) {
                match(L_ROUND_BRACKET);
                generate("ID", "ID", newLine.name, 1);
                generate("functionCallStm", "TEMP", "", 0);
                functionCallStm();
                currentNode = currentNode.up;
                currentToken = this.getNextToken();
                if (currentToken.getType() == SEMICOLON) {
                    match(SEMICOLON);
                } else {
                    syntax_error(currentToken, "SEMICOL");
                }
            } else if (currentToken.getType() == L_SQ_BRACKET) {
                match(L_SQ_BRACKET);
                generate("ASSIGN", "ASSIGN", "", 0);
                generate("subscriptExpr", "TEMP", "", 0);
                generate("ID", "ID", newLine.name, 1);
                subscriptExpr();
                currentNode = currentNode.up;
                currentToken = this.getNextToken();

                if (currentToken.getType() == TokenType.ASSIGN_OP) {
                    match(TokenType.ASSIGN_OP);
                    currentToken = this.getNextToken();
                    currentToken = exp();
                    generate("exp", "TEMP", "", 0);
                    currentNode = currentNode.up;
                    currentNode = currentNode.up;
                    if (currentToken.getType() == SEMICOLON) {
                        match(SEMICOLON);
                    } else {
                        syntax_error(currentToken, "SEMICOL");
                    }
                } else {
                    syntax_error(currentToken, "ASSIGN_OP");
                }
            } else {
                syntax_error(currentToken, "ASSIGN_OP or LPAR or LBRK");
            }
        } else if (currentToken.getType() == IF) {

            currentToken = this.getNextToken();

            if (currentToken.getType() == L_ROUND_BRACKET) {
                match(L_ROUND_BRACKET);
                generate("IF", "IF", "", 1);
                generate("exp", "TEMP", "", 0);
                currentToken = this.getNextToken();
                currentToken = exp();
                currentNode = currentNode.up;
                if (currentToken.getType() == R_ROUND_BRACKET) {
                    match(R_ROUND_BRACKET);
                    block();
                    currentToken = this.getNextToken();
                    if (currentToken.getType() == ELSE) {
                        match(ELSE);
                        generate("ELSE", "ELSE", "", 2);
                        block();
                    } else
                        return currentToken;
                } else
                    syntax_error(currentToken, "RPAR");
            } else
                syntax_error(currentToken, "LPAR");
        } else if (currentToken.getType() == WHILE) {
            currentToken = this.getNextToken();
            if (currentToken.getType() == L_ROUND_BRACKET) {
                match(L_ROUND_BRACKET);
                generate("WHILE", "WHILE", "", 1);
                generate("exp", "TEMP", "", 0);
                currentToken = this.getNextToken();
                currentToken = exp();
                currentNode = currentNode.up;
                if (currentToken.getType() == R_ROUND_BRACKET) {
                    match(R_ROUND_BRACKET);
                    block();
                } else
                    syntax_error(currentToken, "RPAR");
            } else
                syntax_error(currentToken, "LPAR");
        } else if (currentToken.getType() == RETURN) {
            generate("RETURN", "TEMP", "", 1);
            currentToken = this.getNextToken();
            if (currentToken.getType() == SEMICOLON) {
                generate("VOID", "TYPE", "VOID", 1);
                match(SEMICOLON);
            } else {
                generate("exp", "TEMP", "", 0);
                currentToken = exp();
                currentNode = currentNode.up;
                if (currentToken.getType() == SEMICOLON)
                    match(SEMICOLON);
                else
                    syntax_error(currentToken, "SEMICOL");
            }
        }
        currentToken = this.getNextToken();
        return currentToken;
    }

    private Token compop() {
        generate("addop", "TEMP", "", 0);
        currentToken = addop();
        currentNode = currentNode.up;

        while (opa()) {
            currentToken = this.getNextToken();
            generate("addop", "TEMP", "", 0);
            currentToken = addop();
            currentNode = currentNode.up;
            currentNode = currentNode.up;
        }

        return currentToken;
    }

    private void rotate(String op) {
        Node nn;
        nn = currentNode.down;

        while (nn.right != null)
            nn = nn.right;
        if (nn.left != null) {
            nn.left.right = null;
            nn.left = null;
            generate(op, op, "", 0);
            currentNode.down = nn;
            nn.up = currentNode;
            //		curent=curent->u;
        } else {
            currentNode.down = null;
            generate(op, op, "", 0);
            currentNode.down = nn;
            nn.up = currentNode;
            //		curent=curent->u;
        }
    }

    private Token exp() {
        if (
                (currentToken.getType() == NOT_OP) ||
                        (currentToken.getType() == MINUS_OP) ||
                        (currentToken.getType() == L_ROUND_BRACKET) ||
                        (currentToken.getType() == ID) ||
                        this.isNumber(currentToken) ||
                        (currentToken.getType() == STRING_CONSTANT) ||
                        (currentToken.getType() == TRUE) ||
                        (currentToken.getType() == FALSE)
                ) {
            generate("orop", "TEMP", "", 0);
            currentToken = orop();
            currentNode = currentNode.up;

            while (currentToken.getType() == OR_OP) {
                rotate("OR_OP");
                currentToken = this.getNextToken();
                generate("orop", "TEMP", "", 0);
                currentToken = orop();
                currentNode = currentNode.up;
                currentNode = currentNode.up;
            }
        } else {
            syntax_error(currentToken, "exp");
        }

        return currentToken;
    }

    private Token orop() {
        generate("andop", "TEMP", "", 0);
        currentToken = andop();
        currentNode = currentNode.up;
        while (currentToken.getType() == AND_OP) {
            rotate("AND_OP");
            currentToken = this.getNextToken();
            generate("andop", "TEMP", "", 0);
            currentToken = andop();
            currentNode = currentNode.up;
            currentNode = currentNode.up;
        }

        return currentToken;
    }

    private Token andop() {
        generate("compop", "TEMP", "", 0);
        currentToken = compop();
        currentNode = currentNode.up;

        if (opc()) {
            currentToken = this.getNextToken();
            generate("compop", "TEMP", "", 0);
            currentToken = compop();
            currentNode = currentNode.up;
            currentNode = currentNode.up;
        }

        return currentToken;
    }

    private boolean opa() {
        if ((currentToken.getType() == PLUS_OP) || (currentToken.getType() == MINUS_OP)) {
            match(currentToken.getType());
            if (currentToken.getType() == PLUS_OP)
                rotate("PLUSOP");
            else
                rotate("MINUS_OP");
            return true;
        }
        return false;
    }

    private Token addop() {
        generate("operand", "TEMP", "", 0);
        currentToken = operand();
        currentNode = currentNode.up;
        while (opm()) {
            currentToken = this.getNextToken();
            generate("operand", "TEMP", "", 0);
            currentToken = operand();
            currentNode = currentNode.up;
            currentNode = currentNode.up;
        }

        return currentToken;
    }

    private Boolean opm() {
        if ((currentToken.getType() == MUL_OP) || (currentToken.getType() == DIVIDE_OP)) {
            match(currentToken.getType());
            if (currentToken.getType() == MUL_OP)
                rotate("MUL_OP");
            else
                rotate("DIVIDE_OP");
            return true;
        }
        return false;
    }

    private Token operand() {
        if (
                (currentToken.getType() == NOT_OP) ||
                        (currentToken.getType() == MINUS_OP) ||
                        (currentToken.getType() == L_ROUND_BRACKET) ||
                        (currentToken.getType() == ID) ||
                        (currentToken.getType() == INT_DATATYPE) ||
                        (currentToken.getType() == STRING_CONSTANT) ||
                        (currentToken.getType() == TRUE) ||
                        (currentToken.getType() == FALSE)
                ) {
            switch (currentToken.getType()) {
                case NOT_OP:
                    match(NOT_OP);
                    generate("NOT_OP", "NOT_OP", "", 0);
                    currentToken = this.getNextToken();
                    generate("exp", "TEMP", "", 0);
                    currentToken = exp();
                    currentNode = currentNode.up;
                    currentNode = currentNode.up;
                    break;
                case MINUS_OP:
                    match(MINUS_OP);
                    generate("MINUS_OP", "MINUS_OP", "", 0);
                    currentToken = this.getNextToken();
                    if ((currentToken.getType() == L_ROUND_BRACKET) || (currentToken.getType() == ID) || (currentToken.getType() == INT_DATATYPE) || (currentToken.getType() == STRING_CONSTANT) || (currentToken.getType() == TRUE) || (currentToken.getType() == FALSE)) {
                        match(currentToken.getType());
                        generate("atom", "TEMP", "", 0);
                        currentToken = atom();
                        currentNode = currentNode.up;
                        currentNode = currentNode.up;
                    } else
                        syntax_error(currentToken, "atom");
                    break;
                default:
                    match(currentToken.getType());
                    generate("atom", "TEMP", "", 0);
                    currentToken = atom();
                    currentNode = currentNode.up;
                    break;
            }
        } else {
            syntax_error(currentToken, "operand");
            currentToken = this.getNextToken();
        }

        return currentToken;
    }

    private Token atom() {

        switch (currentToken.getType()) {
            case L_ROUND_BRACKET:
                currentToken = this.getNextToken();
                generate("exp", "TEMP", "", 0);
                currentToken = exp();
                currentNode = currentNode.up;

                if (currentToken.getType() == R_ROUND_BRACKET) {
                    match(R_ROUND_BRACKET);
                    currentToken = this.getNextToken();
                    return currentToken;
                } else {
                    syntax_error(currentToken, "RPAR");
                    return currentToken;
                }
            case ID:
                newLine.name = currentToken.getValue();
                search();
                generate("ID", "ID", newLine.name, 1);
                currentToken = this.getNextToken();

                if (currentToken.getType() == L_ROUND_BRACKET) {
                    match(L_ROUND_BRACKET);
                    generate("functionCallStm", "TEMP", "", 0);
                    functionCallStm();
                    currentNode = currentNode.up;
                } else if (currentToken.getType() == L_SQ_BRACKET) {
                    match(L_SQ_BRACKET);
                    generate("subscriptExpr", "TEMP", "", 0);
                    subscriptExpr();
                    currentNode = currentNode.up;
                } else
                    return currentToken;
                break;
            default: {
                newLine.name = currentToken.getValue();
                switch (currentToken.getType()) {
                    case INT_DATATYPE://TODO
                        generate("INTLITERAL", "INTLITERAL", newLine.name, 1);
                        break;
                    case STRING_CONSTANT:
                        generate("STRLITERAL", "STRLITERAL", newLine.name, 1);
                        break;
                    case TRUE:
                        generate("TRUE", "TRUE", "TRUE", 1);
                        break;
                    case FALSE:
                        generate("FALSE", "FALSE", "FALSE", 1);
                        break;
                }
            }
            break;
        }

        currentToken = this.getNextToken();

        return currentToken;
    }

    private Token actualList() {
        currentToken = this.getNextToken();
        generate("exp", "TEMP", "", 0);
        currentToken = exp();
        currentNode = currentNode.up;
        while (currentToken.getType() == TokenType.COMMA) {
            match(COMMA);
            generate("exp", "TEMP", "", 0);
            currentToken = this.getNextToken();
            currentToken = exp();
            currentNode = currentNode.up;
        }

        return currentToken;
    }

    private void subscriptExpr() {
        generate("exp", "TEMP", "", 0);
        currentToken = this.getNextToken();
        currentToken = exp();
        currentNode = currentNode.up;

        if (currentToken.getType() == TokenType.R_SQ_BRACKET)
            match(TokenType.R_SQ_BRACKET);
        else
            syntax_error(currentToken, "RBRK");
    }

    private void match(TokenType tk) {
    }

    private void id() {
        currentToken = this.getNextToken();
        if (currentToken.getType() != TokenType.ID) {
            syntax_error(currentToken, "ID");
        } else {
            match(TokenType.ID);
            newLine.name = currentToken.getValue();
        }
    }

    private void syntax_error(Token tk, String val) {
        errorMessages.add("\nEroare de sintaxa. Se astepta token-ul(sau un token al non-terminalului) " + val + " in locul token-ului" + tk.getValue() + "\n");
    }

    private Token getNextToken() {
        Token token = new Token();
        try {
            token = scanner.getNextToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (token.getType() == BAD) {
            this.errorMessages.add("Bad token error on line: " + token.getLine() + " Unknown token: '" + token.getValue() + "'");
        }

        return token;
    }

    private boolean isNumber(Token token) {

        return (
                token.getType() == BIN_NUMBER ||
                        token.getType() == HEX_NUMBER ||
                        token.getType() == OCT_NUMBER ||
                        token.getType() == REAL_NUMBER ||
                        token.getType() == INT_NUMBER

        );
    }

    private boolean isDataType(Token token) {

        return (
                (currentToken.getType() == TokenType.INT_DATATYPE) ||
                (currentToken.getType() == TokenType.BOOL_DATATYPE) ||
                (currentToken.getType() == TokenType.FLOAT_DATATYPE) ||
                (currentToken.getType() == TokenType.CHAR_DATATYPE) ||
                (currentToken.getType() == TokenType.VOID)
        );
    }

    public ArrayList<String> getErrorMessages() {
        return errorMessages;
    }
}
