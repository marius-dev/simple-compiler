package model;

public enum  TokenType {
    ID,

    IF,
    ELSE,
    WHILE,
    CIN,
    COUT,
    READ,
    WRITE,
    RETURN,

    DOT,
    COMMA,
    SEMICOLON,


    BOOL_DATATYPE,
    FLOAT_DATATYPE,
    CHAR_DATATYPE,
    INT_DATATYPE,
    VOID,

    INT_NUMBER,
    BOOL_LITERAL,
    REAL_NUMBER,
    OCT_NUMBER,
    BIN_NUMBER,
    HEX_NUMBER,
    STRING_CONSTANT,

    L_CURLY_BRACKET,
    R_CURLY_BRACKET,
    L_ROUND_BRACKET,
    R_ROUND_BRACKET,
    L_SQ_BRACKET,
    R_SQ_BRACKET,


    LESS_THAN,
    GREATER_THAN,
    GREAT_EQ,
    LESS_EQ,


    TRUE,
    FALSE,


    NOT_OP,
    PLUS_OP,
    MINUS_OP,
    MUL_OP,
    DIVIDE_OP,
    AND_OP,
    OR_OP,
    EQUAL_OP,
    DIFF_OP,
    ASSIGN_OP,

    BAD,
    EOF,
}
