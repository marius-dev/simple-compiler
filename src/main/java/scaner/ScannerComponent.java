package scaner;

import model.Token;
import model.TokenType;
import util.DataTable;
import util.StringUtilService;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 */
public class ScannerComponent {

    private ArrayList<Token> tokens;
    private Integer index;
    private long position;
    private Integer line;
    private DataTable dataTable;

    //file handlers
    private FileInputStream file;

    public ScannerComponent(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL fileUri = classLoader.getResource(fileName);

            if (fileUri != null) {
                file = new FileInputStream(fileUri.getPath());

            } else {
                System.exit(0);
            }

            this.dataTable = new DataTable();
            this.line = 1;
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public Token getNextToken() throws IOException {
        boolean bin = true;
        boolean oc = true;
        int he = 0;
        char c;

        //salveaza pozitia curenta din fisier
        position = file.getChannel().position();

        Token token = new Token();

        //elimina spatiile albe

        int bi = ' ';
        char ch = (char) bi;

        do{
            bi = file.read();
            ch = (char) bi;

            if (ch == '\n') {
                line++;
            }

        } while ((ch == '\n' || ch == '\t' || ch == ' ') && ((bi) != -1));


        token.setValue(ch + "");

        if ((bi) == -1) {
            token.setType(TokenType.EOF);
            token.setValue("tfy");
        } else if (Character.isLetter(ch) || ch == '_') {
            bi = file.read();
            ch = (char) bi;

            while (StringUtilService.isAlNum(ch) || ch == '_') {
                token.appendValue(ch);

                bi = file.read();
                ch = (char) bi;
            }

            if (this.getDataTable().attributeIsDefined(DataTable.KEY_WORDS, token.getValue())) {
                token.setType(
                        this.getDataTable().getAttribute(DataTable.KEY_WORDS, token.getValue()).getTokenType()
                );

            } else {
                token.setType(TokenType.ID);
            }

            //this.stepBackWith(-1);
        } else if (Character.isDigit(ch)) {
            c = ch;
            bi = file.read();
            ch = (char) bi;

            if (c == '0' && ch == 'X') {
                token.appendValue(ch);
                bi = file.read();
                ch = (char) bi;

                while (
                        ((bi) != -1) &&
                                (
                                        Character.isDigit(ch) ||
                                                ch == 'A' || ch == 'B' || ch == 'C' || ch == 'D' || ch == 'E' || ch == 'F'
                                )
                        ) {
                    he = 1;
                    token.appendValue(ch);

                    bi = file.read();
                    ch = (char) bi;
                }
                if (he == 1) {
                    token.setType(TokenType.HEX);
                } else {
                    token.setType(TokenType.BAD);
                }
            } else {
                if ((c != '0') && (c != '1')) {
                    bin = false;
                }
                if ((c > '7')) {
                    oc = false;
                }

                while (((bi) != -1) && Character.isDigit(ch)) {
                    if ((ch != '0') && (ch != '1')) {
                        bin = false;
                    }
                    if ((ch > '7')) {
                        oc = false;
                    }

                    token.appendValue(ch);

                    bi = file.read();
                    ch = (char) bi;
                }
                if (bin && ch == 'b') {
                    token.appendValue(ch);
                    token.setType(TokenType.BIN);

                    bi = file.read();
                    ch = (char) bi;

                } else if (oc && ch == 'o') {
                    token.appendValue(ch);
                    token.setType(TokenType.OCT);

                    bi = file.read();
                    ch = (char) bi;

                } else if (ch == '.') {
                    token.appendValue(ch);
                    bi = file.read();
                    ch = (char) bi;

                    if (!Character.isDigit(ch)) {
                        token.setType(TokenType.BAD);
                    }

                    while (Character.isDigit(ch)) {
                        token.appendValue(ch);
                        token.setType(TokenType.REAL);

                        bi = file.read();
                        ch = (char) bi;
                    }

                    if (ch == 'e' || ch == 'E') {
                        token.appendValue(ch);
                        bi = file.read();
                        ch = (char) bi;

                        if (ch == '-' || Character.isDigit(ch)) {
                            token.appendValue(ch);
                            token.setType(TokenType.REAL);

                            bi = file.read();
                            ch = (char) bi;

                            while (Character.isDigit(ch)) {
                                token.appendValue(ch);
                                bi = file.read();
                                ch = (char) bi;
                            }
                        } else {
                            token.setType(TokenType.BAD);
                        }
                    }
                } else {
                    token.setType(TokenType.INT);
                }
            }

            this.stepBackWith(-1);
        } else if (ch == '\"') {
            bi = file.read();
            ch = (char) bi;

            while ((bi != -1) && (ch != '\"')) {
                token.appendValue(ch);
                bi = file.read();
                ch = (char) bi;
            }

            if (token.getValue().contains("\"")) {
                token.appendValue(ch);
                token.setType(TokenType.SIR);
            } else {
                token.setType(TokenType.BAD);
            }
        } else if (ch == '\'') {
            bi = file.read();
            ch = (char) bi;

            if (StringUtilService.isAlNum(ch)) {
                token.appendValue(ch);
            }

            bi = file.read();
            ch = (char) bi;

            if (ch == '\'') {
                token.appendValue(ch);
                token.setType(TokenType.CH_CON);
            } else {
                token.setType(TokenType.BAD);
            }

        } else if (this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTERS, token.getValue())) {

            token.setType(
                    this.getDataTable().getAttribute(DataTable.SPECIAL_CHARACTERS, token.getValue()).getTokenType()
            );

            bi = file.read();
            ch = (char) bi;

            if (this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTER_SEQUENCE, token.getValue() + ch)) {
                token.appendValue(ch);
                token.setType(
                        this.getDataTable().getAttribute(DataTable.SPECIAL_CHARACTER_SEQUENCE, token.getValue()).getTokenType()
                );

            }

            this.stepBackWith(-1);
        } else {
            token.setType(TokenType.BAD);
        }

        token.setLine(line);
        return token;
    }

    public void goToPosition(long pos) throws IOException {
        this.file.getChannel().position(pos);
    }

    public void stepBackWith(int steps) throws IOException {
        long posO = file.getChannel().position();
        file.getChannel().position(posO + steps);
    }

    public void getCurrentPositionInFile() throws IOException {
        long posO = file.getChannel().position();
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public long getLastTokenPosition() {
        return position;
    }

    public Integer getLine() {
        return line;
    }
}
