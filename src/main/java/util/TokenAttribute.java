package util;

import model.TokenType;

/**
 *
 */
public class TokenAttribute {
    private String value;
    private TokenType tokenType;

    public TokenAttribute(String value, TokenType tokenType) {
        this.value = value;
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}
