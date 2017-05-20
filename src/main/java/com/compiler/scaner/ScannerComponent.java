package com.compiler.scaner;

import com.compiler.model.Token;
import com.compiler.model.TokenType;
import com.compiler.util.DataTable;
import com.compiler.util.StringUtilService;

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

        Token token = new Token();

        //elimina spatiile albe
        int bi = -1;
        char chBuffer = (char) bi;

        do {
            bi = file.read();
            chBuffer = (char) bi;

            if (chBuffer == '\n') {
                line++;
            }

        } while ((chBuffer == '\n' || chBuffer == '\t' || chBuffer == ' ') && ((bi) != -1));


        //salveaza pozitia curenta din fisier
        position = file.getChannel().position();


        if(line==5){
            char x =10;
        }

        token.setValue(chBuffer + "");
        if ((bi) == -1) {
            token.setType(TokenType.EOF);
            token.setValue("eof");
        } else if (Character.isLetter(chBuffer) || chBuffer == '_') {
            bi = file.read();
            chBuffer = (char) bi;

            if ((bi) == -1) {
                token.setType(TokenType.EOF);
                token.setValue("eof");
            } else {
                boolean isBadToken = false;

                while(chBuffer != ' ' && bi != -1 && !this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTERS, chBuffer+"")){
                    if(! StringUtilService.isAlNum(chBuffer) && chBuffer != '_'){
                        isBadToken = true;
                    }

                    token.appendValue(chBuffer);
                    bi = file.read();
                    chBuffer = (char) bi;
                }

                if(!isBadToken) {
                    if (this.getDataTable().attributeIsDefined(DataTable.KEY_WORDS, token.getValue())) {
                        token.setType(
                                this.getDataTable().getAttribute(DataTable.KEY_WORDS, token.getValue()).getTokenType()
                        );

                    } else {
                        token.setType(TokenType.ID);
                    }
                }else{
                    token.setType(TokenType.BAD);
                }
            }

            this.skip(-1);
        } else if (Character.isDigit(chBuffer)) {
            c = chBuffer;
            bi = file.read();
            chBuffer = (char) bi;

            if (c == '0' && chBuffer == 'x') {
                token.appendValue(chBuffer);
                bi = file.read();
                chBuffer = (char) bi;

                boolean isBadToken = false;

                while (chBuffer != ' ' && bi !=-1 && !this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTERS, chBuffer+"")){
                    if((!Character.isDigit(chBuffer) && chBuffer != 'A' && chBuffer != 'B' && chBuffer != 'C' && chBuffer != 'D' && chBuffer != 'E' && chBuffer != 'F')){
                        isBadToken = true;
                    }

                    token.appendValue(chBuffer);

                    bi = file.read();
                    chBuffer = (char) bi;
                }

                if (!isBadToken) {
                    token.setType(TokenType.HEX_NUMBER);
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


                while (bi != -1 && chBuffer!= ' ' &&  !this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTERS, chBuffer+"")) {
                    if(!Character.isDigit(chBuffer)){
                        bin = false;
                        oc = false;
                    }
                    if ((chBuffer != '0') && (chBuffer != '1')) {
                        bin = false;
                    }
                    if ((chBuffer > '7')) {
                        oc = false;
                    }

                    token.appendValue(chBuffer);

                    bi = file.read();
                    chBuffer = (char) bi;
                }

                if (bin && chBuffer == 'b') {
                    token.appendValue(chBuffer);
                    token.setType(TokenType.BIN_NUMBER);

                    bi = file.read();
                    chBuffer = (char) bi;

                } else if (oc && chBuffer == 'o') {
                    token.appendValue(chBuffer);
                    token.setType(TokenType.OCT_NUMBER);

                    bi = file.read();
                    chBuffer = (char) bi;

                } else if (chBuffer == '.') {
                    token.appendValue(chBuffer);
                    bi = file.read();
                    chBuffer = (char) bi;

                    if (!Character.isDigit(chBuffer)) {
                        token.setType(TokenType.BAD);
                    }

                    while (Character.isDigit(chBuffer)) {
                        token.appendValue(chBuffer);
                        token.setType(TokenType.REAL_NUMBER);

                        bi = file.read();
                        chBuffer = (char) bi;
                    }

                    if (chBuffer == 'e' || chBuffer == 'E') {
                        token.appendValue(chBuffer);
                        bi = file.read();
                        chBuffer = (char) bi;

                        if (chBuffer == '-' || Character.isDigit(chBuffer)) {
                            token.appendValue(chBuffer);
                            token.setType(TokenType.REAL_NUMBER);

                            bi = file.read();
                            chBuffer = (char) bi;

                            while (Character.isDigit(chBuffer)) {
                                token.appendValue(chBuffer);
                                bi = file.read();
                                chBuffer = (char) bi;
                            }
                        } else {
                            token.setType(TokenType.BAD);
                        }
                    }
                } else {
                    token.setType(TokenType.INT_NUMBER);
                }
            }

            this.skip(-1);
        } else if (chBuffer == '\"') {
            bi = file.read();
            chBuffer = (char) bi;

            while ((bi != -1) && (chBuffer != '\"')) {
                token.appendValue(chBuffer);
                bi = file.read();
                chBuffer = (char) bi;
            }

            if (token.getValue().contains("\"")) {
                token.appendValue(chBuffer);
                token.setType(TokenType.STRING_CONSTANT);
            } else {
                token.setType(TokenType.BAD);
            }
        } else if (chBuffer == '\'') {
            bi = file.read();
            chBuffer = (char) bi;

            if (StringUtilService.isAlNum(chBuffer)) {
                token.appendValue(chBuffer);
            }

            bi = file.read();
            chBuffer = (char) bi;

            if (chBuffer == '\'') {
                token.appendValue(chBuffer);
                token.setType(TokenType.STRING_CONSTANT);
            } else {
                token.setType(TokenType.BAD);
            }

        } else if (this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTERS, token.getValue())) {

            token.setType(
                    this.getDataTable().getAttribute(DataTable.SPECIAL_CHARACTERS, token.getValue()).getTokenType()
            );

            bi = file.read();
            chBuffer = (char) bi;

            if (this.getDataTable().attributeIsDefined(DataTable.SPECIAL_CHARACTER_SEQUENCE, token.getValue() + chBuffer)) {
                token.appendValue(chBuffer);
                token.setType(
                        this.getDataTable().getAttribute(DataTable.SPECIAL_CHARACTER_SEQUENCE, token.getValue()).getTokenType()
                );

                this.skip(1);
            }

            this.skip(-1);
        } else {
            token.setType(TokenType.BAD);
        }

        token.setLine(line);
        return token;
    }

    public void goToPosition(long pos) throws IOException {
        this.file.getChannel().position(pos);
    }

    public void skip(int steps) throws IOException {
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

    public void closeFile(){
        try {
            this.file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
