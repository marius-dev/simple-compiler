package model;

/**
 *
 */
public class Token {
    private TokenType type;
    private String value;
    private Integer line;

    public Token appendValue(char value) {
        this.value += value;
        return this;
    }

    public TokenType getType() {
        return type;
    }

    public Token setType(TokenType type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Token setValue(String value) {
        this.value = value;
        return this;
    }

    public Integer getLine() {
        return line;
    }

    public Token setLine(Integer line) {
        this.line = line;
        return this;
    }
}
