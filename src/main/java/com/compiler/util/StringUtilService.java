package com.compiler.util;

/**
 * Created by ilies on 16-May-17.
 */
public class StringUtilService {
    public static boolean isAlNum(char ch){
        return Character.isDigit(ch) || Character.isLetter(ch);
    }
}
