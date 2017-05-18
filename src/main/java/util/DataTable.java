package util;

import model.Token;
import model.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ilies on 16-May-17.
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
        map = new HashMap<String, ArrayList<TokenAttribute>>();

        map.put(KEY_WORDS, this.getKeyWords());
        map.put(SPECIAL_CHARACTERS, this.getSpecialCharacters());
        map.put(SPECIAL_CHARACTER_SEQUENCE, this.getSpecialCharacterSequence());
    }

    private  ArrayList<TokenAttribute> getKeyWords(){
        ArrayList<TokenAttribute> keyWords = new ArrayList<TokenAttribute>();
        keyWords.add( new TokenAttribute("if", TokenType.IF));
        keyWords.add(new TokenAttribute("then", TokenType.THEN));
        keyWords.add(new TokenAttribute("else", TokenType.ELSE));
        keyWords.add(new TokenAttribute("begin", TokenType.BEGIN));
        keyWords.add(new TokenAttribute("end", TokenType.END));

        return keyWords;
    }

    private  ArrayList<TokenAttribute> getSpecialCharacters(){
        ArrayList<TokenAttribute> specialCharacters = new ArrayList<TokenAttribute>();
        specialCharacters .add( new TokenAttribute(",", TokenType.COMMA));
        specialCharacters .add(new TokenAttribute("=", TokenType.EQUAL));
        specialCharacters .add(new TokenAttribute(".", TokenType.DOT));
        specialCharacters .add(new TokenAttribute(":", TokenType.DOTS));
        specialCharacters .add(new TokenAttribute("<", TokenType.LESS_THAN));
        specialCharacters .add(new TokenAttribute(">", TokenType.GREATER_THAN));

        specialCharacters .add( new TokenAttribute("+", TokenType.PLUS));
        specialCharacters .add(new TokenAttribute(";", TokenType.SEMI));
        specialCharacters .add(new TokenAttribute("(", TokenType.L_PAREN));
        specialCharacters .add(new TokenAttribute(")", TokenType.R_PAREN));

        return specialCharacters ;
    }

    private  ArrayList<TokenAttribute> getSpecialCharacterSequence(){
        ArrayList<TokenAttribute> specialCharacterSequence = new ArrayList<TokenAttribute>();
        specialCharacterSequence .add( new TokenAttribute(":=", TokenType.DDOTS));
        specialCharacterSequence .add( new TokenAttribute("<=", TokenType.LESS_EQUAL));

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
