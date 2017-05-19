package util;

import model.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class DataTable {
    public static final String KEY_WORDS  = "key_words";
    public static final String SPECIAL_CHARACTERS  = "special_characters";
    public static final String SPECIAL_CHARACTER_SEQUENCE  = "special_character_sequence";

    private static HashMap<String, ArrayList<TokenAttribute>> map;

    public DataTable() {
        this.initDataTable();
    }

    private void initDataTable(){
        map = new HashMap<>();

        map.put(KEY_WORDS, this.getKeyWords());
        map.put(SPECIAL_CHARACTERS, this.getSpecialCharacters());
        map.put(SPECIAL_CHARACTER_SEQUENCE, this.getSpecialCharacterSequence());
    }

    private  ArrayList<TokenAttribute> getKeyWords(){
        ArrayList<TokenAttribute> keyWords = new ArrayList<TokenAttribute>();
        //DFD
        keyWords.add( new TokenAttribute("if", TokenType.IF));
        keyWords.add(new TokenAttribute("else", TokenType.ELSE));
        keyWords.add( new TokenAttribute("while", TokenType.WHILE));
        keyWords.add(new TokenAttribute("return", TokenType.RETURN));
        keyWords.add(new TokenAttribute("cin", TokenType.CIN));
        keyWords.add(new TokenAttribute("cout", TokenType.COUT));

        //DATA TYPES
        keyWords.add(new TokenAttribute("int", TokenType.INT_DATATYPE));
        keyWords.add(new TokenAttribute("bool", TokenType.BOOL_DATATYPE));
        keyWords.add(new TokenAttribute("float", TokenType.FLOAT_DATATYPE));
        keyWords.add(new TokenAttribute("char", TokenType.CHAR_DATATYPE));
        keyWords.add(new TokenAttribute("string", TokenType.CHAR_DATATYPE));
        keyWords.add(new TokenAttribute("void", TokenType.VOID));

        return keyWords;
    }

    private  ArrayList<TokenAttribute> getSpecialCharacters(){
        ArrayList<TokenAttribute> specialCharacters = new ArrayList<TokenAttribute>();
        specialCharacters .add(new TokenAttribute(",", TokenType.COMMA));
        specialCharacters .add(new TokenAttribute(";", TokenType.SEMICOLON));

        specialCharacters .add(new TokenAttribute("+", TokenType.PLUS_OP));
        specialCharacters .add(new TokenAttribute("-", TokenType.MINUS_OP));
        specialCharacters .add(new TokenAttribute("*", TokenType.MUL_OP));
        specialCharacters .add(new TokenAttribute("-", TokenType.MINUS_OP));
        specialCharacters .add(new TokenAttribute("/", TokenType.DIVIDE_OP));
        specialCharacters .add(new TokenAttribute("!", TokenType.NOT_OP));
        specialCharacters .add(new TokenAttribute("=", TokenType.ASSIGN_OP));
        specialCharacters .add(new TokenAttribute("|", TokenType.OR_OP));
        specialCharacters .add(new TokenAttribute("&", TokenType.AND_OP));


        specialCharacters .add(new TokenAttribute(".", TokenType.DOT));
        specialCharacters .add(new TokenAttribute("<", TokenType.LESS_THAN));
        specialCharacters .add(new TokenAttribute(">", TokenType.GREATER_THAN));



        specialCharacters .add(new TokenAttribute("(", TokenType.L_ROUND_BRACKET));
        specialCharacters .add(new TokenAttribute(")", TokenType.R_ROUND_BRACKET));
        specialCharacters .add(new TokenAttribute("[", TokenType.L_SQ_BRACKET));
        specialCharacters .add(new TokenAttribute("]", TokenType.R_SQ_BRACKET));
        specialCharacters .add(new TokenAttribute("{", TokenType.L_CURLY_BRACKET));
        specialCharacters .add(new TokenAttribute("}", TokenType.R_CURLY_BRACKET));

        return specialCharacters ;
    }

    private  ArrayList<TokenAttribute> getSpecialCharacterSequence(){
        ArrayList<TokenAttribute> specialCharacterSequence = new ArrayList<TokenAttribute>();

        specialCharacterSequence .add( new TokenAttribute("<=", TokenType.LESS_EQ));
        specialCharacterSequence .add( new TokenAttribute(">=", TokenType.GREAT_EQ));
        specialCharacterSequence .add( new TokenAttribute("==", TokenType.EQUAL_OP));
        specialCharacterSequence .add( new TokenAttribute("!=", TokenType.DIFF_OP));
        specialCharacterSequence .add( new TokenAttribute("&&", TokenType.AND_OP));
        specialCharacterSequence .add( new TokenAttribute("||", TokenType.OR_OP));


        return specialCharacterSequence ;
    }

    public boolean attributeIsDefined(String key, String value) {
        ArrayList<TokenAttribute> data = this.getData(key);
        for(TokenAttribute t : data){
            if(t.getValue().equals(value)){
                return true;
            }
        }
        return false;
    }

    public  TokenAttribute getAttribute(String key, String value) {
        ArrayList<TokenAttribute> data = this.getData(key);
        for(TokenAttribute t : data){
            if(t.getValue().equals(value)){
                return t;
            }
        }

        return null;
    }

    /**
     * @param  key key
     * @return a list from Datatable with the given key
     */
    public ArrayList<TokenAttribute> getData(String key) {
        return map.get(key);
    }
}
